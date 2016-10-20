package kr.plurly.daily.inject.module;

import android.content.Context;
import android.net.ConnectivityManager;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kr.plurly.daily.app.App;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.model.EventModel;
import kr.plurly.daily.network.EventService;
import kr.plurly.daily.util.Constraints;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class AppModule {

    public static final String URL = "http://10.0.2.2:3000";

    private final App app;

    public AppModule(App app) {

        this.app = app;
    }

    @Singleton
    @Provides
    EventModel eventModel() { return new EventModel(app); }

    @Singleton
    @Provides
    EventService eventService(Retrofit retrofit) { return retrofit.create(EventService.class); }

    @Singleton
    @Provides
    public OkHttpClient client() {

        // Build OkHttpClient
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        clientBuilder.addInterceptor(interceptor);
        clientBuilder.writeTimeout(Constraints.WRITE_TIME, TimeUnit.MILLISECONDS);
        clientBuilder.readTimeout(Constraints.READ_TIME, TimeUnit.MILLISECONDS);
        clientBuilder.connectTimeout(Constraints.CONNECTION_TIME, TimeUnit.MILLISECONDS);

        clientBuilder.retryOnConnectionFailure(true);

        OkHttpClient client = clientBuilder.build();

        // Build Picasso
        Picasso.Builder picassoBuilder = new Picasso.Builder(app);
        picassoBuilder.downloader(new OkHttp3Downloader(client));

        Picasso picasso = picassoBuilder.build();
        Picasso.setSingletonInstance(picasso);

        return client;
    }

    @Singleton
    @Provides
    public Retrofit retrofit(OkHttpClient client) {

        Retrofit.Builder builder = new Retrofit.Builder();

        builder.client(client);
        builder.baseUrl(URL);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        return builder.build();
    }


    @Singleton
    @Provides
    public RxBus bus() {

        return new RxBus();
    }

    @Singleton
    @Provides
    public ConnectivityManager connectivityManager() {

        return (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
