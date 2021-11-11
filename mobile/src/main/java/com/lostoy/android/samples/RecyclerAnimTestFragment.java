package com.lostoy.android.samples;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class RecyclerAnimTestFragment extends BaseFragment {

    private RecyclerView recyclerView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_recycler_anim;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = findViewById(R.id.recyclerView);
    }
}
