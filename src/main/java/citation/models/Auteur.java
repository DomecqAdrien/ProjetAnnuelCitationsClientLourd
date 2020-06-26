package citation.models;

import java.util.ArrayList;
import java.util.List;



public class Auteur {
	

	public int id;
	public String nom;
	public String prenom;
	public String dateNaissance;

	public List<Book> books;
	
	public Auteur(String nom, String prenom, String dateNaissance) {
		this.nom = nom;
		this.prenom = prenom;
		this.dateNaissance = dateNaissance;
		this.books = new ArrayList<>();
	}
	
	public Auteur() {
	}


}