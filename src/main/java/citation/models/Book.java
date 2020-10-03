package citation.models;

import java.util.List;

import lombok.Data;

@Data
public class Book {
	

	private int id;
	private int auteurId;
	private Auteur auteur;
	private String imageUrl;
	private String titre;
	private int anneeParution;
	private List<Citation> citations;
	


}
