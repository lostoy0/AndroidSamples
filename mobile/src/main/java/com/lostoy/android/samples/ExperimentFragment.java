package com.lostoy.android.samples;

import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by lifanlong on 2017/11/30.
 */

public class ExperimentFragment extends BaseFragment {

    public static ArrayList<EditText> editTextList = new ArrayList<>();
    private ArrayList<View> itemViews = new ArrayList<>();
    private LinearLayout mRoot;
    private LayoutInflater mInflater;
    private ScrollView mScrollView;

    public static ExperimentFragment newInstance() {
        ExperimentFragment fragment = new ExperimentFragment();

        return fragment;
    }

    public ExperimentFragment() {
        super();

        Log.e("raymond", ExperimentFragment.class.getSimpleName());
        Log.e("raymond", "edittextlist size " + editTextList.size());

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_experiment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = LayoutInflater.from(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("raymond", "onViewCreated----------");

        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);

        mRoot = (LinearLayout) view.findViewById(R.id.editTextRoot);

        view.findViewById(R.id.addEditText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditText(v);
            }
        });

        view.findViewById(R.id.removeEditText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditText(v);
            }
        });

        view.findViewById(R.id.requestFocus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFocus(v);
            }
        });
    }

    public void addEditText(View v) {
        final View item = mInflater.inflate(R.layout.item_with_edittext, null);
        item.findViewById(R.id.requestItemFocus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.findViewById(R.id.editText).requestFocus();
            }
        });
        item.findViewById(R.id.removeItemView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoot.removeView(item);
//                itemViews.remove(item);
                if(item.findViewById(R.id.editText).isFocused()) {
                    Log.e("raymond", "is focused");
                } else {
                    Log.e("raymond", "do not have focused");
                }
            }
        });
        EditText editText = (EditText) item.findViewById(R.id.editText);
        editTextList.add(editText);
        itemViews.add(item);
        mRoot.addView(item, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void removeEditText(View v) {
        itemViews.clear();
        mRoot.removeAllViews();
        editTextList.clear();
//        View item = itemViews.remove(0);
//        mRoot.removeView(item);
//        if(item.findViewById(R.id.editText).isFocused()) {
//            Log.e("raymond", "is focused");
//        } else {
//            Log.e("raymond", "do not have focused");
//        }
    }

    public void requestFocus(View v) {
        try {
//            editTextList.get(0).requestFocus();
//            mScrollView.requestChildFocus(itemViews.get(0), editTextList.get(0));
            mScrollView.offsetDescendantRectToMyCoords(editTextList.get(0), new Rect());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
