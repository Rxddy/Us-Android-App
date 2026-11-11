package com.us.couples;

import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MenuRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

// every screen extends this, sets up the toolbar + help button
public abstract class BaseActivity extends AppCompatActivity {

    @StringRes
    protected abstract int helpTextRes();

    @MenuRes
    protected int menuRes() {
        return R.menu.menu_help;
    }

    protected boolean showUpButton() {
        return true;
    }

    protected void setUpToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (showUpButton() && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        applySystemBarInsets(toolbar);
    }

    // android 15+ draws behind the status bar, so push the toolbar down
    // and keep the content above the nav bar / keyboard
    private void applySystemBarInsets(MaterialToolbar toolbar) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, value, true);
        final int barHeight = TypedValue.complexToDimensionPixelSize(
                value.data, getResources().getDisplayMetrics());

        View content = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            toolbar.setPadding(bars.left, bars.top, bars.right, 0);
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            params.height = barHeight + bars.top;
            toolbar.setLayoutParams(params);
            view.setPadding(0, 0, 0, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menuRes(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_help) {
            HelpDialog.show(this, getTitle(), helpTextRes());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
