package citation.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
public class Citation {

	private int id;
	private int bookId;
	private Book book;
	private String imageUrl;
	private String citation;
	private List<Tag> tags;
	@JsonIgnoreProperties({"citationsConnexes"})
	private List<Citation> citationsConnexes = new ArrayList<>();
	private int tauxRessemblance;
	
}
