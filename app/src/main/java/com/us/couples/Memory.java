package com.us.couples;

// one saved memory (date is saved as yyyy-MM-dd text)
public final class Memory {
    public final long id;
    public final String title;
    public final String note;
    public final String date;
    public final String mood;

    public Memory(long id, String title, String note, String date, String mood) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.date = date;
        this.mood = mood;
    }
}
