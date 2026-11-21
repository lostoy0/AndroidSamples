package com.lostoy.android.samples.hybrid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.flutter.bridge.RouteTable
import com.lostoy.android.samples.R

class HybridNativeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.hybrid_native_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_hybrid_native_detail)

        val id = intent.getIntExtra(ID, 0)
        val title = intent.getStringExtra(TITLE).orEmpty()
        findViewById<TextView>(R.id.hybrid_route_parameters).text =
            getString(R.string.hybrid_route_parameters, id, title)

        findViewById<View>(R.id.hybrid_open_flutter).setOnClickListener {
            val url = Uri.Builder().scheme("samples").authority("flutter")
                .appendPath("detail").appendPath(id.toString())
                .appendQueryParameter("title", "来自 Android")
                .build()
            HybridSampleRoutes.open(this, url.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val ID = "id"
        private const val TITLE = "title"

        fun intent(context: Context, route: RouteTable.Request): Intent =
            Intent(context, HybridNativeDetailActivity::class.java)
                .putExtra(ID, route.positiveInt(ID))
                .putExtra(TITLE, route.query[TITLE])
    }
}
