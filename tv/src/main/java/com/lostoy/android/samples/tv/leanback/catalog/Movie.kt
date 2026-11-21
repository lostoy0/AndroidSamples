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
package com.lostoy.android.samples.tv.leanback.catalog

import android.util.Log
import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException

/*
 * Movie class represents video entity with title, description, image thumbs and video url.
 *
 */
class Movie : Serializable {
    var id: Long = 0
    var title: String? = null
    var description: String? = null
    private var bgImageUrl: String? = null
    var cardImageUrl: String? = null
    var videoUrl: String? = null
    var studio: String? = null
    var category: String? = null

    var backgroundImageUrl: String
        get() = bgImageUrl!!
        set(bgImageUrl) {
            this.bgImageUrl = bgImageUrl
        }

    val backgroundImageURI: URI?
        get() {
            try {
                Log.d("BACK MOVIE: ", bgImageUrl!!)
                return URI(this.backgroundImageUrl)
            } catch (e: URISyntaxException) {
                Log.d("URI exception: ", bgImageUrl!!)
                return null
            }
        }

    val cardImageURI: URI?
        get() {
            try {
                return URI(this.cardImageUrl)
            } catch (e: URISyntaxException) {
                return null
            }
        }

    override fun toString(): String {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", backgroundImageUrl='" + bgImageUrl + '\'' +
                ", backgroundImageURI='" + this.backgroundImageURI.toString() + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                '}'
    }

    companion object {
        const val serialVersionUID: Long = 727566175075960653L
        var count: Long = 0
            private set

        fun incCount() {
            count++
        }
    }
}
