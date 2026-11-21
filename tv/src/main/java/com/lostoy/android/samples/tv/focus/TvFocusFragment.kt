package com.lostoy.android.samples.tv.focus

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lostoy.android.samples.R

class TvFocusFragment : Fragment(R.layout.fragment_tv_demo) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.descriptionTextView).ellipsize = TextUtils.TruncateAt.END
        view.findViewById<View>(R.id.playButton).requestFocus()
        val recycler = view.findViewById<FocusedItemRecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recycler.adapter = FocusRowAdapter(recycler)
    }

    private class FocusRowAdapter(private val recycler: RecyclerView) :
        RecyclerView.Adapter<FocusRowAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val item = LayoutInflater.from(parent.context).inflate(R.layout.tv_item_movie, parent, false)
            return Holder(item)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.itemView.setOnFocusChangeListener { view, hasFocus ->
                val scale = if (hasFocus) 1.2f else 1f
                view.animate().scaleX(scale).scaleY(scale).start()
                if (hasFocus) recycler.invalidate()
            }
        }

        override fun getItemCount() = 20

        class Holder(item: View) : RecyclerView.ViewHolder(item)
    }
}
