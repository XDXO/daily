package kr.plurly.daily.layer.craft;

import android.text.TextUtils;

import java.net.ConnectException;

import javax.inject.Inject;

import io.realm.Realm;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.component.activity.DailyPresenter;
import kr.plurly.daily.domain.Event;
import kr.plurly.daily.inject.component.AppComponent;
import kr.plurly.daily.job.DeleteEventJob;
import kr.plurly.daily.job.UpdateEventJob;
import kr.plurly.daily.model.EventModel;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class CraftPresenter extends DailyPresenter<CraftView> {

    @Inject
    EventModel eventModel;

    @Inject
    RxBus bus;

    private final Action1<Throwable> onError;

    public CraftPresenter(final CraftView view) {

        super(view);

        AppComponent component = view.getAppComponent();
        component.inject(this);

        onError = new Action1<Throwable>() {

            @Override
            public void call(Throwable throwable) {

                if (throwable instanceof ConnectException)
                    view.finish();
            }
        };
    }

    void newEvent(final Event event) {

        final String uuid = event.getUUID();

        if (TextUtils.isEmpty(event.getTitle())) {

            view.hintEmptyTitle();
            return;
        }

        if (TextUtils.isEmpty(event.getContent())) {

            view.hintEmptyContent();
            return;
        }

        Observable<Response<Event>> observable = eventModel.newEvent(event);
        observable.subscribe(new Action1<Response<Event>>() {

            @Override
            public void call(Response<Event> response) {

                Realm realm = Realm.getDefaultInstance();

                if (response.isSuccessful()) {

                    Event server = response.body();
                    eventModel.persist(realm, event, server);

                    bus.post(new UpdateEventJob(server));
                    view.finish();
                }
                else {

                    Event cached = realm.where(Event.class).equalTo(Event.FIELD_UUID, uuid).findFirst();

                    if (cached != null) {

                        bus.post(new DeleteEventJob(cached));

                        realm.beginTransaction();
                        cached.deleteFromRealm();
                        realm.commitTransaction();
                    }

                    view.hintFailedPost();
                }

            }
        }, onError);
    }

    void delete(Event event) {

        eventModel.delete(event);
        view.finish();
    }
}
