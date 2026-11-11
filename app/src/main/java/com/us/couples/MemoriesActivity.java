package com.us.couples;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.List;

// memory list. landscape shows the detail fragment next to the list,
// portrait opens MemoryDetailActivity
public class MemoriesActivity extends BaseActivity
        implements MemoryAdapter.Listener, MemoryDetailFragment.Host {

    private MemoryAdapter adapter;
    private ProgressBar progress;
    private TextView empty;
    private View root;

    @Override
    protected int helpTextRes() {
        return R.string.help_memories;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);
        setUpToolbar();
        root = findViewById(R.id.root);
        progress = findViewById(R.id.progress);
        empty = findViewById(R.id.tv_empty);

        adapter = new MemoryAdapter(this);
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMemories();
    }

    private boolean isTwoPane() {
        return findViewById(R.id.detail_container) != null;
    }

    private void loadMemories() {
        progress.setVisibility(View.VISIBLE);
        Background.run(() -> UsDatabase.get(this).getMemories(), (List<Memory> memories) -> {
            if (isDestroyed()) {
                return;
            }
            progress.setVisibility(View.GONE);
            adapter.submit(memories);
            empty.setVisibility(memories.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onOpen(Memory memory) {
        if (isTwoPane()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, MemoryDetailFragment.newInstance(memory.id))
                    .commit();
        } else {
            Intent intent = new Intent(this, MemoryDetailActivity.class);
            intent.putExtra(MemoryDetailActivity.EXTRA_ID, memory.id);
            startActivity(intent);
        }
    }

    @Override
    public void onDelete(Memory memory) {
        Background.run(() -> {
            UsDatabase.get(this).deleteMemory(memory.id);
            return true;
        }, ok -> {
            if (isDestroyed()) {
                return;
            }
            removeDetailIfShowing();
            loadMemories();
            Snackbar.make(root, R.string.memory_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> restore(memory))
                    .show();
        });
    }

    private void restore(Memory memory) {
        Background.run(() -> UsDatabase.get(this)
                        .insertMemory(memory.title, memory.note, memory.date, memory.mood),
                id -> {
                    if (!isDestroyed()) {
                        loadMemories();
                    }
                });
    }

    @Override
    public void onMemoryDeleted() {
        removeDetailIfShowing();
        loadMemories();
        Snackbar.make(root, R.string.memory_deleted, Snackbar.LENGTH_SHORT).show();
    }

    private void removeDetailIfShowing() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.detail_container);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commit();
        }
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_memory, null);
        TextInputEditText title = view.findViewById(R.id.et_title);
        TextInputEditText note = view.findViewById(R.id.et_note);
        Spinner mood = view.findViewById(R.id.spinner_mood);
        Button dateButton = view.findViewById(R.id.btn_date);

        mood.setAdapter(ArrayAdapter.createFromResource(this, R.array.moods,
                android.R.layout.simple_spinner_dropdown_item));
        final String[] date = {Dates.today()};
        dateButton.setText(Dates.pretty(date[0]));
        dateButton.setOnClickListener(v -> {
            LocalDate d = LocalDate.parse(date[0]);
            new DatePickerDialog(this, (picker, y, m, day) -> {
                date[0] = Dates.iso(y, m, day);
                dateButton.setText(Dates.pretty(date[0]));
            }, d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()).show();
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_memory)
                .setView(view)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String t = title.getText() == null ? "" : title.getText().toString().trim();
            if (t.isEmpty()) {
                title.setError(getString(R.string.error_title_required));
                return;
            }
            String n = note.getText() == null ? "" : note.getText().toString().trim();
            String m = (String) mood.getSelectedItem();
            dialog.dismiss();
            progress.setVisibility(View.VISIBLE);
            Background.run(() -> UsDatabase.get(this).insertMemory(t, n, date[0], m), id -> {
                if (isDestroyed()) {
                    return;
                }
                Toast.makeText(this, R.string.memory_saved, Toast.LENGTH_SHORT).show();
                loadMemories();
            });
        });
    }
}
