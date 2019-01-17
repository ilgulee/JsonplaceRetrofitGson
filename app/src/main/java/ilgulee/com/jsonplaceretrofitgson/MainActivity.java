package ilgulee.com.jsonplaceretrofitgson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView textViewResult;
    private JsonPlaceHolderApi mJsonPlaceHolderApi;
    private CompositeDisposable mCompositeDisposable;
    private RxJava2CallAdapterFactory mAdapterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.text_view_result);

        mAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

        //create OkHttp client
        OkHttpClient.Builder okhttpclientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (BuildConfig.DEBUG) {
            okhttpclientBuilder.addInterceptor(loggingInterceptor);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(mAdapterFactory)
                .client(okhttpclientBuilder.build())
                .build();

        mJsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        getPosts();
        //getComments();
        //createPost();
        //updatePost();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: starts");
        mCompositeDisposable.dispose();
        super.onDestroy();
        Log.d(TAG, "onDestroy: ends");
    }


    private Observable<List<Post>> getPosts() {
        Log.d(TAG, "getPosts: starts");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", "1");
        parameters.put("_sort", "id");
        parameters.put("_order", "desc");

        Observable<List<Post>> call = mJsonPlaceHolderApi.getPosts(parameters);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Post>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        //mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<Post> posts) {
                        for (Post post : posts) {
                            String content = "";
                            content += "ID: " + post.getId() + "\n";
                            content += "User ID: " + post.getUserId() + "\n";
                            content += "Title: " + post.getTitle() + "\n";
                            content += "Text: " + post.getText() + "\n\n";
                            Log.d(TAG, "onResponse: " + content);
                            textViewResult.append(content);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (t instanceof HttpException) {
                            HttpException response = (HttpException) t;
                            int code = response.code();
                            Log.d(TAG, "onError: " + code);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        Log.d(TAG, "getPosts: ends");
        return null;
    }

    private void updatePost() {
        Post post = new Post(12, null, "New Text");

        Call<Post> call = mJsonPlaceHolderApi.patchPost(5, post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n";
                content += "ID: " + postResponse.getId() + "\n";
                content += "User ID: " + postResponse.getUserId() + "\n";
                content += "Title: " + postResponse.getTitle() + "\n";
                content += "Text: " + postResponse.getText() + "\n\n";

                textViewResult.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void deletePost() {
        Call<Void> call = mJsonPlaceHolderApi.deletePost(5);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                textViewResult.setText("Code: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private Call<Post> createPost() {
        Post post = new Post(23, "New Title", "New Text");
        Call<Post> call = mJsonPlaceHolderApi.createPost(post);

//        Map<String, String> fields = new HashMap<>();
//        fields.put("userId", "25");
//        fields.put("title", "New Title");
//        Call<Post> call = mJsonPlaceHolderApi.createPost(fields);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Log.d(TAG, "onResponse: starts");
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n";
                content += "ID: " + postResponse.getId() + "\n";
                content += "User ID: " + postResponse.getUserId() + "\n";
                content += "Title: " + postResponse.getTitle() + "\n";
                content += "Text: " + postResponse.getText() + "\n\n";
                Log.d(TAG, "onResponse: " + content);
                textViewResult.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
        return null;
    }



}
