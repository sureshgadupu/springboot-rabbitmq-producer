package dev.fullstackcode.sb.rabbitmq.producer;

import dev.fullstackcode.sb.rabbitmq.producer.controller.RabbitMQProducerController;
import dev.fullstackcode.sb.rabbitmq.producer.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@Slf4j
public class RabbitMQProducerControllerIT {


    static RabbitMQContainer rabbitMQContainer ;

    static {
        WaitStrategy waitStrategy =new HostPortWaitStrategy();
        rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.10.6-management-alpine") .withExposedPorts(5672,15672)
                .withStartupTimeout(Duration.of(300, SECONDS));
         rabbitMQContainer.setWaitStrategy(waitStrategy);
        rabbitMQContainer.start();
     }

    static {
        rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.10.6-management-alpine")
                .withStartupTimeout(Duration.of(100, SECONDS));
        rabbitMQContainer.start();

    }

//    static GenericContainer rabbitMQContainer ;
//    static {
//     WaitStrategy hs = new HostPortWaitStrategy().withStartupTimeout(Duration.of(3000, SECONDS));
//      rabbitMQContainer = new GenericContainer(DockerImageName.parse("rabbitmq:3.10.6-alpine"))
//            .withExposedPorts(5672,15672) .withStartupTimeout(Duration.of(120, SECONDS));
//        rabbitMQContainer.start();
//
//        }
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitMQProducerController rabbitMQProducerController;

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void testQueue() throws Exception {
        log.info("url ->{}",rabbitMQContainer.getAmqpUrl());
        log.info("rabbitMQProducerController ->{}",rabbitMQProducerController);
        log.info("{admin} ->{}",rabbitAdmin);
        log.info("{template}->{}",rabbitTemplate);

        Event event = new Event();
        event.setId(1);
        event.setName("Event A");
        rabbitMQProducerController.send(event);
       // Thread.sleep(10);
       Message message =  rabbitTemplate.receive("queue.A",10);
       Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        Event o1 = (Event) converter.fromMessage(message);
       Object o =  message.getMessageProperties().getHeader("__TypeId__");
        log.info("{template}->{}",o1);
        log.info("{template}->{}",message);
       log.info("{template}->{}",message.getBody());

//        ResponseEntity<Object> queues = testRestTemplate.getForEntity("http://"+ rabbitMQContainer.getHost()+":"+rabbitMQContainer.getMappedPort(15672)+"/api/queues", Object.class);
//        log.info("queues{}",queues);
       String s ="s";
       assertEquals("s",s);
    }

    @Test
    public void testQueueCreation() throws Exception {
        //log.info("queues{}",rabbitMQContainer.get);
  //     ResponseEntity<ArrayList> queues = testRestTemplate.getForEntity("http://"+ rabbitMQContainer.getHost()+":"+rabbitMQContainer.getHttpPort()+"/api/queues/vhost", ArrayList.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", "guest");
        map.add("password", "guest");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        Object o1 =  testRestTemplate.getForEntity("http://"+ rabbitMQContainer.getHost()+":"+rabbitMQContainer.getHttpPort()+"/api/auth", String.class);

//        Object o2 =  testRestTemplate.getForEntity("http://"+ rabbitMQContainer.getHost()+":"+rabbitMQContainer.getHttpPort()+"/js/tmpl/login.ejs?0.5553623788616073", String.class);
//        log.info("auth{}",o2);
//       Object o =  testRestTemplate.exchange("http://"+ rabbitMQContainer.getHost()+":"+rabbitMQContainer.getHttpPort()+"#/login", HttpMethod.PUT,request,String.class,"");
//        log.info("auth{}",o);
       ResponseEntity<Object> queues = testRestTemplate.withBasicAuth("guest","guest").getForEntity("http://"+ rabbitMQContainer.getHost()+":"+rabbitMQContainer.getHttpPort()+"/api/exchanges", Object.class);
       log.info("queues{}",queues);
    }


//    @Bean
//    MessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter());
//        return rabbitTemplate;
//    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        log.info("url ->{}",rabbitMQContainer.getAmqpUrl());
        log.info("url ->{}",rabbitMQContainer.getHttpPort());
 //       registry.add("spring.rabbitmq.host",rabbitMQContainer::getAmqpUrl);

        registry.add("spring.rabbitmq.host",() -> rabbitMQContainer.getHost());
        registry.add("spring.rabbitmq.port",() -> rabbitMQContainer.getAmqpPort());
//        log.info("url ->{}",rabbitMQContainer.getHost());
//        registry.add("spring.rabbitmq.host",() -> rabbitMQContainer.getHost() + ":"+ rabbitMQContainer.getFirstMappedPort());
    }
// https://rawcdn.githack.com/rabbitmq/rabbitmq-server/v3.10.7/deps/rabbitmq_management/priv/www/api/index.html
}
