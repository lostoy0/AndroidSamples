package com.lostoy.android.samples.tv.leanback.catalog


object SampleMovieCatalog {
    val MOVIE_CATEGORY: Array<String> = arrayOf(
        "Category Zero",
        "Category One",
        "Category Two",
        "Category Three",
        "Category Four",
        "Category Five",
    )

    private var movies: MutableList<Movie>? = null
    val list: MutableList<Movie>
        get() = movies ?: setupMovies()

    fun setupMovies(): MutableList<Movie> {
        val list = ArrayList<Movie>()
        val title: Array<String> = arrayOf(
            "Zeitgeist 2010_ Year in Review",
            "Google Demo Slam_ 20ft Search",
            "Introducing Gmail Blue",
            "Introducing Google Fiber to the Pole",
            "Introducing Google Nose"
        )

        val description = ("Fusce id nisi turpis. Praesent viverra bibendum semper. "
                + "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est "
                + "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit "
                + "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit "
                + "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id "
                + "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.")

        val videoUrl: Array<String> = arrayOf(
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue.mp4",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole.mp4",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4"
        )
        val bgImageUrl: Array<String> = arrayOf(
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/bg.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/bg.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/bg.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/bg.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/bg.jpg",
        )
        val cardImageUrl: Array<String> = arrayOf(
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/card.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/card.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/card.jpg",
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg"
        )

        list.add(
            buildMovieInfo(
                "category", title[0],
                description, "Studio Zero", videoUrl[0], cardImageUrl[0], bgImageUrl[0]
            )
        )
        list.add(
            buildMovieInfo(
                "category", title[1],
                description, "Studio One", videoUrl[1], cardImageUrl[1], bgImageUrl[1]
            )
        )
        list.add(
            buildMovieInfo(
                "category", title[2],
                description, "Studio Two", videoUrl[2], cardImageUrl[2], bgImageUrl[2]
            )
        )
        list.add(
            buildMovieInfo(
                "category", title[3],
                description, "Studio Three", videoUrl[3], cardImageUrl[3], bgImageUrl[3]
            )
        )
        list.add(
            buildMovieInfo(
                "category", title[4],
                description, "Studio Four", videoUrl[4], cardImageUrl[4], bgImageUrl[4]
            )
        )

        movies = list
        return list
    }

    private fun buildMovieInfo(
        category: String?, title: String?,
        description: String?, studio: String?, videoUrl: String?, cardImageUrl: String?,
        bgImageUrl: String
    ): Movie {
        val movie = Movie()
        movie.id = Movie.count
        Movie.incCount()
        movie.title = title
        movie.description = description
        movie.studio = studio
        movie.category = category
        movie.cardImageUrl = cardImageUrl
        movie.backgroundImageUrl = bgImageUrl
        movie.videoUrl = videoUrl
        return movie
    }
}
