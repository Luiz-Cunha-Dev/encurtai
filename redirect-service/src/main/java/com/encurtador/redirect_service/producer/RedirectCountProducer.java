package com.encurtador.redirect_service.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.Sender;

@Component
@Slf4j
public class RedirectCountProducer extends AbstractRabbitProducer {
    public RedirectCountProducer(Sender sender) {
        super(sender);
    }

    @Override
    protected String getExchange() {
        return "redirect-exchange";
    }

    @Override
    protected String getRoutingKey() {
        return "redirect.count";
    }

    public void publish(String message) {
        publishEvent(message).subscribe();
    }
}

