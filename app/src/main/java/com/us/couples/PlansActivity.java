package com.us.couples;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.List;

// plans list
public class PlansActivity extends BaseActivity implements PlanAdapter.Listener {

    private PlanAdapter adapter;
    private ProgressBar progress;
    private TextView empty;
    private View root;

    @Override
    protected int helpTextRes() {
        return R.string.help_plans;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        setUpToolbar();
        root = findViewById(R.id.root);
        progress = findViewById(R.id.progress);
        empty = findViewById(R.id.tv_empty);

        adapter = new PlanAdapter(this);
        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        findViewById(R.id.fab_add).setOnClickListener(v -> showAddDialog());
        loadPlans();
    }

    private void loadPlans() {
        progress.setVisibility(View.VISIBLE);
        Background.run(() -> UsDatabase.get(this).getPlans(), (List<Plan> plans) -> {
            if (isDestroyed()) {
                return;
            }
            progress.setVisibility(View.GONE);
            adapter.submit(plans);
            empty.setVisibility(plans.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onOpen(Plan plan) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_plan_detail, null);
        ((TextView) view.findViewById(R.id.tv_when)).setText(Dates.pretty(plan.date));
        ((TextView) view.findViewById(R.id.tv_where)).setText(plan.place == null || plan.place.isEmpty()
                ? getString(R.string.plan_no_place) : plan.place);
        ((TextView) view.findViewById(R.id.tv_status)).setText(
                plan.done ? R.string.plan_status_done : R.string.plan_status_upcoming);
        new MaterialAlertDialogBuilder(this)
                .setTitle(plan.title)
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    public void onToggleDone(Plan plan, boolean done) {
        Background.run(() -> {
            UsDatabase.get(this).setPlanDone(plan.id, done);
            return true;
        }, ok -> {
            if (!isDestroyed()) {
                loadPlans();
            }
        });
    }

    @Override
    public void onDelete(Plan plan) {
        Background.run(() -> {
            UsDatabase.get(this).deletePlan(plan.id);
            return true;
        }, ok -> {
            if (isDestroyed()) {
                return;
            }
            loadPlans();
            Snackbar.make(root, R.string.plan_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> Background.run(
                            () -> UsDatabase.get(this).insertPlan(plan.title, plan.place, plan.date, plan.done),
                            id -> {
                                if (!isDestroyed()) {
                                    loadPlans();
                                }
                            }))
                    .show();
        });
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_plan, null);
        TextInputEditText title = view.findViewById(R.id.et_title);
        TextInputEditText place = view.findViewById(R.id.et_place);
        Button dateButton = view.findViewById(R.id.btn_date);

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
                .setTitle(R.string.add_plan)
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
            String p = place.getText() == null ? "" : place.getText().toString().trim();
            dialog.dismiss();
            progress.setVisibility(View.VISIBLE);
            Background.run(() -> UsDatabase.get(this).insertPlan(t, p, date[0], false), id -> {
                if (isDestroyed()) {
                    return;
                }
                Toast.makeText(this, R.string.plan_saved, Toast.LENGTH_SHORT).show();
                loadPlans();
            });
        });
    }
}
