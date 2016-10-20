package kr.plurly.daily.inject.module;


import dagger.Module;
import dagger.Provides;
import kr.plurly.daily.collection.adapter.EventThumbnailAdapter;
import kr.plurly.daily.component.activity.DailyView;
import kr.plurly.daily.dialog.DismissDialog;
import kr.plurly.daily.dialog.EmotionDialog;
import kr.plurly.daily.layer.craft.CraftPresenter;
import kr.plurly.daily.layer.craft.CraftView;
import kr.plurly.daily.layer.entry.EntryPresenter;
import kr.plurly.daily.layer.entry.EntryView;
import kr.plurly.daily.util.TimeTracker;

@Module
public class ActivityModule {

    private final DailyView view;

    public ActivityModule(DailyView view) {

        this.view = view;
    }


    @Provides
    CraftPresenter craftPresenter() { return new CraftPresenter((CraftView) view); }

    @Provides
    EntryPresenter entryPresenter() { return new EntryPresenter((EntryView) view); }

    @Provides
    EventThumbnailAdapter eventThumbnailAdapter() { return new EventThumbnailAdapter(view); }


    @Provides
    DismissDialog dismissDialog() { return new DismissDialog(); }

    @Provides
    EmotionDialog emotionDialog() { return new EmotionDialog(); }


    @Provides
    TimeTracker timeTracker() { return new TimeTracker(); }
}
