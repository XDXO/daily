package kr.plurly.daily.job;

import kr.plurly.daily.domain.Event;

public class DeleteEventJob {

    public final Event event;

    public DeleteEventJob(Event event) {

        this.event = event;
    }
}
