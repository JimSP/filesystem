package br.com.cafebinario.filesystem.listener;

import static br.com.cafebinario.filesystem.functions.Retry.retry;

import org.springframework.stereotype.Component;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import br.com.cafebinario.filesystem.dto.NotifyDTO;
import br.com.cafebinario.filesystem.functions.HttpPutNotifySender;

@Component
public class FileSystemWatcherEventMessageListener implements MessageListener<NotifyDTO> {

    @Override
    public void onMessage(final Message<NotifyDTO> message) {
        final NotifyDTO notifyDTO = message.getMessageObject();
        retry(HttpPutNotifySender::sendHttpRequest, notifyDTO, 3);
    }
}