package com.example.commerce.config;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 通过 短信，钉钉 等方式告警
 */
@Slf4j
@Component
public class NotifierConfig extends AbstractEventNotifier {
    protected NotifierConfig(InstanceRepository repository) {
        super(repository);
    }

    /**
     * 实现对事件的通知
     */
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            log.info("NotifierConfig.doNotify()");
            if (event instanceof InstanceStatusChangedEvent) {
                log.info("instance status changed: [{}] [{}] [{}]",
                        instance.getRegistration().getName(),
                        event.getInstance(),
                        ((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
            } else {
                log.info("instance info: [{}] [{}] [{}]",
                        instance.getRegistration().getName(),
                        event.getInstance(),
                        event.getType()
                );
            }
        });
    }
}
