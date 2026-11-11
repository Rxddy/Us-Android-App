package com.us.couples;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;

// home screen: the 4 cards + the toolbar icons
public class MainActivity extends BaseActivity {
    static final String PREFS = "us_prefs";
    static final String KEY_ANNIVERSARY = "anniversary";

    private TextView mainDays;
    private TextView mainUnit;
    private TextView mainHint;
    private ProgressBar mainProgress;

    @Override
    protected int helpTextRes() {
        return R.string.help_main;
    }

    @Override
    protected int menuRes() {
        return R.menu.menu_main;
    }

    @Override
    protected boolean showUpButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        mainDays = findViewById(R.id.tv_main_days);
        mainUnit = findViewById(R.id.tv_main_unit);
        mainHint = findViewById(R.id.tv_main_hint);
        mainProgress = findViewById(R.id.progress_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.wordmark);
        }

        findViewById(R.id.card_memories).setOnClickListener(v -> open(MemoriesActivity.class));
        findViewById(R.id.card_plans).setOnClickListener(v -> open(PlansActivity.class));
        findViewById(R.id.card_ping).setOnClickListener(v -> open(PingActivity.class));
        findViewById(R.id.card_days).setOnClickListener(v -> open(OurDaysActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        LocalDate start = Dates.parse(prefs.getString(KEY_ANNIVERSARY, null));
        if (start == null) {
            mainDays.setText("-");
            mainUnit.setVisibility(View.GONE);
            mainProgress.setVisibility(View.GONE);
            mainHint.setText(R.string.summary_no_date);
        } else {
            long days = DaysCalculator.daysTogether(start, LocalDate.now());
            long next = DaysCalculator.nextMilestone(days);
            mainDays.setText(String.valueOf(days));
            mainUnit.setVisibility(View.VISIBLE);
            mainProgress.setVisibility(View.VISIBLE);
            mainProgress.setMax(DaysCalculator.MILESTONE_STEP);
            mainProgress.setProgress(DaysCalculator.milestoneProgress(days));
            mainHint.setText(getString(R.string.summary_progress, next - days, next));
        }
    }

    private void open(Class<?> screen) {
        startActivity(new Intent(this, screen));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_memories) {
            open(MemoriesActivity.class);
            return true;
        } else if (id == R.id.action_plans) {
            open(PlansActivity.class);
            return true;
        } else if (id == R.id.action_ping) {
            open(PingActivity.class);
            return true;
        } else if (id == R.id.action_days) {
            open(OurDaysActivity.class);
            return true;
        } else if (id == R.id.action_language) {
            showLanguageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        String[] tags = {"en-US", "en-GB"};
        String[] labels = {getString(R.string.language_us), getString(R.string.language_gb)};
        LocaleListCompat current = AppCompatDelegate.getApplicationLocales();
        int checked = !current.isEmpty() && "GB".equals(current.get(0).getCountry()) ? 1 : 0;
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.action_language)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(tags[which]));
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
