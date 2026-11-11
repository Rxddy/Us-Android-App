package com.us.couples;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// runs stuff on a background thread, then hands the result back on the main thread.
// use it for any db work so the ui doesnt freeze
public final class Background {
    private static final String TAG = "Background";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private Background() {
    }

    public interface Task<T> {
        T run() throws Exception;
    }

    public interface Callback<T> {
        void onResult(T result);

        default void onError(Exception e) {
            Log.e(TAG, "Background task failed", e);
        }
    }

    public static <T> void run(Task<T> task, Callback<T> callback) {
        EXECUTOR.execute(() -> {
            try {
                T result = task.run();
                MAIN.post(() -> callback.onResult(result));
            } catch (Exception e) {
                MAIN.post(() -> callback.onError(e));
            }
        });
    }
}
