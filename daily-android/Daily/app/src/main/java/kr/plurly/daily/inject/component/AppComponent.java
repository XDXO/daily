package kr.plurly.daily.inject.component;


import android.net.ConnectivityManager;

import javax.inject.Singleton;

import dagger.Component;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.component.receiver.ConnectivityReceiver;
import kr.plurly.daily.inject.module.AppModule;
import kr.plurly.daily.layer.craft.CraftPresenter;
import kr.plurly.daily.layer.entry.EntryPresenter;
import kr.plurly.daily.model.EventModel;
import kr.plurly.daily.network.EventService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {

    EventModel eventModel();
    EventService eventService();

    OkHttpClient client();
    Retrofit retrofit();

    RxBus bus();

    ConnectivityManager connectivityManager();

    void inject(EventModel eventModel);

    void inject(CraftPresenter craftPresenter);
    void inject(EntryPresenter entryPresenter);

    void inject(ConnectivityReceiver connectivityReceiver);
}
