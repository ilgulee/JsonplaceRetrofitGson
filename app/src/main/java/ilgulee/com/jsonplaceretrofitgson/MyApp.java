package ilgulee.com.jsonplaceretrofitgson;

import android.app.Application;

public class MyApp extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule("https://jsonplaceholder.typicode.com/"))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
