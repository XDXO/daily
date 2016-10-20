package kr.plurly.daily.util;


import java.util.Date;

public class TimeTracker {

    private Date current;
    public Date getCurrent() { return current; }

    private Date next;
    public Date getNext() { return next; }

    public void pull() {

        current = next;
        next = null;
    }

    public void updateCurrent(Date date) {

        if (current == null || date != null && date.before(current))
            current = date;
    }

    public void updateNext(Date date) {

        if (next == null || date != null && date.before(next))
            next = date;
    }

    public void reset() {

        current = null;
        next = null;
    }
}
