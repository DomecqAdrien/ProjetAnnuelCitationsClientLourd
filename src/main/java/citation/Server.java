package citation;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import citation.models.*;
import citation.services.*;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class Server {
	private static List<Citation> citations = null;
	private static List<Book> books = null;
	private static List<Auteur> auteurs = null;
	private static String replyTo;
	private static ObjectMapper op;
	private static DicoLinkInterface DicolinkService;
	
	
	private static final String over_citation = "{\"statut\": \"OK\", \"citation\": %citation% }";
	private static final String over_citations = "{\"statut\": \"OK\", \"citations\": %citation% }";
	private static final String en_cours = "{\"statut\": \"En cours de traitement : %avancement% \"}";
	
	private static final String RPC_QUEUE_NAME = "rpc_queue";
	
	public static void main(String[] argv) throws Exception {
    	loadData();
		op = new ObjectMapper();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        
        Retrofit retrofitSynonymes = new Retrofit.Builder()
			    .baseUrl("https://api.dicolink.com/v1/")
			    .addConverterFactory(GsonConverterFactory.create())
			    .build();
        
		DicolinkService = retrofitSynonymes.create(DicoLinkInterface.class);

        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String response = "";
                replyTo = delivery.getProperties().getReplyTo();
                System.out.println(replyTo);
                
                String message = new String(delivery.getBody(), "UTF-8");
                String type = message.split(":::")[0].trim();
                String content = message.split(":::")[1].trim();
                try {
                    

                    System.out.println(" [.] fib(" + message + ")");
                    channel.basicPublish("", replyTo, null, en_cours.replace("%avancement%", "0%").getBytes("UTF-8"));
                    
                    if(type.equals("citation")) response += getCitation(content, channel);
                    if(type.equals("citations")) response += searchCitationByText(content, channel);
                    
                    
                    
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                	if(type.equals("citation")) channel.basicPublish("", replyTo, null, over_citation.replace("%citation%", response).getBytes("UTF-8"));
                	if(type.equals("citations")) channel.basicPublish("", replyTo, null, over_citations.replace("%citation%", response).getBytes("UTF-8"));
                    
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            
            

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    
    public static String getCitation(String citationToGet, Channel channel) throws JsonProcessingException {
		System.out.println("citation à chercher "+citationToGet);
		
		Citation citationToReturn = citations.stream().filter(c -> c.getCitation().equals(citationToGet)).findFirst().get();
		try {
			channel.basicPublish("", replyTo, null, en_cours.replace("%avancement%", "20%").getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Citation citation: citations) {
			Collection<Tag> similar = new HashSet<Tag>(citation.getTags());
			similar.retainAll(citationToReturn.getTags());
			if(similar.size() > 0) {
				if(!citation.getCitation().equals(citationToReturn.getCitation())) {
					citation.setBook(books.stream().filter(b -> b.getId() == citation.getBookId()).findFirst().get());
					citation.setTags(new ArrayList<>(similar));
					citationToReturn.getCitationsConnexes().add(citation);
				}	
			}
		}
		
		try {
			channel.basicPublish("", replyTo, null, en_cours.replace("%avancement%", "50%").getBytes("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int bookId = citationToReturn.getBookId();
		citationToReturn.setBook(books.stream().filter(b -> b.getId() == bookId).findFirst().get());
		try {
			channel.basicPublish("", replyTo, null, en_cours.replace("%avancement%", "80%").getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int authorId = citationToReturn.getBook().getAuteurId();			
		citationToReturn.getBook().setAuteur(auteurs.stream().filter(a -> a.getId() == authorId).findFirst().get());
		citationToReturn.getBook().getAuteur().setBooks(books.stream().filter(b -> b.getAuteurId() == authorId).collect(Collectors.toList()));
		
		return op.writeValueAsString(citationToReturn);
	}
	
	
	public static String searchCitationByText(String text, Channel channel) throws JsonProcessingException {
		System.out.println("Citations à chercher selon: "+text);
		
		List<Citation> citationsToReturn = new ArrayList<>();
		List<String> mots = new ArrayList<>();
		
		try {
			channel.basicPublish("", replyTo, null, en_cours.replace("%avancement%", "20%").getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String mot : trimCitation(text).split(" ")) {
			if(mot.length() > 3 && !Character.isUpperCase(mot.charAt(0))) {
				System.out.println(mot);
				DicolinkService.getAllSynonymes(mot, "fgr4EokUt-ahnd-Lv5qCmS3Xw_aZU0he").enqueue(new Callback<List<Dicolink>>(){

					@Override
					public void onResponse(Call<List<Dicolink>> call, Response<List<Dicolink>> response) {
						if(response.isSuccessful()) {
							for(Dicolink d : response.body()) {
								mots.add(d.getMot());
								System.out.println(mot);
							}
						}
					}

					@Override
					public void onFailure(Call<List<Dicolink>> call, Throwable t) {
						t.printStackTrace();
					}	
				});
			}
			mots.add(mot);
		}
		
		try {
			channel.basicPublish("", replyTo, null, en_cours.replace("%avancement%", "50%").getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		for(Citation citation: citations) {
			int countSameWords = 0;
			List<String> motsCitation = Arrays.asList(trimCitation(citation.getCitation()).split(" "));

			for(String mot : mots)
				if(motsCitation.contains(mot)) 
					countSameWords++;
			
			if(countSameWords > 0) {
				citation.setTauxRessemblance(countSameWords);
				citationsToReturn.add(citation);
			}
		}
		try {
			channel.basicPublish("", replyTo, null, en_cours.replace("%avancement%", "90%").getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		citationsToReturn = citationsToReturn.stream().sorted(Comparator.comparing(Citation::getTauxRessemblance).reversed()).collect(Collectors.toList());
		return  op.writeValueAsString(citationsToReturn);
	}
	
	public static String trimCitation(String citation) {
		citation = Normalizer.normalize(citation, Normalizer.Form.NFD);
		citation = citation.replaceAll("[^\\p{ASCII}]", "").replaceAll("[^a-zA-Z0-9]", " ").replace("  ", " ");
		return citation.trim();
	}
	
	
	public static void loadData() {
		
		Retrofit retrofit = new Retrofit.Builder()
			    .baseUrl("http://localhost:8081/")
			    .addConverterFactory(GsonConverterFactory.create())
			    .build();

		APIInterface service = retrofit.create(APIInterface.class);
		
		Call<List<Citation>> callCitations = service.getAllCitations();
		Call<List<Book>> callBooks = service.getAllBooks();
		Call<List<Auteur>> callAuteurs = service.getAllAuthors();
		
		try {
			citations = callCitations.execute().body();
			books = callBooks.execute().body();
			auteurs = callAuteurs.execute().body();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
