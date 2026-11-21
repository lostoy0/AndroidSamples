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
package com.lostoy.android.samples.tv.leanback.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.DetailsFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.DetailsOverviewRowPresenter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lostoy.android.samples.R
import com.lostoy.android.samples.tv.leanback.browse.MovieBrowseActivity
import com.lostoy.android.samples.tv.leanback.catalog.Movie
import com.lostoy.android.samples.tv.leanback.catalog.SampleMovieCatalog
import com.lostoy.android.samples.tv.leanback.playback.MoviePlaybackActivity
import com.lostoy.android.samples.tv.leanback.presenter.MovieCardPresenter
import com.lostoy.android.samples.tv.leanback.presenter.MovieDescriptionPresenter
import com.lostoy.android.samples.tv.leanback.util.LeanbackUiUtils
import java.util.Collections

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
class MovieDetailsFragment : DetailsFragment() {
    private var mSelectedMovie: Movie? = null

    private var mAdapter: ArrayObjectAdapter? = null
    private var mPresenterSelector: ClassPresenterSelector? = null

    private var mBackgroundManager: BackgroundManager? = null
    private var mDefaultBackground: Drawable? = null
    private var mMetrics: DisplayMetrics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate DetailsFragment")
        super.onCreate(savedInstanceState)

        prepareBackgroundManager()

        mSelectedMovie = getActivity().getIntent()
            .getSerializableExtra(MovieDetailsActivity.MOVIE) as Movie?
        if (mSelectedMovie != null) {
            setupAdapter()
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            setupMovieListRow()
            setupMovieListRowPresenter()
            updateBackground(mSelectedMovie!!.backgroundImageUrl)
            setOnItemViewClickedListener(ItemViewClickedListener())
        } else {
            val intent = Intent(getActivity(), MovieBrowseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity())
        mBackgroundManager!!.attach(getActivity().getWindow())
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background)
        mMetrics = DisplayMetrics()
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics)
    }

    protected fun updateBackground(uri: String?) {
        Glide.with((getActivity() as androidx.fragment.app.FragmentActivity?)!!)
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into(object :
                CustomTarget<Drawable>(mMetrics!!.widthPixels, mMetrics!!.heightPixels) {
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
    }

    private fun setupAdapter() {
        mPresenterSelector = ClassPresenterSelector()
        mAdapter = ArrayObjectAdapter(mPresenterSelector!!)
        setAdapter(mAdapter)
    }

    private fun setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString())
        val row = DetailsOverviewRow(mSelectedMovie!!)
        row.setImageDrawable(getResources().getDrawable(R.drawable.default_background))
        val width = LeanbackUiUtils.convertDpToPixel(
            getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH
        )
        val height = LeanbackUiUtils.convertDpToPixel(
            getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT
        )
        Glide.with((getActivity() as androidx.fragment.app.FragmentActivity?)!!)
            .load(mSelectedMovie!!.cardImageUrl)
            .centerCrop()
            .error(R.drawable.default_background)
            .into(object : CustomTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    Log.d(TAG, "details overview card image url ready: " + resource)
                    row.setImageDrawable(resource)
                    mAdapter!!.notifyArrayItemRangeChanged(0, mAdapter!!.size())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    row.setImageDrawable(null)
                }
            })

        row.addAction(
            Action(
                ACTION_WATCH_TRAILER.toLong(), getResources().getString(
                    R.string.watch_trailer_1
                ), getResources().getString(R.string.watch_trailer_2)
            )
        )
        row.addAction(
            Action(
                ACTION_RENT.toLong(), getResources().getString(R.string.rent_1),
                getResources().getString(R.string.rent_2)
            )
        )
        row.addAction(
            Action(
                ACTION_BUY.toLong(), getResources().getString(R.string.buy_1),
                getResources().getString(R.string.buy_2)
            )
        )

        mAdapter!!.add(row)
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background and style.
        val detailsPresenter =
            DetailsOverviewRowPresenter(MovieDescriptionPresenter())
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background))
        detailsPresenter.setStyleLarge(true)

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(
            getActivity(),
            MovieDetailsActivity.SHARED_ELEMENT_NAME
        )

        detailsPresenter.setOnActionClickedListener(object : OnActionClickedListener {
            override fun onActionClicked(action: Action) {
                if (action.getId() == ACTION_WATCH_TRAILER.toLong()) {
                    val intent = Intent(getActivity(), MoviePlaybackActivity::class.java)
                    intent.putExtra(MovieDetailsActivity.MOVIE, mSelectedMovie)
                    startActivity(intent)
                } else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
        mPresenterSelector!!.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun setupMovieListRow() {
        val subcategories: Array<String?>? = arrayOf<String?>(getString(R.string.related_movies))
        val list = SampleMovieCatalog.list

        Collections.shuffle(list)
        val listRowAdapter = ArrayObjectAdapter(MovieCardPresenter())
        for (j in 0..<NUM_COLS) {
            listRowAdapter.add(list.get(j % 5)!!)
        }

        val header = HeaderItem(0, subcategories!![0])
        mAdapter!!.add(ListRow(header, listRowAdapter))
    }

    private fun setupMovieListRowPresenter() {
        mPresenterSelector!!.addClassPresenter(ListRow::class.java, ListRowPresenter())
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
                intent.putExtra(getResources().getString(R.string.movie), mSelectedMovie)
                intent.putExtra(getResources().getString(R.string.should_start), true)
                startActivity(intent)


                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    (itemViewHolder.view as ImageCardView).getMainImageView()!!,
                    MovieDetailsActivity.SHARED_ELEMENT_NAME
                ).toBundle()
                getActivity().startActivity(intent, bundle)
            }
        }
    }

    companion object {
        private const val TAG = "MovieDetailsFragment"

        private const val ACTION_WATCH_TRAILER = 1
        private const val ACTION_RENT = 2
        private const val ACTION_BUY = 3

        private const val DETAIL_THUMB_WIDTH = 274
        private const val DETAIL_THUMB_HEIGHT = 274

        private const val NUM_COLS = 10
    }
}
