package models;

import java.util.ArrayList;
import java.util.List;



public class Auteur {
	

	public int id;
	public String nom;
	public String pr�nom;
	public String dateNaissance;

	public List<Book> books;
	
	public Auteur(String nom, String pr�nom, String dateNaissance) {
		this.nom = nom;
		this.pr�nom = pr�nom;
		this.dateNaissance = dateNaissance;
		this.books = new ArrayList<>();
	}
	
	public Auteur() {
	}


}