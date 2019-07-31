package br.com.cafebinario.filesystem.configurations;

import java.io.File;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.jimfs.Jimfs;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import br.com.cafebinario.filesystem.mappers.PathToFtpFileMapper;
import br.com.cafebinario.filesystem.watchers.FileWatcher;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FileSystemConfiguration {

    private static final String DEFAULT_ADMIN_SETTINGS = "admin";
    private static final String HAZEL_FS = "hazelfs";
    private static final String HAZEL_ROOTS = "hazelRoots";
    
    @Bean
    public UserManager userManager() throws FtpException {
        
        final PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        
        final UserManager um = userManagerFactory.createUserManager();
        
        final BaseUser user = new BaseUser();

        userManagerFactory.setFile(new File("admin.properties"));
        
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());

        user.setName(DEFAULT_ADMIN_SETTINGS);
        
        user.setPassword(DEFAULT_ADMIN_SETTINGS);
        
        user.setHomeDirectory(DEFAULT_ADMIN_SETTINGS);
        
        um.save(user);

        return um;
    }

    @Bean
    public ListenerFactory listenerFactory(@Value("${ftpServer.port:6921}") final Integer ftpPort) throws UnknownHostException {
        
        final ListenerFactory listenerFactory = new ListenerFactory();
        
        listenerFactory.setServerAddress(Inet4Address.getLocalHost().getHostAddress());
        
        listenerFactory.setPort(ftpPort);
        
        
        return listenerFactory;
    }

    @Bean(initMethod="start", destroyMethod = "stop")
    public FtpServer ftpServer(
            @Autowired final UserManager userManager,
            @Autowired final ListenerFactory listenerFactory,
            @Autowired final FileSystemFactory fileSystemFactory)
            throws FtpException {

        final FtpServerFactory serverFactory = new FtpServerFactory();

        serverFactory.addListener("default", listenerFactory.createListener());

        serverFactory.setFileSystem(fileSystemFactory);

        serverFactory.setUserManager(userManager);

        final CommandFactory commandFactory = serverFactory.getCommandFactory();
        
        serverFactory.setCommandFactory(commandFactory);

        return serverFactory.createServer();
    }

    @Bean
    public Config config() {

        return new Config(HAZEL_FS).addMapConfig(mapConfig());
    }

    private MapConfig mapConfig() {

        return new MapConfig(HAZEL_ROOTS);
    }

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstance(@Autowired final Config config) {

        return Hazelcast.getOrCreateHazelcastInstance(config);
    }

    @Bean
    public FileSystemFactory fileSystemFactory(
    		@Autowired final FileSystem fileSystem,
            @Autowired final PathToFtpFileMapper pathToFtpFileMapper) {

        return newFileSystemFactory(fileSystem, pathToFtpFileMapper);
    }

	private FileSystemFactory newFileSystemFactory(final FileSystem fileSystem,
			final PathToFtpFileMapper pathToFtpFileMapper) {
		
		return user -> new FileSystemView() {

            @Override
            public boolean isRandomAccessible() throws FtpException {

                return fileSystem.isOpen();
            }

            @Override
            public FtpFile getWorkingDirectory() throws FtpException {

                return pathToFtpFileMapper.convert(fileSystem.getPath(user.getHomeDirectory()));
            }

            @Override
            public FtpFile getHomeDirectory() throws FtpException {

                return pathToFtpFileMapper.convert(fileSystem.getPath(user.getHomeDirectory()));
            }

            @Override
            public FtpFile getFile(final String file) throws FtpException {

                return pathToFtpFileMapper.convert(fileSystem.getPath(file));
            }

            @Override
            public void dispose() {

                log.info("m=dispose, user={}", user);
            }

            @Override
            public boolean changeWorkingDirectory(final String dir) throws FtpException {

                return false;
            }
        };
	}

    @Bean(destroyMethod="close")
    public FileSystem fileSystem(@Autowired final HazelcastInstance hazelcastInstance) {

    	try {
    		
    		return FileSystems.getFileSystem(URI.create("jimfs://hazelfs"));
    	}catch (Exception e) {
    		
    		return Jimfs.newFileSystem(HAZEL_FS, com.google.common.jimfs.Configuration.unix(),
                    hazelcastInstance.getMap(HAZEL_ROOTS));
		}
        
    }
    
    @Bean(initMethod="start", destroyMethod="close")
    public FileWatcher fileWatcher(@Autowired final FileSystem fileSystem) {
    	
    	return FileWatcher.of(fileSystem);
    }
}
