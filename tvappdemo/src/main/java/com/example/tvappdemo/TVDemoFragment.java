package com.example.tvappdemo;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifanlong on 2017/12/27.
 */

public class TVDemoFragment extends Fragment {

    private Button mPlayButton, mNextButton, mFavouriteButton;

    private RecyclerViewForTV mRecyclerView;
    private MovieListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutRes();
        if(layoutId > 0) {
            return inflater.inflate(layoutId, container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return (T) getView().findViewById(id);
    }

    protected int getLayoutRes() {
        return R.layout.fragment_tv_demo;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPlayButton = findViewById(R.id.playButton);
        mNextButton = findViewById(R.id.nextButton);
        mFavouriteButton = findViewById(R.id.favouriteButton);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new MovieListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {
        private Context mContext;

        private List<Movie> mMovieList = new ArrayList<>();

        public MovieListAdapter(Context context) {
            mContext = context;
            for(int i=0; i<20; i++) {
                mMovieList.add(new Movie());
            }
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MovieViewHolder(LayoutInflater.from(mContext).inflate(R.layout.tv_item_movie, parent, false));
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, final int position) {
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {
                        v.animate().scaleX(1.2f).scaleY(1.2f).start();
                        mRecyclerView.invalidate();
                    } else {
                        v.animate().scaleX(1.0f).scaleY(1.0f).start();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mMovieList.size();
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        public MovieViewHolder(View itemView) {
            super(itemView);
        }
    }

    class Movie {

    }
}
