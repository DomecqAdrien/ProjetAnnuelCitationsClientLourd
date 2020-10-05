package citation.services;

import java.util.List;

import citation.models.Dicolink;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DicoLinkInterface {
		
	@GET("{mot}/synonymes")
	Call<List<Dicolink>> getAllSynonymes(@Path("mot") String mot, @Query("api_key") String APIKey);

}
