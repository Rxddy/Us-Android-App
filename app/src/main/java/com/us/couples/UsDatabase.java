package com.us.couples;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// sqlite db for memories, plans and pings.
// only call this from Background.run, not the main thread
public class UsDatabase extends SQLiteOpenHelper {
    private static final String NAME = "us.db";
    private static final int VERSION = 1;

    private static UsDatabase instance;

    public static synchronized UsDatabase get(Context context) {
        if (instance == null) {
            instance = new UsDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private UsDatabase(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE memories (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, note TEXT, date TEXT, mood TEXT)");
        db.execSQL("CREATE TABLE plans (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, place TEXT, date TEXT, done INTEGER NOT NULL DEFAULT 0)");
        db.execSQL("CREATE TABLE pings (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "kind TEXT NOT NULL, message TEXT, sent_at INTEGER NOT NULL)");
        seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS memories");
        db.execSQL("DROP TABLE IF EXISTS plans");
        db.execSQL("DROP TABLE IF EXISTS pings");
        onCreate(db);
    }

    // a few starter rows so the lists arent empty the first time you open the app
    private void seed(SQLiteDatabase db) {
        db.insert("memories", null, memoryValues("First coffee date",
                "We talked until the cafe closed and neither of us wanted to leave.",
                "2024-02-14", "Romantic"));
        db.insert("memories", null, memoryValues("Rainy day walk",
                "Shared one umbrella, got soaked anyway, and laughed the whole way home.",
                "2024-06-02", "Happy"));
        db.insert("plans", null, planValues("Sunset picnic", "Riverside park", "2026-12-20", false));
        db.insert("plans", null, planValues("Cook pasta together", "Home", "2026-12-05", false));
    }

    private static ContentValues memoryValues(String title, String note, String date, String mood) {
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("note", note);
        v.put("date", date);
        v.put("mood", mood);
        return v;
    }

    private static ContentValues planValues(String title, String place, String date, boolean done) {
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("place", place);
        v.put("date", date);
        v.put("done", done ? 1 : 0);
        return v;
    }

    // ---- Memories ----

    public long insertMemory(String title, String note, String date, String mood) {
        return getWritableDatabase().insert("memories", null, memoryValues(title, note, date, mood));
    }

    public List<Memory> getMemories() {
        List<Memory> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("memories", null, null, null, null, null,
                "date DESC, id DESC")) {
            while (c.moveToNext()) {
                list.add(readMemory(c));
            }
        }
        return list;
    }

    public Memory getMemory(long id) {
        try (Cursor c = getReadableDatabase().query("memories", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readMemory(c) : null;
        }
    }

    public void deleteMemory(long id) {
        getWritableDatabase().delete("memories", "id = ?", new String[]{String.valueOf(id)});
    }

    private static Memory readMemory(Cursor c) {
        return new Memory(c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("title")),
                c.getString(c.getColumnIndexOrThrow("note")),
                c.getString(c.getColumnIndexOrThrow("date")),
                c.getString(c.getColumnIndexOrThrow("mood")));
    }

    // ---- Plans ----

    public long insertPlan(String title, String place, String date, boolean done) {
        return getWritableDatabase().insert("plans", null, planValues(title, place, date, done));
    }

    public List<Plan> getPlans() {
        List<Plan> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("plans", null, null, null, null, null,
                "done ASC, date ASC, id ASC")) {
            while (c.moveToNext()) {
                list.add(new Plan(c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("title")),
                        c.getString(c.getColumnIndexOrThrow("place")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        c.getInt(c.getColumnIndexOrThrow("done")) == 1));
            }
        }
        return list;
    }

    public void setPlanDone(long id, boolean done) {
        ContentValues v = new ContentValues();
        v.put("done", done ? 1 : 0);
        getWritableDatabase().update("plans", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deletePlan(long id) {
        getWritableDatabase().delete("plans", "id = ?", new String[]{String.valueOf(id)});
    }

    // ---- Pings ----

    public long insertPing(String kind, String message, long sentAt) {
        ContentValues v = new ContentValues();
        v.put("kind", kind);
        v.put("message", message);
        v.put("sent_at", sentAt);
        return getWritableDatabase().insert("pings", null, v);
    }

    public List<Ping> getPings() {
        List<Ping> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pings", null, null, null, null, null,
                "sent_at DESC, id DESC")) {
            while (c.moveToNext()) {
                list.add(new Ping(c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("kind")),
                        c.getString(c.getColumnIndexOrThrow("message")),
                        c.getLong(c.getColumnIndexOrThrow("sent_at"))));
            }
        }
        return list;
    }

    public void clearPings() {
        getWritableDatabase().delete("pings", null, null);
    }
}
