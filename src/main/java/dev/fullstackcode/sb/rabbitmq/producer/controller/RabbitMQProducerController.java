package dev.fullstackcode.sb.rabbitmq.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fullstackcode.sb.rabbitmq.producer.model.Event;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value ="rabbitmq/event")
public class RabbitMQProducerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange directExchange;


    @PostMapping
    public String  send(@RequestBody Event event){
        if( event.getName().equalsIgnoreCase("Event A")) {
            rabbitTemplate.convertAndSend(directExchange.getName(), "event_A", event);
        } else if (event.getName().equalsIgnoreCase("Event B")) {
            rabbitTemplate.convertAndSend(directExchange.getName(), "event_B", event);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"unknown event");
        }
        return "message sent successfully";
    }

}
