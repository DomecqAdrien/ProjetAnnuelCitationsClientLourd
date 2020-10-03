package citation.controllers;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import citation.models.Citation;

@RestController
public class TestController {

	private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    
    public TestController() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }
    
	@PostMapping("/test")
	public void test(@RequestParam(value="text") String text, @RequestParam(value="hash") String hash) throws IOException, TimeoutException {
		String replyQueueName = channel.queueDeclare().getQueue();
		System.out.println(replyQueueName);
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(hash)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, text.getBytes("UTF-8"));
        
	}
	
	@GetMapping("/testCheck")
	public void testCheck(@RequestParam(value="queueName") String replyQueueName, @RequestParam(value="hash") String hash) throws IOException, InterruptedException {
		System.out.println(replyQueueName);
		final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(hash)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });
        String result = response.take();
        channel.basicCancel(ctag);
        System.out.println(result);
	}
}
