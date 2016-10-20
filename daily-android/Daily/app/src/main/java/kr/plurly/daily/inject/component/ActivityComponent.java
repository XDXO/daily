package kr.plurly.daily.inject.component;

import dagger.Component;
import kr.plurly.daily.collection.adapter.EventThumbnailAdapter;
import kr.plurly.daily.dialog.DismissDialog;
import kr.plurly.daily.dialog.EmotionDialog;
import kr.plurly.daily.inject.module.ActivityModule;
import kr.plurly.daily.inject.scope.PerActivity;
import kr.plurly.daily.layer.craft.CraftPresenter;
import kr.plurly.daily.layer.craft.CraftView;
import kr.plurly.daily.layer.entry.EntryPresenter;
import kr.plurly.daily.layer.entry.EntryView;
import kr.plurly.daily.util.TimeTracker;

@PerActivity
@Component(modules = { ActivityModule.class }, dependencies = { AppComponent.class })
public interface ActivityComponent extends AppComponent {

    CraftPresenter craftPresenter();
    EntryPresenter entryPresenter();

    EventThumbnailAdapter eventThumbnailAdapter();

    DismissDialog dismissDialog();
    EmotionDialog emotionDialog();

    TimeTracker timeTracker();

    void inject(CraftView craftView);
    void inject(EntryView entryView);

    void inject(EventThumbnailAdapter eventThumbnailAdapter);
}
