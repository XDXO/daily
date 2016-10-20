package kr.plurly.daily.component.activity;

public class DailyPresenter<T extends DailyView> {

    protected final T view;

    public DailyPresenter(T view) {

        this.view = view;
    }
}
