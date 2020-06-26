package models;

import java.util.ArrayList;
import java.util.List;



public class Auteur {
	

	public int id;
	public String nom;
	public String prénom;
	public String dateNaissance;

	public List<Book> books;
	
	public Auteur(String nom, String prénom, String dateNaissance) {
		this.nom = nom;
		this.prénom = prénom;
		this.dateNaissance = dateNaissance;
		this.books = new ArrayList<>();
	}
	
	public Auteur() {
	}


}