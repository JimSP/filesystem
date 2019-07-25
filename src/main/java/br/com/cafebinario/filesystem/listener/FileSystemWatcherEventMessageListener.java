package br.com.cafebinario.filesystem.listener;

import static br.com.cafebinario.filesystem.functions.Retry.retry;

import org.springframework.stereotype.Component;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import br.com.cafebinario.filesystem.dtos.NotifyDTO;
import br.com.cafebinario.filesystem.functions.HttpPutNotifySender;
import br.com.cafebinario.logger.Log;
import br.com.cafebinario.logger.LogLevel;
import br.com.cafebinario.logger.VerboseMode;

@Component
public class FileSystemWatcherEventMessageListener implements MessageListener<NotifyDTO> {

    @Override
    @Log
    public void onMessage(final Message<NotifyDTO> message) {
        final NotifyDTO notifyDTO = message.getMessageObject();
        final boolean accept =retry(content->HttpPutNotifySender.sendHttpRequest(content, notifyDTO.getUrl()), notifyDTO, 3);
        
        if(!accept) {
        	sendHttpRequestForceLogVerboseModeON(notifyDTO);
        }
    }

    @Log(verboseMode=VerboseMode.ON, logLevel=LogLevel.ERROR)
    public boolean sendHttpRequestForceLogVerboseModeON(final NotifyDTO notifyDTO) {
		return false;
	}
}
