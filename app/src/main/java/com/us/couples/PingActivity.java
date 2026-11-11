package com.us.couples;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

// send a ping. for now they only save to the local db,
// actually sending to the partner is for later
public class PingActivity extends BaseActivity {

    private PingAdapter adapter;
    private ProgressBar progress;
    private EditText note;
    private TextView empty;
    private View root;
    private View[] buttons;

    @Override
    protected int helpTextRes() {
        return R.string.help_ping;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        setUpToolbar();
        root = findViewById(R.id.root);
        progress = findViewById(R.id.progress);
        note = findViewById(R.id.et_note);
        empty = findViewById(R.id.tv_empty);

        adapter = new PingAdapter();
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        View thinking = findViewById(R.id.btn_thinking);
        View missYou = findViewById(R.id.btn_miss_you);
        View hug = findViewById(R.id.btn_hug);
        buttons = new View[]{thinking, missYou, hug};
        thinking.setOnClickListener(v -> send(Ping.THINKING));
        missYou.setOnClickListener(v -> send(Ping.MISS_YOU));
        hug.setOnClickListener(v -> send(Ping.HUG));
        findViewById(R.id.btn_clear).setOnClickListener(v -> clearHistory());

        note.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                send(Ping.THINKING);
                return true;
            }
            return false;
        });
        loadPings();
    }

    private void setBusy(boolean busy) {
        progress.setVisibility(busy ? View.VISIBLE : View.GONE);
        for (View b : buttons) {
            b.setEnabled(!busy);
        }
    }

    private void loadPings() {
        Background.run(() -> UsDatabase.get(this).getPings(), (List<Ping> pings) -> {
            if (isDestroyed()) {
                return;
            }
            adapter.submit(pings);
            empty.setVisibility(pings.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void send(String kind) {
        String message = note.getText().toString().trim();
        setBusy(true);
        Background.run(() -> {
            UsDatabase.get(this).insertPing(kind, message, System.currentTimeMillis());
            return UsDatabase.get(this).getPings();
        }, (List<Ping> pings) -> {
            if (isDestroyed()) {
                return;
            }
            setBusy(false);
            note.setText("");
            adapter.submit(pings);
            empty.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.ping_sent, getString(PingAdapter.labelFor(kind))),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void clearHistory() {
        Background.run(() -> {
            UsDatabase.get(this).clearPings();
            return true;
        }, ok -> {
            if (isDestroyed()) {
                return;
            }
            loadPings();
            Snackbar.make(root, R.string.ping_history_cleared, Snackbar.LENGTH_SHORT).show();
        });
    }
}
