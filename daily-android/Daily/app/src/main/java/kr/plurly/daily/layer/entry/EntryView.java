package kr.plurly.daily.layer.entry;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import kr.plurly.daily.R;
import kr.plurly.daily.bus.RxBus;
import kr.plurly.daily.collection.adapter.EventThumbnailAdapter;
import kr.plurly.daily.component.activity.DailyView;
import kr.plurly.daily.databinding.LayoutViewEntryBinding;
import kr.plurly.daily.domain.Event;
import kr.plurly.daily.job.DeleteEventJob;
import kr.plurly.daily.job.FetchEventJob;
import kr.plurly.daily.job.NewEventJob;
import kr.plurly.daily.job.SyncEventJob;
import kr.plurly.daily.job.UpdateEventJob;
import kr.plurly.daily.layer.craft.CraftView;
import kr.plurly.daily.util.Constraints;
import kr.plurly.daily.util.TimeTracker;
import rx.Subscription;
import rx.functions.Action1;

public class EntryView extends DailyView implements SwipeRefreshLayout.OnRefreshListener {

    private LayoutViewEntryBinding binding;

    @Inject
    EntryPresenter presenter;

    @Inject
    RxBus bus;

    @Inject
    TimeTracker timeTracker;

    @Inject
    EventThumbnailAdapter adapter;

    private boolean first = true;

    private Subscription newEventJobSubscription;
    private Subscription updateEventJobSubscription;
    private Subscription deleteEventJobSubscription;
    private Subscription fetchEventJobSubscription;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityComponent.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.layout_view_entry);

        binding.setPresenter(presenter);
        binding.setView(this);

        binding.refreshLayout.setOnRefreshListener(this);

        binding.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(binding.toolbar);

        final ActionBar toolbar = getSupportActionBar();

        if (toolbar != null) {

            toolbar.setTitle(R.string.app_name);
        }

        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewEvents.setAdapter(adapter);
    }

    @Override
    protected void onStart() {

        super.onStart();

        refresh(null);
        presenter.fetch();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (newEventJobSubscription == null)
            newEventJobSubscription = bus.register(NewEventJob.class, new Action1<NewEventJob>() {

                        @Override
                        public void call(NewEventJob job) {

                            adapter.add(job.event);
                            binding.recyclerViewEvents.smoothScrollToPosition(0);
                        }
                    });

        if (updateEventJobSubscription == null)
            updateEventJobSubscription = bus.register(UpdateEventJob.class, new Action1<UpdateEventJob>() {

                        @Override
                        public void call(UpdateEventJob job) {

                            adapter.update(job.event);
                            binding.recyclerViewEvents.smoothScrollToPosition(0);
                        }
                    });

        if (deleteEventJobSubscription == null)
            deleteEventJobSubscription = bus.register(DeleteEventJob.class, new Action1<DeleteEventJob>() {

                        @Override
                        public void call(DeleteEventJob job) { adapter.remove(job.event); }
                    });

        if (fetchEventJobSubscription == null)
            fetchEventJobSubscription = bus.register(
                    FetchEventJob.class,
                    new Action1<FetchEventJob>() {

                        @Override
                        public void call(FetchEventJob job) {

                            if (binding.refreshLayout.isRefreshing())
                                binding.refreshLayout.setRefreshing(false);

                            refresh(job.oldest);
                        }
                    });

        bus.post(new SyncEventJob());
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (binding.refreshLayout.isRefreshing())
            binding.refreshLayout.setRefreshing(false);

        timeTracker.reset();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        unsubscribe(newEventJobSubscription);
        unsubscribe(updateEventJobSubscription);
        unsubscribe(deleteEventJobSubscription);
        unsubscribe(fetchEventJobSubscription);
    }

    @Override
    public void onRefresh() {

        bus.post(new SyncEventJob());
        presenter.fetch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_craft : {

                Intent intent = new Intent(this, CraftView.class);
                startActivity(intent);

                break;
            }
        }

        return true;
    }

    private void refresh(Event reference) {

        if (binding.refreshLayout.isRefreshing() && reference != null) {

            timeTracker.updateNext(reference.getCreatedAt());
            return;
        }

        if (binding.refreshLayout.isRefreshing())
            binding.refreshLayout.setRefreshing(true);

        if (reference != null)
            timeTracker.updateCurrent(reference.getCreatedAt());

        Date since = null;

        boolean fetchAll = first;
        first = false;

        if (!fetchAll) {

            Date fromAdapter = adapter.getReference();

            if (reference != null) {

                Date fromReference = reference.getCreatedAt();
                since = fromReference.before(fromAdapter) ? fromReference : fromAdapter;
            }
            else {

                since = fromAdapter;
            }
        }

        since = since != null ? since : Constraints.DEFAULT_FETCH_DATE;
        presenter.load(since);
    }

    public void appendEvents(List<Event> events) {

        if (binding.refreshLayout.isRefreshing())
            binding.refreshLayout.setRefreshing(false);

        timeTracker.pull();

        if (events != null && events.size() > 0)
            adapter.addAll(events);
    }

    public void hintFailedFetch() {

        if (binding.refreshLayout.isRefreshing())
            binding.refreshLayout.setRefreshing(false);

        snack(binding.getRoot(), R.string.label_entry_error_load_events);
    }

    private void unsubscribe(Subscription subscription) {

        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    public void setRefresh(boolean refresh) {

        if (refresh != binding.refreshLayout.isRefreshing())
            binding.refreshLayout.setRefreshing(refresh);
    }
}
