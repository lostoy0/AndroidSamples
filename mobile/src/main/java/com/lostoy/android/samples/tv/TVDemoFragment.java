package com.lostoy.android.samples.tv;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lostoy.android.samples.BaseFragment;
import com.lostoy.android.samples.R;
import com.lostoy.android.samples.widget.RecyclerViewForTV;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifanlong on 2017/12/27.
 */

public class TVDemoFragment extends BaseFragment {

    private Button mPlayButton, mNextButton, mFavouriteButton;

    private RecyclerViewForTV mRecyclerView;
    private MovieListAdapter mAdapter;

    @Override
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
//        mRecyclerView.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
//            @Override
//            public int onGetChildDrawingOrder(int childCount, int i) {
//                int focusPos = mRecyclerView.indexOfChild(mRecyclerView.getFocusedChild());
//                if(focusPos >= 0 && focusPos < childCount) {
//                    if(i == focusPos) {
//                        return childCount-1;
//                    } else if(i == childCount-1) {
//                        return focusPos;
//                    }
//                }
//                return i;
//            }
//        });
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
