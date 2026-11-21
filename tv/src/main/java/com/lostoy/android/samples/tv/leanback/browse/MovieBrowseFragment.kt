/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lostoy.android.samples.tv.leanback.browse

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lostoy.android.samples.R
import com.lostoy.android.samples.tv.leanback.catalog.Movie
import com.lostoy.android.samples.tv.leanback.catalog.SampleMovieCatalog
import com.lostoy.android.samples.tv.leanback.details.MovieDetailsActivity
import com.lostoy.android.samples.tv.leanback.error.BrowseErrorActivity
import com.lostoy.android.samples.tv.leanback.presenter.MovieCardPresenter
import java.net.URI
import java.util.Collections
import java.util.Timer
import java.util.TimerTask

class MovieBrowseFragment : BrowseFragment() {
    private val mHandler = Handler()
    private var mRowsAdapter: ArrayObjectAdapter? = null
    private var mDefaultBackground: Drawable? = null
    private var mMetrics: DisplayMetrics? = null
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundURI: URI? = null
    private var mBackgroundManager: BackgroundManager? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

        prepareBackgroundManager()

        setupUIElements()

        loadRows()

        setupEventListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString())
            mBackgroundTimer!!.cancel()
        }
    }

    private fun loadRows() {
        val list = SampleMovieCatalog.setupMovies()

        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = MovieCardPresenter()

        var i: Int
        i = 0
        while (i < NUM_ROWS) {
            if (i != 0) {
                Collections.shuffle(list)
            }
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            for (j in 0..<NUM_COLS) {
                listRowAdapter.add(list.get(j % 5)!!)
            }
            val header = HeaderItem(i.toLong(), SampleMovieCatalog.MOVIE_CATEGORY[i])
            mRowsAdapter!!.add(ListRow(header, listRowAdapter))
            i++
        }

        val gridHeader = HeaderItem(i.toLong(), "PREFERENCES")

        val mGridPresenter = GridItemPresenter()
        val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
        gridRowAdapter.add(getResources().getString(R.string.grid_view))
        gridRowAdapter.add(getString(R.string.error_fragment))
        gridRowAdapter.add(getResources().getString(R.string.personal_settings))
        mRowsAdapter!!.add(ListRow(gridHeader, gridRowAdapter))

        setAdapter(mRowsAdapter)
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity())
        mBackgroundManager!!.attach(getActivity().getWindow())
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background)
        mMetrics = DisplayMetrics()
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title)) // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED)
        setHeadersTransitionOnBackEnabled(true)

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background))
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque))
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                    .show()
            }
        })

        setOnItemViewClickedListener(ItemViewClickedListener())
        setOnItemViewSelectedListener(ItemViewSelectedListener())
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?, row: Row?
        ) {
            if (item is Movie) {
                val movie = item
                Log.d(TAG, "Item: " + item.toString())
                val intent = Intent(getActivity(), MovieDetailsActivity::class.java)
                intent.putExtra(MovieDetailsActivity.MOVIE, movie)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    (itemViewHolder.view as ImageCardView).getMainImageView()!!,
                    MovieDetailsActivity.SHARED_ELEMENT_NAME
                ).toBundle()
                getActivity().startActivity(intent, bundle)
            } else if (item is String) {
                if (item.indexOf(getString(R.string.error_fragment)) >= 0) {
                    val intent = Intent(getActivity(), BrowseErrorActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?, row: Row?
        ) {
            if (item is Movie) {
                mBackgroundURI = item.backgroundImageURI
                startBackgroundTimer()
            }
        }
    }

    protected fun updateBackground(uri: String?) {
        val width = mMetrics!!.widthPixels
        val height = mMetrics!!.heightPixels
        Glide.with((getActivity() as androidx.fragment.app.FragmentActivity?)!!)
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into(object : CustomTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    mBackgroundManager!!.setDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    mBackgroundManager!!.setDrawable(null)
                }
            })
        mBackgroundTimer!!.cancel()
    }

    private fun startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer!!.cancel()
        }
        mBackgroundTimer = Timer()
        mBackgroundTimer!!.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {
        override fun run() {
            mHandler.post(object : Runnable {
                override fun run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI.toString())
                    }
                }
            })
        }
    }

    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val view = TextView(parent.getContext())
            view.setLayoutParams(ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT))
            view.setFocusable(true)
            view.setFocusableInTouchMode(true)
            view.setBackgroundColor(getResources().getColor(R.color.default_background))
            view.setTextColor(Color.WHITE)
            view.setGravity(Gravity.CENTER)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
            (viewHolder.view as TextView).setText(item as String?)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        }
    }

    companion object {
        private const val TAG = "MovieBrowseFragment"

        private const val BACKGROUND_UPDATE_DELAY = 300
        private const val GRID_ITEM_WIDTH = 200
        private const val GRID_ITEM_HEIGHT = 200
        private const val NUM_ROWS = 6
        private const val NUM_COLS = 15
    }
}
