package com.us.couples;

// one planned date / date idea
public final class Plan {
    public final long id;
    public final String title;
    public final String place;
    public final String date;
    public final boolean done;

    public Plan(long id, String title, String place, String date, boolean done) {
        this.id = id;
        this.title = title;
        this.place = place;
        this.date = date;
        this.done = done;
    }
}
