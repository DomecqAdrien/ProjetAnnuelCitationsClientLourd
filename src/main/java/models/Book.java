package models;

import java.util.ArrayList;
import java.util.List;

public class Book {
	

	public int id;
	public String titre;
	public Auteur auteur;
	public int anneeParution;
	
	
	public List<Citation> citations;
	
	public Book(String nom, int auteur_id, int anneeParution) {
		this.titre = nom;
		this.anneeParution = anneeParution;
		this.citations = new ArrayList<>();
	}
	
	public Book() {
	}


}
