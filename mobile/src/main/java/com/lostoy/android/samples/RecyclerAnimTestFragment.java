package com.lostoy.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAnimTestFragment extends BaseFragment {

    private static final String STATE_ITEM_COUNT = "item_count";

    private int itemCount = 5;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_recycler_anim;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            itemCount = savedInstanceState.getInt(STATE_ITEM_COUNT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ItemAdapter adapter = new ItemAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        View removeButton = view.findViewById(R.id.remove_recycler_item);
        removeButton.setEnabled(itemCount > 0);
        view.findViewById(R.id.add_recycler_item).setOnClickListener(button -> {
            int position = itemCount++;
            adapter.notifyItemInserted(position);
            recyclerView.smoothScrollToPosition(position);
            removeButton.setEnabled(true);
        });
        removeButton.setOnClickListener(button -> {
            int position = --itemCount;
            adapter.notifyItemRemoved(position);
            removeButton.setEnabled(itemCount > 0);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ITEM_COUNT, itemCount);
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView row = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ItemHolder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            TextView row = (TextView) holder.itemView;
            row.setText(getString(R.string.sample_recycler_item, position + 1));
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {
        ItemHolder(TextView row) {
            super(row);
        }
    }
}
