package kr.plurly.daily.job;

import kr.plurly.daily.domain.Event;

public class FetchEventJob {

    public final Event oldest;

    public FetchEventJob(Event event) {

        this.oldest = event;
    }
}
