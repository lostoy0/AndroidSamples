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
package com.lostoy.android.samples.tv.leanback.presenter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.lostoy.android.samples.R
import com.lostoy.android.samples.tv.leanback.catalog.Movie

/*
 * A MovieCardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
class MovieCardPresenter : Presenter() {
    private var mDefaultCardImage: Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background)
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background)
        mDefaultCardImage = parent.getResources().getDrawable(R.drawable.movie)

        val cardView: ImageCardView = object : ImageCardView(parent.getContext()) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.setFocusable(true)
        cardView.setFocusableInTouchMode(true)
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val movie = item as Movie
        val cardView = viewHolder.view as ImageCardView

        Log.d(TAG, "onBindViewHolder")
        if (movie.cardImageUrl != null) {
            cardView.setTitleText(movie.title)
            cardView.setContentText(movie.studio)
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
            Glide.with(viewHolder.view.getContext())
                .load(movie.cardImageUrl)
                .centerCrop()
                .error(mDefaultCardImage)
                .into(cardView.getMainImageView()!!)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null)
        cardView.setMainImage(null)
    }

    companion object {
        private const val TAG = "MovieCardPresenter"

        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 176
        private var sSelectedBackgroundColor = 0
        private var sDefaultBackgroundColor = 0
        private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
            val color: Int = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
            // Both background colors should be set because the view's background is temporarily visible
            // during animations.
            view.setBackgroundColor(color)
            view.setInfoAreaBackgroundColor(color)
        }
    }
}
