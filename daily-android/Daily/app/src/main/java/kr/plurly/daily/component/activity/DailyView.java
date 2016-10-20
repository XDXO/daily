package kr.plurly.daily.component.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import kr.plurly.daily.app.App;
import kr.plurly.daily.inject.component.ActivityComponent;
import kr.plurly.daily.inject.component.AppComponent;
import kr.plurly.daily.inject.component.DaggerActivityComponent;
import kr.plurly.daily.inject.module.ActivityModule;

public class DailyView extends AppCompatActivity {

    protected AppComponent appComponent;
    public AppComponent getAppComponent() { return appComponent; }

    protected ActivityComponent activityComponent;
    public ActivityComponent getActivityComponent() { return activityComponent; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        App app = (App) getApplication();

        appComponent = app.getAppComponent();
        activityComponent = DaggerActivityComponent.builder().appComponent(appComponent).activityModule(new ActivityModule(this)).build();
    }

    public void snack(View view, int id) { Snackbar.make(view, id, Snackbar.LENGTH_LONG).show(); }
}
