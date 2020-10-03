package citation.controllers;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import citation.models.Auteur;
import citation.models.Book;
import citation.models.Citation;
import citation.models.Tag;
import citation.services.APIs;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RechercheCitation {
	List<Citation> citations = null;
	List<Book> books = null;
	List<Auteur> auteurs = null;
	ObjectMapper op;
	
	public RechercheCitation() {
		loadData();
		op = new ObjectMapper();
	}

	//TODO CL:  Liste de citation : rechercher les citations par mots connexes
	//TODO API : envoyer sans attendre
	//TODO Android : envoyer requète et suivre l'avancement

	
	public String getCitation(String citationToGet) throws JsonProcessingException {
		loadData();
		System.out.println(citationToGet);
		
		Citation citationToReturn = citations.stream().filter(c -> c.getCitation().equals(citationToGet)).findFirst().get();
		
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
		
		int bookId = citationToReturn.getBookId();
		citationToReturn.setBook(books.stream().filter(b -> b.getId() == bookId).findFirst().get());
		
		int authorId = citationToReturn.getBook().getAuteurId();			
		citationToReturn.getBook().setAuteur(auteurs.stream().filter(a -> a.getId() == authorId).findFirst().get());
		citationToReturn.getBook().getAuteur().setBooks(books.stream().filter(b -> b.getAuteurId() == authorId).collect(Collectors.toList()));
		
		return op.writeValueAsString(citationToReturn);
	}
	
	
	public String searchCitationByText(String text) throws JsonProcessingException {
		loadData();
		System.out.println("Citation à chercher: "+text);
		
		List<Citation> citationsToReturn = new ArrayList<>();
		List<String> mots = new ArrayList<>();
		
		for(String mot : trimCitation(text).split(" ")) {
			if(mot.length() > 5 && !Character.isUpperCase(mot.charAt(0))) {
				List<String> synonymes = new ArrayList<>();
				//TODO : check synonymes
				for(String synonyme: synonymes)
					mots.add(synonyme);
			}
			mots.add(mot);
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
	
		return  op.writeValueAsString(citationsToReturn.stream().sorted(Comparator.comparing(Citation::getTauxRessemblance).reversed()).collect(Collectors.toList()));
	}
	
	public String trimCitation(String citation) {
		citation = Normalizer.normalize(citation, Normalizer.Form.NFD);
		citation = citation.replaceAll("[^\\p{ASCII}]", "").replaceAll("[^a-zA-Z0-9]", " ").replace("  ", " ");
		return citation.trim();
	}
	
	
	public void loadData() {
		
		Retrofit retrofit = new Retrofit.Builder()
			    .baseUrl("http://localhost:8081/")
			    .addConverterFactory(GsonConverterFactory.create())
			    .build();

		APIs service = retrofit.create(APIs.class);
		
		/*Call<List<Citation>> callCitations = service.getAllCitations();
		Call<List<Book>> callBooks = service.getAllBooks();
		//callBooks.wait();
		
		callCitations.enqueue(new Callback<List<Citation>>() {
			@Override
			
			public void onResponse(Call<List<Citation>> call, Response<List<Citation>> response) {
				// TODO Auto-generated method stub
				System.out.println("response citations");
				callBooks.notify();
			}
			@Override
			public void onFailure(Call<List<Citation>> call, Throwable t) {
				// TODO Auto-generated method stub
			}
		});
		
			
		callBooks.enqueue(new Callback<List<Book>>() {
			@Override
			public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
				System.out.println("response books");
				// TODO Auto-generated method stub	
			}
			@Override
			public void onFailure(Call<List<Book>> call, Throwable t) {
				// TODO Auto-generated method stub
			}
		});*/
		
		if(citations == null) {
			System.out.println("not loaded");
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Citation[]> responseCitations = restTemplate.getForEntity("http://localhost:8081/citation/all", Citation[].class);
			citations = Arrays.asList(responseCitations.getBody());
			ResponseEntity<Book[]> responseBooks = restTemplate.getForEntity("http://localhost:8081/book/all", Book[].class);
			books = Arrays.asList(responseBooks.getBody());
			ResponseEntity<Auteur[]> responseAuteurs = restTemplate.getForEntity("http://localhost:8081/auteur/all", Auteur[].class);
			auteurs = Arrays.asList(responseAuteurs.getBody());
		}
		else System.out.println("loaded");
	}
}
