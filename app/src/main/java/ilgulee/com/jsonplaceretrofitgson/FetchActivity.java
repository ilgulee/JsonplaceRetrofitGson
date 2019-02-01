package ilgulee.com.jsonplaceretrofitgson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchActivity extends AppCompatActivity {
    private static final String TAG = "FetchActivity";
    private TextView textViewResult;
    private JsonPlaceHolderApi mJsonPlaceHolderApi;
    @Inject Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ( (MyApp)getApplication()).getAppComponent().inject(this);
        textViewResult = findViewById(R.id.text_view_result);
        mJsonPlaceHolderApi = mRetrofit.create(JsonPlaceHolderApi.class);

        fetchPosts();
    }

    private Call<List<Post>> fetchPosts() {
        Log.d(TAG, "fetchPosts: starts");

        Call<List<Post>> call = mJsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                Log.d(TAG, "onResponse: starts");
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Post> posts = response.body();
                Log.d(TAG, "onResponse: " + Thread.currentThread().getName());
                for (Post post : posts) {
                    String content = "";
                    content += "ID: " + post.getId() + "\n";
                    content += "User ID: " + post.getUserId() + "\n";
                    content += "Title: " + post.getTitle() + "\n";
                    content += "Text: " + post.getText() + "\n\n";
                    Log.d(TAG, "onResponse: " + content);
                    textViewResult.append(content);
                }
                Log.d(TAG, "onResponse: ends");
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
        return null;
    }
}
