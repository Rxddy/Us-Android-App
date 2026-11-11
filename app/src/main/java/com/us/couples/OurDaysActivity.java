package com.us.couples;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;

// days together + progress to the next 100 day milestone
public class OurDaysActivity extends BaseActivity {

    private SharedPreferences prefs;
    private TextView daysCount;
    private TextView detail;
    private TextView milestone;
    private ProgressBar milestoneBar;

    @Override
    protected int helpTextRes() {
        return R.string.help_days;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_days);
        setUpToolbar();
        prefs = getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
        daysCount = findViewById(R.id.tv_days);
        detail = findViewById(R.id.tv_detail);
        milestone = findViewById(R.id.tv_milestone);
        milestoneBar = findViewById(R.id.progress_milestone);
        findViewById(R.id.btn_set_date).setOnClickListener(v -> pickDate());
        refresh();
    }

    private void pickDate() {
        LocalDate current = Dates.parse(prefs.getString(MainActivity.KEY_ANNIVERSARY, null));
        if (current == null) {
            current = LocalDate.now();
        }
        new DatePickerDialog(this, (picker, y, m, d) -> {
            LocalDate chosen = LocalDate.of(y, m + 1, d);
            if (chosen.isAfter(LocalDate.now())) {
                Snackbar.make(findViewById(R.id.root), R.string.error_future_date,
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            prefs.edit().putString(MainActivity.KEY_ANNIVERSARY, chosen.toString()).apply();
            Toast.makeText(this, R.string.anniversary_saved, Toast.LENGTH_SHORT).show();
            refresh();
        }, current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth()).show();
    }

    private void refresh() {
        LocalDate start = Dates.parse(prefs.getString(MainActivity.KEY_ANNIVERSARY, null));
        if (start == null) {
            daysCount.setText("-");
            detail.setText(R.string.days_not_set);
            milestone.setVisibility(View.INVISIBLE);
            milestoneBar.setVisibility(View.INVISIBLE);
            return;
        }
        LocalDate today = LocalDate.now();
        long days = DaysCalculator.daysTogether(start, today);
        long untilAnniversary = DaysCalculator.daysUntilAnniversary(start, today);
        daysCount.setText(String.valueOf(days));
        detail.setText(getString(R.string.days_detail, Dates.pretty(start.toString()), untilAnniversary));
        milestone.setVisibility(View.VISIBLE);
        milestone.setText(getString(R.string.days_milestone, DaysCalculator.nextMilestone(days)));
        milestoneBar.setVisibility(View.VISIBLE);
        milestoneBar.setMax(DaysCalculator.MILESTONE_STEP);
        milestoneBar.setProgress(DaysCalculator.milestoneProgress(days));
    }
}
