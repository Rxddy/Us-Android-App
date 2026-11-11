package com.us.couples;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// full view of one memory + delete button
public class MemoryDetailFragment extends Fragment {
    private static final String ARG_ID = "memory_id";

    // the activity implements this so it knows when we delete
    public interface Host {
        void onMemoryDeleted();
    }

    private Host host;
    private ProgressBar progress;
    private View content;
    private TextView title;
    private TextView meta;
    private TextView note;
    private Button delete;

    public static MemoryDetailFragment newInstance(long memoryId) {
        MemoryDetailFragment fragment = new MemoryDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, memoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Host) {
            host = (Host) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memory_detail, container, false);
        progress = view.findViewById(R.id.progress);
        content = view.findViewById(R.id.content);
        title = view.findViewById(R.id.tv_title);
        meta = view.findViewById(R.id.tv_meta);
        note = view.findViewById(R.id.tv_note);
        delete = view.findViewById(R.id.btn_delete);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        long id = requireArguments().getLong(ARG_ID, -1);
        Context appContext = requireContext().getApplicationContext();
        content.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        Background.run(() -> UsDatabase.get(appContext).getMemory(id), memory -> {
            if (!isAdded()) {
                return;
            }
            progress.setVisibility(View.GONE);
            if (memory == null) {
                Toast.makeText(requireContext(), R.string.memory_not_found, Toast.LENGTH_SHORT).show();
                if (host != null) {
                    host.onMemoryDeleted();
                }
                return;
            }
            title.setText(memory.title);
            meta.setText(getString(R.string.memory_meta, Dates.pretty(memory.date), memory.mood));
            note.setText(memory.note == null || memory.note.isEmpty()
                    ? getString(R.string.memory_no_note) : memory.note);
            content.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> deleteMemory(appContext, id));
        });
    }

    private void deleteMemory(Context appContext, long id) {
        delete.setEnabled(false);
        Background.run(() -> {
            UsDatabase.get(appContext).deleteMemory(id);
            return true;
        }, ok -> {
            if (isAdded() && host != null) {
                host.onMemoryDeleted();
            }
        });
    }
}
