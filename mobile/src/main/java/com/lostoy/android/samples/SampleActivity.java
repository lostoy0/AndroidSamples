package com.lostoy.android.samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public class SampleActivity extends BaseActivity {

    private static final String EXTRA_SAMPLE = "sample";

    enum Sample {
        INPUT(R.string.sample_input),
        FLOW(R.string.sample_flow),
        RECYCLER_ANIMATION(R.string.sample_recycler_animation),
        PROGRESS(R.string.sample_progress),
        JNI(R.string.sample_jni),
        GAID(R.string.sample_gaid),
        OAID(R.string.sample_oaid),
        NUMBERS(R.string.sample_numbers);

        @StringRes
        final int titleRes;

        Sample(@StringRes int titleRes) {
            this.titleRes = titleRes;
        }

        Fragment createFragment() {
            switch (this) {
                case INPUT:
                    return ExperimentFragment.newInstance();
                case FLOW:
                    return new FlowTestFragment();
                case RECYCLER_ANIMATION:
                    return new RecyclerAnimTestFragment();
                case PROGRESS:
                    return new ProgressBarFragment();
                default:
                    return UtilitySampleFragment.newInstance(this);
            }
        }
    }

    static Intent createIntent(Context context, Sample sample) {
        return new Intent(context, SampleActivity.class).putExtra(EXTRA_SAMPLE, sample.name());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sample sample = Sample.valueOf(getIntent().getStringExtra(EXTRA_SAMPLE));

        setTitle(sample.titleRes);
        setContentView(R.layout.activity_sample);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sample_container, sample.createFragment())
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
