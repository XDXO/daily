package kr.plurly.daily.job;

import kr.plurly.daily.domain.Event;

public class UpdateEventJob {

    public final Event event;

    public UpdateEventJob(Event event) {

        this.event = event;
    }
}
