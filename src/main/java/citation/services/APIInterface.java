package citation.services;

import java.util.List;

import citation.models.Auteur;
import citation.models.Book;
import citation.models.Citation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {
	@GET("citation/all")
	Call<List<Citation>> getAllCitations();
	
	@GET("book/all")
	Call<List<Book>> getAllBooks();
	
	@GET("auteur/all")
	Call<List<Auteur>> getAllAuthors();
	
	@GET("book/{id}")
	Call<Book> getBookById(@Path("id") int id);
	
	@GET("auteur/{id}")
	Call<Auteur> getAuteurById(@Path("id") int id);
}
