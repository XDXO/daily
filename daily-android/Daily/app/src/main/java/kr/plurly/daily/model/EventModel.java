package kr.plurly.daily.model;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

import io.realm.Realm;
import kr.plurly.daily.app.App;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.domain.Event;
import kr.plurly.daily.inject.component.AppComponent;
import kr.plurly.daily.job.DeleteEventJob;
import kr.plurly.daily.job.NewEventJob;
import kr.plurly.daily.job.SyncEventJob;
import kr.plurly.daily.job.UpdateEventJob;
import kr.plurly.daily.network.EventService;
import kr.plurly.daily.util.Constraints;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static kr.plurly.daily.util.Constraints.MIME_TYPE_IMAGE;
import static kr.plurly.daily.util.Constraints.MIME_TYPE_TEXT;

public class EventModel {

    private class SyncSuccessAction implements Action1<Response<Event>> {

        private final Realm realm;
        private final Event event;

        SyncSuccessAction(Realm realm, Event event) {

            this.realm = realm;
            this.event = event;
        }

        @Override
        public void call(Response<Event> response) {

            if (response.isSuccessful()) {

                Event server = response.body();
                persist(realm, event, server);

                bus.post(new UpdateEventJob(server));
            }
            else {

                Log.e("[Sync]", "Failure : " + event.getUUID());
            }
        }
    }

    @Inject
    RxBus bus;

    @Inject
    EventService eventService;

    public EventModel(App app) {

        AppComponent component = app.getAppComponent();
        component.inject(this);

        bus.register(SyncEventJob.class, new Action1<SyncEventJob>() {

            Action1<Throwable> onSyncFailure = new Action1<Throwable>() {

                @Override
                public void call(Throwable throwable) {

                    Log.e("[Network]", "Failure : " + throwable.getMessage());
                }
            };

            @Override
            public void call(SyncEventJob job) {

                final Realm realm = Realm.getDefaultInstance();

                for (final Event event : realm.where(Event.class).equalTo(Event.FIELD_SYNCED, false).findAll()) {

                    Observable<Response<Event>> observable = upload(event);
                    observable.subscribe(new SyncSuccessAction(realm, event), onSyncFailure);
                }
            }
        });
    }

    public Observable<Response<Event>> newEvent(Event event) {

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.copyToRealm(event);
        realm.commitTransaction();

        bus.post(new NewEventJob(event));

        return upload(event);
    }

    public Observable<Response<List<Event>>> fetch(Date timestamp) {

        long since = timestamp != null ? timestamp.getTime() : 0L;
        Observable<Response<List<Event>>> observable = eventService.fetch(since);

        return observable.subscribeOn(Schedulers.newThread())
                         .observeOn(AndroidSchedulers.mainThread());
    }

    public void saveAll(List<Event> events) {

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(events);
        realm.commitTransaction();
    }

    public List<Event> load(Date oldest) {

        Realm realm = Realm.getDefaultInstance();
        ListIterator<Event> query = realm.where(Event.class).greaterThan(Event.FIELD_CREATED_AT, oldest).findAll().listIterator();

        List<Event> events = new ArrayList<>();

        while (query.hasNext()) {

            Event event = query.next();
            events.add(event);
        }

        return events;
    }

    public void delete(Event event) {

        Realm realm = Realm.getDefaultInstance();
        Event found = realm.where(Event.class).equalTo(Event.FIELD_UUID, event.getUUID()).findFirst();

        if (found == null)
            return;

        bus.post(new DeleteEventJob(event));

        realm.beginTransaction();
        found.deleteFromRealm();
        realm.commitTransaction();
    }



    private Observable<Response<Event>> upload(Event event) {

        String path = event.getPath();

        MultipartBody.Part photo = null;

        if (!TextUtils.isEmpty(path)) {

            File file = new File(path);
            photo = MultipartBody.Part.createFormData(Constraints.PART_NAME_PHOTO, file.getPath(), RequestBody.create(MediaType.parse(MIME_TYPE_IMAGE), file));
        }

        RequestBody pUUID = RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), event.getUUID());
        RequestBody pTitle = RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), event.getTitle());
        RequestBody pContent = RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), event.getTitle());
        RequestBody pEmotion = RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), String.valueOf(event.getEmotion()));

        Observable<Response<Event>> observable;

        if (event.hasLocation()) {

            RequestBody pLocation = RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), event.getLocation());
            RequestBody pLatitude = RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), String.valueOf(event.getLatitude()));
            RequestBody pLongitude = RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), String.valueOf(event.getLongitude()));

            observable = eventService.newEvent(pUUID, pTitle, pContent, pEmotion, photo, pLocation, pLatitude, pLongitude);
        }
        else {

            observable = eventService.newEvent(pUUID, pTitle, pContent, pEmotion, photo);
        }

        return observable.subscribeOn(Schedulers.newThread())
                         .observeOn(AndroidSchedulers.mainThread());
    }

    public void persist(Realm realm, Event event, Event server) {

        // Save local path to synced data for caching
        server.setPath(event.getPath());

        realm.beginTransaction();
        realm.insertOrUpdate(server);
        realm.commitTransaction();
    }
}
