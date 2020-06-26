package citation.models;

import java.util.List;

public class Citation {

	public int id;
	public Book book;
	public String citation;
	public List<Tag> tags;
	public List<Citation> citationConnexes;
	
}
