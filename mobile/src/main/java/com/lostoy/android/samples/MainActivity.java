package com.lostoy.android.samples;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.example.flutter.bridge.BridgeException;
import com.example.flutter.bridge.BridgeSession;
import com.example.flutter.bridge.HybridTabFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String FLUTTER_TAB = "flutter-tab";
    private static final String SHOW_FLUTTER = "show-flutter";

    private HybridTabFragment flutterTab;
    private boolean showFlutter;
    private boolean awaitingBack;
    private boolean resumed;
    private OnBackPressedCallback flutterBack;
    private ListView samples;
    private View flutterPanel;
    private RadioGroup tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.sample_catalog_title);
        setContentView(R.layout.activity_main);
        samples = findViewById(R.id.sample_list);
        flutterPanel = findViewById(R.id.flutter_tab_panel);
        tabs = findViewById(R.id.home_tabs);
        flutterTab = (HybridTabFragment) getSupportFragmentManager().findFragmentByTag(FLUTTER_TAB);
        installCatalog();

        flutterBack = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (flutterTab == null || awaitingBack) return;
                awaitingBack = true;
                flutterTab.maybePop().whenComplete((popped, error) -> {
                    awaitingBack = false;
                    if (resumed && showFlutter && !Boolean.TRUE.equals(popped)) {
                        tabs.check(R.id.home_native_tab);
                    }
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, flutterBack);
        tabs.setOnCheckedChangeListener((group, selected) -> selectTab(selected == R.id.home_flutter_tab));
        tabs.check(savedInstanceState != null && savedInstanceState.getBoolean(SHOW_FLUTTER)
                ? R.id.home_flutter_tab : R.id.home_native_tab);
        findViewById(R.id.hybrid_call_dart).setOnClickListener(view -> callDart());
    }

    private void installCatalog() {
        List<String> labels = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        for (SampleActivity.Sample sample : SampleActivity.Sample.values()) {
            labels.add(getString(sample.titleRes));
            urls.add("samples://native/samples/" + sample.slug);
        }
        labels.add(getString(R.string.hybrid_open_flutter));
        urls.add(HybridSamples.FLUTTER_DETAIL);
        labels.add(getString(R.string.hybrid_open_communications));
        urls.add(HybridSamples.COMMUNICATIONS);
        samples.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
        samples.setOnItemClickListener((parent, view, position, id) ->
                HybridSamples.open(this, urls.get(position)));
    }

    private void selectTab(boolean flutter) {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.isStateSaved()) return;
        showFlutter = flutter;
        updateTabVisibility();
        if (flutter || flutterTab != null) {
            FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);
            if (flutterTab == null) {
                flutterTab = HybridTabFragment.create(HybridSamples.TAB);
                transaction.add(R.id.flutter_tab_container, flutterTab, FLUTTER_TAB);
            }
            if (flutter) transaction.show(flutterTab);
            else transaction.hide(flutterTab);
            transaction.setMaxLifecycle(flutterTab, flutter ? Lifecycle.State.RESUMED : Lifecycle.State.STARTED);
            transaction.commitNow();
            if (flutter && resumed) flutterTab.onPostResume();
        }
        flutterBack.setEnabled(flutter);
    }

    private void updateTabVisibility() {
        samples.setVisibility(showFlutter ? View.GONE : View.VISIBLE);
        flutterPanel.setVisibility(showFlutter ? View.VISIBLE : View.GONE);
    }

    private void callDart() {
        TextView result = findViewById(R.id.hybrid_dart_result);
        result.setVisibility(View.VISIBLE);
        try {
            BridgeSession bridge = flutterTab.bridge();
            bridge.invoke(HybridSamples.FORMAT, "Android")
                    .thenCombine(bridge.send(Collections.singletonMap("text", "Android")),
                            (reply, message) -> getString(R.string.hybrid_dart_result, reply, message.get("echo")))
                    .whenComplete((value, error) -> {
                        if (!isDestroyed()) result.setText(error == null ? value : errorText(error));
                    });
        } catch (BridgeException error) {
            result.setText(errorText(error));
        }
    }

    private static String errorText(Throwable error) {
        Throwable cause = error.getCause();
        return (cause == null ? error : cause).getMessage();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        resumed = true;
        if (showFlutter && flutterTab != null) flutterTab.onPostResume();
    }

    @Override
    protected void onPause() {
        resumed = false;
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (flutterTab != null) flutterTab.onNewIntent(intent);
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (showFlutter && flutterTab != null) flutterTab.onUserLeaveHint();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (flutterTab != null) flutterTab.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (flutterTab != null) flutterTab.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(SHOW_FLUTTER, showFlutter);
        super.onSaveInstanceState(outState);
    }
}
