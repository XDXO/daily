package kr.plurly.daily.bus;


import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public <T> Subscription register(final Class<T> type, Action1<T> onNext) {

        return bus
                .filter(new Func1<Object, Boolean>() {

                    @Override
                    public Boolean call(Object o) { return o.getClass().equals(type); }
                })
                .map(new Func1<Object, T>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public T call(Object o) { return (T) o; }
                })
                .subscribe(onNext);
    }

    public void post(Object event) {

        bus.onNext(event);
    }
}
