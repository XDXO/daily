package kr.plurly.daily.layer.entry;

import java.net.ConnectException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import kr.plurly.daily.R;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.component.activity.DailyPresenter;
import kr.plurly.daily.domain.Event;
import kr.plurly.daily.inject.component.AppComponent;
import kr.plurly.daily.job.FetchEventJob;
import kr.plurly.daily.model.EventModel;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class EntryPresenter extends DailyPresenter<EntryView> {

    @Inject
    EventModel eventModel;

    @Inject
    RxBus bus;

    private final Action1<Response<List<Event>>> onFetch;
    private final Action1<Throwable> onError;

    public EntryPresenter(final EntryView view) {

        super(view);

        AppComponent component = view.getAppComponent();
        component.inject(this);

        onFetch = new Action1<Response<List<Event>>>() {

            @Override
            public void call(Response<List<Event>> response) {

                if (response.isSuccessful()) {

                    List<Event> events = response.body();
                    eventModel.saveAll(events);

                    Event oldest = null;

                    for (Event event : events) {

                        if (oldest == null || event.getCreatedAt().before(oldest.getCreatedAt()))
                            oldest = event;
                    }

                    bus.post(new FetchEventJob(oldest));
                }
                else {

                    view.hintFailedFetch();
                }
            }
        };

        onError = new Action1<Throwable>() {

            @Override
            public void call(Throwable throwable) {

                int message = throwable instanceof ConnectException ? R.string.label_entry_error_network_error : R.string.label_entry_error_undefined;

                view.snack(view.findViewById(android.R.id.content), message);
                view.setRefresh(false);
            }
        };
    }

    void fetch() {

        Date timestamp = getLatestEventTimestamp();

        Observable<Response<List<Event>>> observable = eventModel.fetch(timestamp);
        observable.subscribe(onFetch, onError);
    }

    void load(Date oldest) {

        List<Event> events = eventModel.load(oldest);
        view.appendEvents(events);
    }

    private Date getLatestEventTimestamp() {

        Realm realm = Realm.getDefaultInstance();
        return realm.where(Event.class).equalTo(Event.FIELD_SYNCED, true).maximumDate(Event.FIELD_CREATED_AT);
    }
}
