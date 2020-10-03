package citation.mq;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
public class NewTask {

	private final static String QUEUE_NAME = "task_queue";
	private static final String EXCHANGE_NAME = "logs";
	
	  public static void main(String[] argv) throws Exception {
		  ConnectionFactory factory = new ConnectionFactory();
		  factory.setHost("localhost");
		  
		  try (Connection connection = factory.newConnection();
		      Channel channel = connection.createChannel()) {
			  boolean durable = true;
			  

			  channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
			  String queueName = channel.queueDeclare().getQueue();
			  channel.queueBind(queueName, EXCHANGE_NAME, "black");
			  String message = String.join(" ", argv);

			  channel.basicPublish("logs", "task_queue", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
			  System.out.println(" [x] Sent '" + message + "'");

		  }
	  }

}
