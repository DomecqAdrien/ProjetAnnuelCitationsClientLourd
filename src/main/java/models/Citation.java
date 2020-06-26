package models;

import java.util.List;

public class Citation {

	public int id;
	public Book book;
	public String citation;
	public List<Tag> tags;
	
	
	public Citation(int book_id, String citation) {
		this.citation = citation;
	}
	
	public Citation() {
	}
}
