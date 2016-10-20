package kr.plurly.daily.app;

import android.app.Application;

import io.realm.Realm;
import kr.plurly.daily.inject.component.AppComponent;
import kr.plurly.daily.inject.component.DaggerAppComponent;
import kr.plurly.daily.inject.module.AppModule;

public class App extends Application {

    // TODO : 1. Post (Sync / Delete) Test
    // TODO : 2. Check Logic for Loss of Connection
    // TODO : 3. Server Connection Test

    private static App instance;
    public static App getInstance() { return instance; }

    private AppComponent component;

    @Override
    public void onCreate() {

        super.onCreate();

        App.instance = this;
        Realm.init(this);

        component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() { return component; }
}
