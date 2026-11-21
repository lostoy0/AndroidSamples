package com.lostoy.android.samples.catalog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lostoy.android.samples.R

class SampleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sampleName = intent.getStringExtra(EXTRA_SAMPLE)
        val sample = Sample.entries.firstOrNull { it.name == sampleName }
        if (sample == null) {
            finish()
            return
        }

        setTitle(sample.titleRes)
        setContentView(R.layout.activity_sample)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.sample_container, sample.createFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val EXTRA_SAMPLE = "sample"

        @JvmStatic
        internal fun createIntent(context: Context, sample: Sample): Intent {
            return Intent(context, SampleDetailActivity::class.java).putExtra(EXTRA_SAMPLE, sample.name)
        }
    }
}
