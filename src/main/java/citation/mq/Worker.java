package citation.mq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Worker {
	private final static String QUEUE_NAME = "sample.queue";

	public static void main(String[] argv) throws Exception  {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    channel.basicQos(1);
	    
	    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            
            try {
				doWork(message);
            } finally {
            	channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            	System.out.println(" [x] Done");
            }
        };
        boolean autoAck = false; // acknowledgment is covered below
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });

	  }
	
	private static void doWork(String task)  {
	    for (char ch: task.toCharArray()) {
	        if (ch == '.')
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Thread.currentThread().interrupt();
				}
	    }
	}
}
