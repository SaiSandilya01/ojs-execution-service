package org.codeexectutionservice.code_exectution_service.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;;

@Configuration
public class RabbitMQConnector {
    public static final String EXCHANGE = "ojs.submit";          // your topic exchange name
    public static final String QUEUE = "ojs.submit.queue";       // queue name
    public static final String ROUTING_KEY = "submission.created";

    @Bean
    public TopicExchange ojsExchange(){
      return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue submitQueue() {
      return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding submitBinding() {
      return BindingBuilder.bind(submitQueue()).to(ojsExchange()).with(ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    

}
