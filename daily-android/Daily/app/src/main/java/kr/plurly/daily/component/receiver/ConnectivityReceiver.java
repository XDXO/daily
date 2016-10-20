package kr.plurly.daily.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

import kr.plurly.daily.app.App;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.inject.component.AppComponent;
import kr.plurly.daily.job.SyncEventJob;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Inject
    ConnectivityManager manager;

    @Inject
    RxBus bus;

    @Override
    public void onReceive(Context context, Intent intent) {

        App app = App.getInstance();

        AppComponent component = app.getAppComponent();
        component.inject(this);

        String action = intent.getAction();

        if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            return;

        NetworkInfo active = manager.getActiveNetworkInfo();

        if (active != null && active.isConnected())
            bus.post(new SyncEventJob());
    }
}
