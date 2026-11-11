package com.us.couples;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// recyclerview adapter for memories
public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.Holder> {

    public interface Listener {
        void onOpen(Memory memory);

        void onDelete(Memory memory);
    }

    private final List<Memory> items = new ArrayList<>();
    private final Listener listener;

    public MemoryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<Memory> memories) {
        items.clear();
        items.addAll(memories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Memory m = items.get(position);
        holder.title.setText(m.title);
        holder.meta.setText(holder.itemView.getContext()
                .getString(R.string.memory_meta, Dates.pretty(m.date), m.mood));
        holder.note.setText(m.note);
        holder.itemView.setOnClickListener(v -> listener.onOpen(m));
        holder.delete.setOnClickListener(v -> listener.onDelete(m));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView meta;
        final TextView note;
        final View delete;

        Holder(View view) {
            super(view);
            title = view.findViewById(R.id.item_title);
            meta = view.findViewById(R.id.item_meta);
            note = view.findViewById(R.id.item_note);
            delete = view.findViewById(R.id.item_delete);
        }
    }
}
