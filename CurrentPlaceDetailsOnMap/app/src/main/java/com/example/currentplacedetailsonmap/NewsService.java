package com.example.currentplacedetailsonmap;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {
    @GET("v2/") // Define the API endpoint
    Call<ApiResponse> getNews(
            @Query("q") String query,          // Query parameters
            @Query("from") String fromDate,    // Query parameters
            @Query("sortBy") String sortBy,    // Query parameters
            @Query("apiKey") String apiKey     // Query parameters
    );
}