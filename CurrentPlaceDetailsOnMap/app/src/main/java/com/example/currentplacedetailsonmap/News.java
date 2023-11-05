package com.example.currentplacedetailsonmap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class News extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private NewsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(NewsViewModel.class)) {
                    return (T) new NewsViewModel();
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(NewsViewModel.class);

        viewModel.getNewsItems().observe(this, newsItems -> adapter.updateNewsItems(newsItems));
        viewModel.fetchNews("tesla", "2023-10-27", "publishedAt", "1c9675c6d7bc4c25a2cbdc6968beb50a");
    }

    public static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
        private List<NewsItem> newsItems;

        public NewsAdapter(List<NewsItem> newsItems) {
            this.newsItems = newsItems;
        }

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_layout, parent, false);
            return new NewsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
            NewsItem currentNewsItem = newsItems.get(position);
            holder.titleTextView.setText(currentNewsItem.getTitle());
            holder.descriptionTextView.setText(currentNewsItem.getDescription());
        }

        @Override
        public int getItemCount() {
            return newsItems.size();
        }

        public void updateNewsItems(List<NewsItem> newsItems) {
            this.newsItems = newsItems;
            notifyDataSetChanged();
        }

        static class NewsViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView descriptionTextView;

            public NewsViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            }
        }
    }

    public static class NewsItem {
        private String title;
        private String description;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class ApiResponse {
        private List<NewsItem> articles;

        public List<NewsItem> getArticles() {
            return articles;
        }
    }

    public interface NewsService {
        @GET("everything")
        Call<ApiResponse> getNews(
                @Query("q") String query,
                @Query("from") String fromDate,
                @Query("sortBy") String sortBy,
                @Query("apiKey") String apiKey
        );
    }

    public static class RetrofitClient {
        private static Retrofit retrofit;

        public static Retrofit getClient(String baseUrl) {
            if (retrofit == null) {
                if (!baseUrl.endsWith("/")) {
                    baseUrl += "/";
                }
                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    public static class NewsViewModel extends ViewModel {
        private MutableLiveData<List<NewsItem>> newsItems = new MutableLiveData<>();
        private NewsService service;

        public NewsViewModel() {
            Retrofit retrofit = RetrofitClient.getClient("https://newsapi.org/v2/");
            service = retrofit.create(NewsService.class);
        }

        public MutableLiveData<List<NewsItem>> getNewsItems() {
            return newsItems;
        }

        public void fetchNews(String query, String fromDate, String sortBy, String apiKey) {
            Call<ApiResponse> call = service.getNews(query, fromDate, sortBy, apiKey);
            Log.d("NewViewModel", "hello");
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("NewsViewModel", "API Response: " + response.body().getArticles());
                        newsItems.setValue(response.body().getArticles());
                    } else {
                        Log.e("NewsViewModel", "API Call failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("NewsViewModel", "Failed to fetch news", t);
                }
            });
        }

    }
}
