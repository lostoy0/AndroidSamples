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
package com.lostoy.android.samples.tv.leanback.playback

import android.app.Activity
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.ControlButtonPresenterSelector
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.PlaybackControlsRow
import androidx.leanback.widget.PlaybackControlsRow.ClosedCaptioningAction
import androidx.leanback.widget.PlaybackControlsRow.FastForwardAction
import androidx.leanback.widget.PlaybackControlsRow.HighQualityAction
import androidx.leanback.widget.PlaybackControlsRow.PlayPauseAction
import androidx.leanback.widget.PlaybackControlsRow.RepeatAction
import androidx.leanback.widget.PlaybackControlsRow.RewindAction
import androidx.leanback.widget.PlaybackControlsRow.ShuffleAction
import androidx.leanback.widget.PlaybackControlsRow.SkipNextAction
import androidx.leanback.widget.PlaybackControlsRow.SkipPreviousAction
import androidx.leanback.widget.PlaybackControlsRow.ThumbsDownAction
import androidx.leanback.widget.PlaybackControlsRow.ThumbsUpAction
import androidx.leanback.widget.PlaybackControlsRowPresenter
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
import com.lostoy.android.samples.tv.leanback.presenter.MovieCardPresenter
import kotlin.math.max

/*
 * Class for video playback with media control
 */
class MoviePlaybackControlsFragment : PlaybackSupportFragment() {
    private var mRowsAdapter: ArrayObjectAdapter? = null
    private var mPrimaryActionsAdapter: ArrayObjectAdapter? = null
    private var mSecondaryActionsAdapter: ArrayObjectAdapter? = null
    private var mPlayPauseAction: PlayPauseAction? = null
    private var mRepeatAction: RepeatAction? = null
    private var mThumbsUpAction: ThumbsUpAction? = null
    private var mThumbsDownAction: ThumbsDownAction? = null
    private var mShuffleAction: ShuffleAction? = null
    private var mFastForwardAction: FastForwardAction? = null
    private var mRewindAction: RewindAction? = null
    private var mSkipNextAction: SkipNextAction? = null
    private var mSkipPreviousAction: SkipPreviousAction? = null
    private var mPlaybackControlsRow: PlaybackControlsRow? = null
    private var mItems = ArrayList<Movie>()
    private var mCurrentItem = 0
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var mSelectedMovie: Movie? = null

    private var mCallback: OnPlayPauseClickedListener? = null

    // Container Activity must implement this interface
    interface OnPlayPauseClickedListener {
        fun onFragmentPlayPause(movie: Movie, position: Int, playPause: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mItems = ArrayList<Movie>()
        mSelectedMovie = getActivity()!!
            .getIntent().getSerializableExtra(MovieDetailsActivity.MOVIE) as Movie?

        val movies = SampleMovieCatalog.list

        for (j in movies.indices) {
            mItems.add(movies[j])
            if (mSelectedMovie?.title == movies[j].title) {
                mCurrentItem = j
            }
        }

        mHandler = Handler()

        setBackgroundType(BACKGROUND_TYPE)
        setFadingEnabled(false)

        setupRows()

        setOnItemViewSelectedListener(object : OnItemViewSelectedListener {
            override fun onItemSelected(
                itemViewHolder: Presenter.ViewHolder?, item: Any?,
                rowViewHolder: RowPresenter.ViewHolder?, row: Row?
            ) {
                Log.i(TAG, "onItemSelected: " + item + " row " + row)
            }
        })
        setOnItemViewClickedListener(object : OnItemViewClickedListener {
            override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder?, item: Any?,
                rowViewHolder: RowPresenter.ViewHolder?, row: Row?
            ) {
                Log.i(TAG, "onItemClicked: " + item + " row " + row)
            }
        })
    }

    @Suppress("deprecation")
    override fun onAttach(context: Activity) {
        super.onAttach(context)
        if (context is OnPlayPauseClickedListener) {
            mCallback = context as OnPlayPauseClickedListener
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnPlayPauseClickedListener"
            )
        }
    }

    private fun setupRows() {
        val ps = ClassPresenterSelector()

        val playbackControlsRowPresenter: PlaybackControlsRowPresenter?
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = PlaybackControlsRowPresenter(
                DescriptionPresenter()
            )
        } else {
            playbackControlsRowPresenter = PlaybackControlsRowPresenter()
        }
        playbackControlsRowPresenter.setOnActionClickedListener(object : OnActionClickedListener {
            override fun onActionClicked(action: Action) {
                if (action.getId() == mPlayPauseAction!!.getId()) {
                    togglePlayback(mPlayPauseAction!!.getIndex() == PlayPauseAction.PLAY)
                } else if (action.getId() == mSkipNextAction!!.getId()) {
                    next()
                } else if (action.getId() == mSkipPreviousAction!!.getId()) {
                    prev()
                } else if (action.getId() == mFastForwardAction!!.getId()) {
                    Toast.makeText(getActivity(), "TODO: Fast Forward", Toast.LENGTH_SHORT).show()
                } else if (action.getId() == mRewindAction!!.getId()) {
                    Toast.makeText(getActivity(), "TODO: Rewind", Toast.LENGTH_SHORT).show()
                }
                if (action is PlaybackControlsRow.MultiAction) {
                    action.nextIndex()
                    notifyChanged(action)
                }
            }
        })
        playbackControlsRowPresenter.setSecondaryActionsHidden(HIDE_MORE_ACTIONS)

        ps.addClassPresenter(PlaybackControlsRow::class.java, playbackControlsRowPresenter)
        ps.addClassPresenter(ListRow::class.java, ListRowPresenter())
        mRowsAdapter = ArrayObjectAdapter(ps)

        addPlaybackControlsRow()
        addOtherRows()

        setAdapter(mRowsAdapter)
    }

    fun togglePlayback(playPause: Boolean) {
        if (playPause) {
            startProgressAutomation()
            setFadingEnabled(true)
            mCallback!!.onFragmentPlayPause(
                mItems.get(mCurrentItem),
                mPlaybackControlsRow!!.getCurrentTime(), true
            )
            mPlayPauseAction!!.setIcon(mPlayPauseAction!!.getDrawable(PlayPauseAction.PAUSE))
        } else {
            stopProgressAutomation()
            setFadingEnabled(false)
            mCallback!!.onFragmentPlayPause(
                mItems.get(mCurrentItem),
                mPlaybackControlsRow!!.getCurrentTime(), false
            )
            mPlayPauseAction!!.setIcon(mPlayPauseAction!!.getDrawable(PlayPauseAction.PLAY))
        }
        notifyChanged(mPlayPauseAction!!)
    }

    private val duration: Int
        get() {
            val movie = mItems.get(mCurrentItem)
            val mmr = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mmr.setDataSource(
                    movie.videoUrl,
                    HashMap<String?, String?>()
                )
            } else {
                mmr.setDataSource(movie.videoUrl)
            }
            val time =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val duration = time!!.toLong()
            return duration.toInt()
        }

    private fun addPlaybackControlsRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = PlaybackControlsRow(mSelectedMovie)
        } else {
            mPlaybackControlsRow = PlaybackControlsRow()
        }
        mRowsAdapter!!.add(mPlaybackControlsRow!!)

        updatePlaybackRow(mCurrentItem)

        val presenterSelector = ControlButtonPresenterSelector()
        mPrimaryActionsAdapter = ArrayObjectAdapter(presenterSelector)
        mSecondaryActionsAdapter = ArrayObjectAdapter(presenterSelector)
        mPlaybackControlsRow!!.setPrimaryActionsAdapter(mPrimaryActionsAdapter)
        mPlaybackControlsRow!!.setSecondaryActionsAdapter(mSecondaryActionsAdapter)

        mPlayPauseAction = PlayPauseAction(getActivity())
        mRepeatAction = RepeatAction(getActivity())
        mThumbsUpAction = ThumbsUpAction(getActivity())
        mThumbsDownAction = ThumbsDownAction(getActivity())
        mShuffleAction = ShuffleAction(getActivity())
        mSkipNextAction = SkipNextAction(getActivity())
        mSkipPreviousAction = SkipPreviousAction(getActivity())
        mFastForwardAction = FastForwardAction(getActivity())
        mRewindAction = RewindAction(getActivity())

        if (PRIMARY_CONTROLS > 5) {
            mPrimaryActionsAdapter!!.add(mThumbsUpAction!!)
        } else {
            mSecondaryActionsAdapter!!.add(mThumbsUpAction!!)
        }
        mPrimaryActionsAdapter!!.add(mSkipPreviousAction!!)
        if (PRIMARY_CONTROLS > 3) {
            mPrimaryActionsAdapter!!.add(RewindAction(getActivity()))
        }
        mPrimaryActionsAdapter!!.add(mPlayPauseAction!!)
        if (PRIMARY_CONTROLS > 3) {
            mPrimaryActionsAdapter!!.add(FastForwardAction(getActivity()))
        }
        mPrimaryActionsAdapter!!.add(mSkipNextAction!!)

        mSecondaryActionsAdapter!!.add(mRepeatAction!!)
        mSecondaryActionsAdapter!!.add(mShuffleAction!!)
        if (PRIMARY_CONTROLS > 5) {
            mPrimaryActionsAdapter!!.add(mThumbsDownAction!!)
        } else {
            mSecondaryActionsAdapter!!.add(mThumbsDownAction!!)
        }
        mSecondaryActionsAdapter!!.add(HighQualityAction(getActivity()))
        mSecondaryActionsAdapter!!.add(ClosedCaptioningAction(getActivity()))
    }

    private fun notifyChanged(action: Action) {
        var adapter = mPrimaryActionsAdapter!!
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1)
            return
        }
        adapter = mSecondaryActionsAdapter!!
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1)
            return
        }
    }

    private fun updatePlaybackRow(index: Int) {
        if (mPlaybackControlsRow!!.getItem() != null) {
            val item = mPlaybackControlsRow!!.getItem() as Movie
            item.title = mItems[mCurrentItem].title
            item.studio = mItems[mCurrentItem].studio
        }
        if (SHOW_IMAGE) {
            updateVideoImage(mItems[mCurrentItem].cardImageURI.toString())
        }
        mRowsAdapter!!.notifyArrayItemRangeChanged(0, 1)
        mPlaybackControlsRow!!.setTotalTime(this.duration)
        mPlaybackControlsRow!!.setCurrentTime(0)
        mPlaybackControlsRow!!.setBufferedProgress(0)
    }

    private fun addOtherRows() {
        val listRowAdapter = ArrayObjectAdapter(MovieCardPresenter())
        for (movie in mItems) {
            listRowAdapter.add(movie)
        }
        val header = HeaderItem(0, getString(R.string.related_movies))
        mRowsAdapter!!.add(ListRow(header, listRowAdapter))
    }

    private val updatePeriod: Int
        get() {
            if (getView() == null || mPlaybackControlsRow!!.getTotalTime() <= 0) {
                return DEFAULT_UPDATE_PERIOD
            }
            return max(
                UPDATE_PERIOD,
                mPlaybackControlsRow!!.getTotalTime() / getView()!!.getWidth()
            )
        }

    private fun startProgressAutomation() {
        mRunnable = object : Runnable {
            override fun run() {
                val updatePeriod: Int = this@MoviePlaybackControlsFragment.updatePeriod
                val currentTime = mPlaybackControlsRow!!.getCurrentTime() + updatePeriod
                val totalTime = mPlaybackControlsRow!!.getTotalTime()
                mPlaybackControlsRow!!.setCurrentTime(currentTime)
                mPlaybackControlsRow!!.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME)

                if (totalTime > 0 && totalTime <= currentTime) {
                    next()
                }
                mHandler!!.postDelayed(this, updatePeriod.toLong())
            }
        }
        mHandler!!.postDelayed(mRunnable!!, this.updatePeriod.toLong())
    }

    private fun next() {
        if (++mCurrentItem >= mItems.size) {
            mCurrentItem = 0
        }

        if (mPlayPauseAction!!.getIndex() == PlayPauseAction.PLAY) {
            mCallback!!.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false)
        } else {
            mCallback!!.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true)
        }
        updatePlaybackRow(mCurrentItem)
    }

    private fun prev() {
        if (--mCurrentItem < 0) {
            mCurrentItem = mItems.size - 1
        }
        if (mPlayPauseAction!!.getIndex() == PlayPauseAction.PLAY) {
            mCallback!!.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false)
        } else {
            mCallback!!.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true)
        }
        updatePlaybackRow(mCurrentItem)
    }

    private fun stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler!!.removeCallbacks(mRunnable!!)
        }
    }

    override fun onStop() {
        stopProgressAutomation()
        super.onStop()
    }

    internal class DescriptionPresenter : AbstractDetailsDescriptionPresenter() {
        override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
            viewHolder.getTitle().setText((item as Movie).title)
            viewHolder.getSubtitle().setText(item.studio)
        }
    }

    protected fun updateVideoImage(uri: String?) {
        Glide.with(getActivity()!!)
            .load(uri)
            .centerCrop()
            .into(object : CustomTarget<Drawable>(CARD_WIDTH, CARD_HEIGHT) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    mPlaybackControlsRow!!.setImageDrawable(resource)
                    mRowsAdapter!!.notifyArrayItemRangeChanged(0, mRowsAdapter!!.size())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    mPlaybackControlsRow!!.setImageDrawable(null)
                }
            })
    }

    companion object {
        private const val TAG = "PlaybackControlsFragmnt"

        private const val SHOW_DETAIL = true
        private const val HIDE_MORE_ACTIONS = false
        private const val PRIMARY_CONTROLS = 5
        private val SHOW_IMAGE = PRIMARY_CONTROLS <= 5
        private val BACKGROUND_TYPE = BG_LIGHT
        private const val CARD_WIDTH = 200
        private const val CARD_HEIGHT = 240
        private const val DEFAULT_UPDATE_PERIOD = 1000
        private const val UPDATE_PERIOD = 16
        private const val SIMULATED_BUFFERED_TIME = 10000
    }
}
