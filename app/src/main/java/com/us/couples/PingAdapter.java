package com.us.couples;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// recyclerview adapter for the ping history
public class PingAdapter extends RecyclerView.Adapter<PingAdapter.Holder> {
    private final List<Ping> items = new ArrayList<>();

    public void submit(List<Ping> pings) {
        items.clear();
        items.addAll(pings);
        notifyDataSetChanged();
    }

    static int labelFor(String kind) {
        switch (kind) {
            case Ping.MISS_YOU:
                return R.string.ping_miss_you;
            case Ping.HUG:
                return R.string.ping_hug;
            default:
                return R.string.ping_thinking;
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ping, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Ping p = items.get(position);
        Context context = holder.itemView.getContext();
        holder.kind.setText(labelFor(p.kind));
        holder.time.setText(DateUtils.getRelativeTimeSpanString(p.sentAt));
        boolean hasMessage = p.message != null && !p.message.isEmpty();
        holder.message.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
        holder.message.setText(hasMessage ? context.getString(R.string.ping_note_quote, p.message) : "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView kind;
        final TextView time;
        final TextView message;

        Holder(View view) {
            super(view);
            kind = view.findViewById(R.id.item_title);
            time = view.findViewById(R.id.item_meta);
            message = view.findViewById(R.id.item_note);
        }
    }
}
