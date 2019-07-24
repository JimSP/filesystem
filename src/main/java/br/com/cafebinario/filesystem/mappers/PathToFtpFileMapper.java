package br.com.cafebinario.filesystem.mappers;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.apache.ftpserver.ftplet.FtpFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.cafebinario.filesystem.ftp.FtpFileCreator;

@Component
public class PathToFtpFileMapper implements Converter<Path, FtpFile>{

    private final FileSystem fileSystem;
    
    public PathToFtpFileMapper(@Autowired final FileSystem fileSystem) {
        
        this.fileSystem = fileSystem;
    }
    
    
    @Override
    public FtpFile convert(final Path source) {
        
        return FtpFileCreator.create(source, fileSystem, this);
    }
}
