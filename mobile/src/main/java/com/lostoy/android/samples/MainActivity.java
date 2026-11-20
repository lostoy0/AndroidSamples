package com.lostoy.android.samples;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.sample_catalog_title);
        setContentView(R.layout.activity_main);

        SampleActivity.Sample[] samples = SampleActivity.Sample.values();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (SampleActivity.Sample sample : samples) {
            adapter.add(getString(sample.titleRes));
        }

        ListView sampleList = findViewById(R.id.sample_list);
        sampleList.setAdapter(adapter);
        sampleList.setOnItemClickListener((parent, view, position, id) ->
                startActivity(SampleActivity.createIntent(this, samples[position])));
    }
}
