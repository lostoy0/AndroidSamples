package com.lostoy.android.samples.device

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lostoy.android.samples.R

class AdvertisingIdSampleFragment : Fragment(R.layout.fragment_utility_sample) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleId = requireArguments().getBoolean(ARG_GOOGLE_ID)
        val output = view.findViewById<TextView>(R.id.sample_output)
        val button = view.findViewById<Button>(R.id.run_sample)
        val appContext = requireContext().applicationContext

        output.setText(R.string.sample_id_description)
        button.setText(if (googleId) R.string.sample_read_gaid else R.string.sample_read_oaid)
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            if (googleId) {
                GoogleAdvertisingIdSample.init(appContext)
                GoogleAdvertisingIdSample.initByRxJava(appContext)
            } else {
                OaidSample.init(appContext)
            }
            output.setText(R.string.sample_id_requested)
        }
    }

    companion object {
        private const val ARG_GOOGLE_ID = "google_id"

        internal fun newInstance(googleId: Boolean) = AdvertisingIdSampleFragment().apply {
            arguments = Bundle().apply { putBoolean(ARG_GOOGLE_ID, googleId) }
        }
    }
}
