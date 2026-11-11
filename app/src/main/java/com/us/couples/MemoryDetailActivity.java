package com.us.couples;

import android.os.Bundle;

// just holds the detail fragment on phones
public class MemoryDetailActivity extends BaseActivity implements MemoryDetailFragment.Host {
    public static final String EXTRA_ID = "memory_id";

    @Override
    protected int helpTextRes() {
        return R.string.help_memory_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);
        setUpToolbar();
        if (savedInstanceState == null) {
            long id = getIntent().getLongExtra(EXTRA_ID, -1);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, MemoryDetailFragment.newInstance(id))
                    .commit();
        }
    }

    @Override
    public void onMemoryDeleted() {
        finish();
    }
}
