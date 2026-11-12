package com.us.couples;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// recyclerview adapter for plans
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.Holder> {

    public interface Listener {
        void onOpen(Plan plan);

        void onToggleDone(Plan plan, boolean done);

        void onDelete(Plan plan);
    }

    private final List<Plan> items = new ArrayList<>();
    private final Listener listener;

    public PlanAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<Plan> plans) {
        items.clear();
        items.addAll(plans);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Plan p = items.get(position);
        holder.title.setText(p.title);
        String when = Dates.pretty(p.date);
        holder.meta.setText(p.place == null || p.place.isEmpty()
                ? when
                : holder.itemView.getContext().getString(R.string.plan_meta, when, p.place));
        holder.done.setOnCheckedChangeListener(null);
        holder.done.setChecked(p.done);
        holder.done.setOnCheckedChangeListener((button, checked) -> listener.onToggleDone(p, checked));
        holder.itemView.setOnClickListener(v -> listener.onOpen(p));
        holder.delete.setOnClickListener(v -> listener.onDelete(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final CheckBox done;
        final TextView title;
        final TextView meta;
        final View delete;

        Holder(View view) {
            super(view);
            done = view.findViewById(R.id.item_done);
            title = view.findViewById(R.id.item_title);
            meta = view.findViewById(R.id.item_meta);
            delete = view.findViewById(R.id.item_delete);
        }
    }
}
