package citation.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
public class Auteur {
	
	private int id;
	private String nom;
	private String prenom;
	private String dateNaissance;
	@JsonIgnoreProperties({"auteur"})
	public List<Book> books = new ArrayList<>();

}