package com.lostoy.android.samples.tools

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lostoy.android.samples.R
import com.lostoy.android.samples.catalog.Sample
import java.math.BigDecimal
import kotlin.math.abs

class ToolSampleFragment : Fragment(R.layout.fragment_utility_sample) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val output = view.findViewById<TextView>(R.id.sample_output)
        when (val sample = Sample.valueOf(requireArguments().getString(ARG_SAMPLE)!!)) {
            Sample.JNI -> {
                System.loadLibrary("native-lib")
                output.text = stringFromJNI()
            }
            Sample.NUMBERS -> {
                val infinity = 1 / 0.0f
                val result = BigDecimal.valueOf(8).divide(BigDecimal.valueOf(2))
                output.text = getString(
                    R.string.sample_number_output,
                    infinity.toString(),
                    abs(infinity) > Int.MAX_VALUE,
                    result.toPlainString()
                )
            }
            else -> error("Expected a tool sample: $sample")
        }
    }

    private external fun stringFromJNI(): String

    companion object {
        private const val ARG_SAMPLE = "sample"

        internal fun newInstance(sample: Sample) = ToolSampleFragment().apply {
            arguments = Bundle().apply { putString(ARG_SAMPLE, sample.name) }
        }
    }
}
