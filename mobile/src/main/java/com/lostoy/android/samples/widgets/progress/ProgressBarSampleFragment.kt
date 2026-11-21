package com.lostoy.android.samples.widgets.progress

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.lostoy.android.samples.R
import kotlin.random.Random

class ProgressBarSampleFragment : Fragment(R.layout.fragment_progressbar) {
    private val handler = Handler(Looper.getMainLooper())
    private var progressBar: CustomHorizontalProgressBar? = null

    private val advanceProgress = object : Runnable {
        override fun run() {
            val bar = progressBar ?: return
            bar.progress += 0.05f
            if (bar.progress < 1f) handler.postDelayed(this, Random.nextLong(1000))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bar = view.findViewById<CustomHorizontalProgressBar>(R.id.progressBar)
        progressBar = bar
        view.findViewById<View>(R.id.startButton).setOnClickListener {
            handler.removeCallbacks(advanceProgress)
            bar.reset()
            handler.postDelayed(advanceProgress, 3000)
        }
    }

    override fun onDestroyView() {
        handler.removeCallbacks(advanceProgress)
        progressBar = null
        super.onDestroyView()
    }
}
