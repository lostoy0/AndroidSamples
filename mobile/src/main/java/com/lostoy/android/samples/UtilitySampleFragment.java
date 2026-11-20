package com.lostoy.android.samples;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.lostoy.android.samples.utils.GAIDUtil;
import com.lostoy.android.samples.utils.OAIDUtil;

import java.math.BigDecimal;

public class UtilitySampleFragment extends BaseFragment {

    private static final String ARG_SAMPLE = "sample";

    static UtilitySampleFragment newInstance(SampleActivity.Sample sample) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_SAMPLE, sample.name());
        UtilitySampleFragment fragment = new UtilitySampleFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_utility_sample;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SampleActivity.Sample sample = SampleActivity.Sample.valueOf(requireArguments().getString(ARG_SAMPLE));
        TextView output = view.findViewById(R.id.sample_output);
        Button runButton = view.findViewById(R.id.run_sample);
        Context applicationContext = requireContext().getApplicationContext();

        switch (sample) {
            case JNI:
                System.loadLibrary("native-lib");
                output.setText(stringFromJNI());
                break;
            case GAID:
                bindReader(runButton, output, R.string.sample_read_gaid, () -> {
                    GAIDUtil.init(applicationContext);
                    GAIDUtil.initByRxJava(applicationContext);
                });
                break;
            case OAID:
                bindReader(runButton, output, R.string.sample_read_oaid, () -> OAIDUtil.init(applicationContext));
                break;
            case NUMBERS:
                float infinity = 1 / 0.0f;
                BigDecimal result = BigDecimal.valueOf(8).divide(BigDecimal.valueOf(2));
                output.setText(getString(R.string.sample_number_output, Float.toString(infinity),
                        Math.abs(infinity) > Integer.MAX_VALUE, result.toPlainString()));
                break;
            default:
                throw new IllegalArgumentException("Expected a utility sample: " + sample);
        }
    }

    private void bindReader(Button button, TextView output, @StringRes int label, Runnable reader) {
        output.setText(R.string.sample_id_description);
        button.setText(label);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(view -> {
            reader.run();
            output.setText(R.string.sample_id_requested);
        });
    }

    private native String stringFromJNI();
}
