package com.lostoy.android.samples;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.example.flutter.bridge.RouteTable;

public final class HybridNativeDetailActivity extends BaseActivity {
    private static final String ID = "id";
    private static final String TITLE = "title";

    static Intent intent(Context context, RouteTable.Request route) {
        return new Intent(context, HybridNativeDetailActivity.class)
                .putExtra(ID, route.positiveInt(ID))
                .putExtra(TITLE, route.query.get(TITLE));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.hybrid_native_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_hybrid_native_detail);
        int id = getIntent().getIntExtra(ID, 0);
        String title = getIntent().getStringExtra(TITLE);
        TextView parameters = findViewById(R.id.hybrid_route_parameters);
        parameters.setText(getString(R.string.hybrid_route_parameters, id, title == null ? "" : title));
        findViewById(R.id.hybrid_open_flutter).setOnClickListener(view -> {
            Uri url = new Uri.Builder().scheme("samples").authority("flutter")
                    .appendPath("detail").appendPath(Integer.toString(id))
                    .appendQueryParameter("title", "来自 Android").build();
            HybridSamples.open(this, url.toString());
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
