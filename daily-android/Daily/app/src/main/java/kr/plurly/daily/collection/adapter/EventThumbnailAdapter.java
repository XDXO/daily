package kr.plurly.daily.collection.adapter;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.plurly.daily.R;
import kr.plurly.daily.collection.listener.OnItemClickListener;
import kr.plurly.daily.component.activity.DailyView;
import kr.plurly.daily.databinding.LayoutItemEventThumbnailBinding;
import kr.plurly.daily.domain.Event;
import kr.plurly.daily.inject.component.ActivityComponent;
import kr.plurly.daily.inject.module.AppModule;
import kr.plurly.daily.util.Constraints;

class EventThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String EVENT_PHOTO_FORMAT = "%s/events/%s/photo";

    private final LayoutItemEventThumbnailBinding binding;

    private OnItemClickListener<Event> listener;
    void setOnItemClickListener(OnItemClickListener<Event> listener) { this.listener = listener; }

    EventThumbnailViewHolder(LayoutItemEventThumbnailBinding binding) {

        super(binding.getRoot());

        this.binding = binding;
        this.binding.getRoot().setOnClickListener(this);
    }

    void bind(Event event) {

        binding.setEvent(event);
        binding.setExpanded(false);

        if (!TextUtils.isEmpty(event.getPath())) {

            Picasso.with(binding.getRoot().getContext())
                   .load(String.format(Locale.getDefault(), EVENT_PHOTO_FORMAT, AppModule.URL, event.getUUID()))
                   .placeholder(new BitmapDrawable(binding.getRoot().getResources(), event.getPath()))
                   .resize(300, 160)
                   .into(binding.imageViewContent);
        }
    }

    void setPosition(int position) {

        binding.setPosition(position);
    }

    void setSize(int size) {

        binding.setSize(size);
    }

    @Override
    public void onClick(View v) {

        boolean expanded = binding.getExpanded();
        binding.setExpanded(!expanded);

        if (listener != null) {

            Event event = binding.getEvent();
            listener.onClick(event);
        }
    }
}

public class EventThumbnailAdapter extends RecyclerView.Adapter<EventThumbnailViewHolder> implements OnItemClickListener<Event>, Comparator<Event> {


    private OnItemClickListener<Event> listener;

    public OnItemClickListener<Event> getOnItemClickListener() { return listener; }
    public void setOnItemClickListener(OnItemClickListener<Event> listener) { this.listener = listener; }


    private List<Event> events = new ArrayList<>();

    public void add(Event event) {

        add(event, true);
    }

    public void addAll(Collection<Event> items) {

        for (Event item : items)
            add(item, false);

        invalidate();
    }

    private void add(Event event, boolean refresh) {

        if (events.contains(event)) {

            update(event, refresh);
            return;
        }

        events.add(0, event);

        if (refresh)
            invalidate();
    }

    public void update(Event event) {

        update(event, true);
    }

    private void update(Event event, boolean refresh) {

        int index = events.indexOf(event);

        if (index == -1)
            return;

        events.set(index, event);

        if (refresh && (!isSorted() || event.isSynced()))
            invalidate();
    }

    public void remove(Event event) {

        int index = events.indexOf(event);

        if (index == -1)
            return;

        events.remove(event);
        invalidate();
    }

    private void invalidate() {

        Collections.sort(events, this);
        notifyDataSetChanged();
    }

    private boolean isSorted() {

        boolean isSorted = true;

        Event pointer = null;

        for (Event event : events) {

            if (pointer != null && compare(pointer, event) > 0) {

                isSorted = false;
                break;
            }

            pointer = event;
        }

        return isSorted;
    }

    public Date getReference() {

        Date date = null;

        for (Event event : events) {

            if (date == null || !event.isSynced() && event.getCreatedAt().before(date)) {

                date = event.getCreatedAt();
            }
        }

        return date != null ? date : latest();
    }

    private Date latest() {

        Date date = null;

        for (Event event : events) {

            if (date == null || event.getCreatedAt().after(date)) {

                date = event.getCreatedAt();
            }
        }

        return date != null ? date : Constraints.DEFAULT_FETCH_DATE;
    }

    @Override
    public int compare(Event e1, Event e2) {

        Date d1 = e1.getCreatedAt();
        Date d2 = e2.getCreatedAt();

        return d1.before(d2) ? 1 : -1;
    }

    public EventThumbnailAdapter(DailyView view) {

        ActivityComponent component = view.getActivityComponent();
        component.inject(this);
    }

    @Override
    public EventThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int type) {

        LayoutItemEventThumbnailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_item_event_thumbnail, parent, false);
        binding.setExpanded(false);

        EventThumbnailViewHolder holder = new EventThumbnailViewHolder(binding);
        holder.setOnItemClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(EventThumbnailViewHolder holder, int position) {

        Event event = events.get(position);

        holder.setPosition(position);
        holder.setSize(events.size());

        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onClick(Event event) {

        if (listener != null)
            listener.onClick(event);
    }
}
