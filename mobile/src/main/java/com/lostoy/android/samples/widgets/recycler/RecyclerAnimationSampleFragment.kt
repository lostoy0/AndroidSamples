package com.lostoy.android.samples.widgets.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lostoy.android.samples.R

class RecyclerAnimationSampleFragment : Fragment(R.layout.fragment_recycler_anim) {
    private var sampleItemCount = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            sampleItemCount = savedInstanceState.getInt(STATE_ITEM_COUNT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = ItemAdapter()
        recyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerView.setAdapter(adapter)

        val removeButton = view.findViewById<View>(R.id.remove_recycler_item)
        removeButton.setEnabled(sampleItemCount > 0)
        view.findViewById<View?>(R.id.add_recycler_item)
            .setOnClickListener(View.OnClickListener { button: View? ->
                val position = sampleItemCount++
                adapter.notifyItemInserted(position)
                recyclerView.smoothScrollToPosition(position)
                removeButton.setEnabled(true)
            })
        removeButton.setOnClickListener(View.OnClickListener { button: View? ->
            val position = --sampleItemCount
            adapter.notifyItemRemoved(position)
            removeButton.setEnabled(sampleItemCount > 0)
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_ITEM_COUNT, sampleItemCount)
    }

    private inner class ItemAdapter : RecyclerView.Adapter<ItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val row = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            return ItemHolder(row)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val row = holder.itemView as TextView
            row.setText(getString(R.string.sample_recycler_item, position + 1))
        }

        override fun getItemCount(): Int {
            return sampleItemCount
        }
    }

    private class ItemHolder(row: TextView) : RecyclerView.ViewHolder(row)
    companion object {
        private const val STATE_ITEM_COUNT = "item_count"
    }
}
