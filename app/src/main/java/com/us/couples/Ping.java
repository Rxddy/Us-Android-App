package com.us.couples;

// one ping we sent (sentAt is in millis)
public final class Ping {
    public static final String THINKING = "thinking";
    public static final String MISS_YOU = "miss_you";
    public static final String HUG = "hug";

    public final long id;
    public final String kind;
    public final String message;
    public final long sentAt;

    public Ping(long id, String kind, String message, long sentAt) {
        this.id = id;
        this.kind = kind;
        this.message = message;
        this.sentAt = sentAt;
    }
}
