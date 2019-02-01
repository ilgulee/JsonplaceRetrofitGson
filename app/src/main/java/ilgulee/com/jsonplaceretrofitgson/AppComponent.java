package ilgulee.com.jsonplaceretrofitgson;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class,NetworkModule.class})
public interface AppComponent {
    void inject(FetchActivity activity);
    void inject(CommentActivity activity);
    void inject(MainActivity activity);
}
