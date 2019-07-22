package br.com.comexport.filesystem.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ftpserver.ftplet.FtpFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PathToFtpFileMapper implements Converter<Path, FtpFile>{

    private final FileSystem fileSystem;
    
    public PathToFtpFileMapper(@Autowired final FileSystem fileSystem) {
        
        this.fileSystem = fileSystem;
    }
    
    
    @Override
    public FtpFile convert(final Path source) {
        
        return new FtpFile() {

            private final AtomicLong time = new AtomicLong(System.currentTimeMillis());
            
            private Path getPath() {
                
                return source;
            }

            @Override
            public boolean setLastModified(final long time) {
                
                if (this.time.get() < time) {
                    this.time.set(time);
                    return true;
                }

                return false;
            }

            @Override
            public boolean move(final FtpFile destination) {
                
                final Path from = getPath();
                
                final Path destinationPath = fileSystem.getPath(destination.getAbsolutePath());
                
                try {
                    
                    return Files.move(from, destinationPath) != null;
                    
                } catch (IOException e) {
                    
                    return false;
                }
            }

            @Override
            public boolean mkdir() {

                try {
                    
                    return Files.createDirectories(getPath()) != null;
                    
                } catch (IOException e) {
                    
                    return false;
                }
            }

            @Override
            public List<? extends FtpFile> listFiles() {

                try (final Stream<Path> stream = Files.list(getPath())) {

                    return stream
                            .map(PathToFtpFileMapper.this::convert)
                            .collect(Collectors.toList());
                    
                } catch (IOException e) {
                    
                    return Collections.emptyList();
                }
            }

            @Override
            public boolean isWritable() {

                return true;
            }

            @Override
            public boolean isRemovable() {

                return true;
            }

            @Override
            public boolean isReadable() {

                return Files.isReadable(getPath());
            }

            @Override
            public boolean isHidden() {

                try {
                    return Files.isHidden(getPath());
                } catch (IOException e) {
                   return false;
                }
            }

            @Override
            public boolean isFile() {

                return !Files.isDirectory(getPath());
            }

            @Override
            public boolean isDirectory() {

                return Files.isDirectory(getPath());
            }

            @Override
            public long getSize() {

                try {
                    
                    return Files.size(getPath());
                } catch (IOException e) {
                    
                   return -1L;
                }
            }

            @Override
            public Object getPhysicalFile() {
                final long size = getSize();
                
                final byte[] buffer = new byte[(int) size];
                
                try(final InputStream inputStream = Files.newInputStream(getPath())) {
                    inputStream.read(buffer);
                    return buffer;
                } catch (IOException e) {
                   return new byte[0];
                }
            }

            @Override
            public String getOwnerName() {

                try {
                    return Files.getOwner(getPath()).getName();
                } catch (IOException e) {
                    return "";
                }
            }

            @Override
            public String getName() {

                return getPath().getFileName().toString();
            }

            @Override
            public int getLinkCount() {

                return getPath().getNameCount();
            }

            @Override
            public long getLastModified() {
                return time.get();
            }

            @Override
            public String getGroupName() {

                return "";
            }

            @Override
            public String getAbsolutePath() {

                return getPath().toAbsolutePath().toString();
            }

            @Override
            public boolean doesExist() {

                return Files.exists(getPath());
            }

            @Override
            public boolean delete() {

                try {
                    return Files.deleteIfExists(getPath());
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public OutputStream createOutputStream(final long offset) throws IOException {

                return Files.newOutputStream(getPath());
            }

            @Override
            public InputStream createInputStream(final long offset) throws IOException {

                return Files.newInputStream(getPath());
            }
        };
    }
}
