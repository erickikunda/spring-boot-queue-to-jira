package com.example.jiraticketservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitConfig {
    @Bean DirectExchange escalationExchange(ApplicationProperties p) { return new DirectExchange(p.queue().exchange()); }
    @Bean Queue escalationQueue(ApplicationProperties p) {
        return QueueBuilder.durable(p.queue().escalationQueue())
                .deadLetterExchange(p.queue().exchange()).deadLetterRoutingKey(p.queue().deadLetterRoutingKey()).build();
    }
    @Bean Queue deadLetterQueue(ApplicationProperties p) { return QueueBuilder.durable(p.queue().deadLetterQueue()).build(); }
    @Bean Binding escalationBinding(ApplicationProperties p, Queue escalationQueue, DirectExchange escalationExchange) {
        return BindingBuilder.bind(escalationQueue).to(escalationExchange).with(p.queue().routingKey());
    }
    @Bean Binding deadLetterBinding(ApplicationProperties p, Queue deadLetterQueue, DirectExchange escalationExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(escalationExchange).with(p.queue().deadLetterRoutingKey());
    }
    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless().maxAttempts(3)
                .recoverer(new RejectAndDontRequeueRecoverer()).build());
        return factory;
    }
}
