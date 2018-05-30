package com.lostoy.android.samples.raymond;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lostoy.android.samples.R;
import com.lostoy.android.samples.raymond.widget.RecyclerViewForTV;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifanlong on 2017/12/27.
 */

public class TVDemoFragment extends BaseFragment {

    private Button mPlayButton, mNextButton, mFavouriteButton;

    private TextView mTitleTextView, mDescTextView;

    private RecyclerViewForTV mRecyclerView;
    private MovieListAdapter mAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_tv_demo;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleTextView = findViewById(R.id.titleTextView);
        mDescTextView = findViewById(R.id.descriptionTextView);
        mDescTextView.setEllipsize(TextUtils.TruncateAt.END);

        mPlayButton = findViewById(R.id.playButton);
        mNextButton = findViewById(R.id.nextButton);
        mFavouriteButton = findViewById(R.id.favouriteButton);

        mPlayButton.requestFocus();

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
        public void onBindViewHolder(MovieViewHolder holder, int position) {
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
