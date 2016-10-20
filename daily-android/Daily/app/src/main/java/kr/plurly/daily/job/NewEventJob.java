package kr.plurly.daily.job;

import kr.plurly.daily.domain.Event;

public class NewEventJob {

    public final Event event;

    public NewEventJob(Event event) {

        this.event = event;
    }
}
