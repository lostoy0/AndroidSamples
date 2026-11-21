package com.lostoy.android.samples.widgets.input

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.lostoy.android.samples.R
import com.lostoy.android.samples.widgets.input.InputSampleData.SubItem

/**
 * Created by lifanlong on 2017/11/30.
 */
class InputFocusSampleFragment : Fragment(R.layout.fragment_experiment) {
    private val editTextList = ArrayList<EditText?>()
    private val itemViews = ArrayList<View?>()
    private var mRoot: LinearLayout? = null
    private var mInflater: LayoutInflater? = null
    private var mScrollView: ScrollView? = null
    private var mContentTextView: TextView? = null

    private var itemData: InputSampleData? = null

    init {
        Log.e("raymond", InputFocusSampleFragment::class.java.getSimpleName())
        Log.e("raymond", "edittextlist size " + editTextList.size)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getArguments()
        if (args != null) {
            itemData = args.getSerializable(KEY_PARAMS) as InputSampleData?
            Log.d("raymond", "item data: " + GsonBuilder().create().toJson(itemData))
        }

        mInflater = LayoutInflater.from(getContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("raymond", "onViewCreated----------")

        mScrollView = view.findViewById<View?>(R.id.scrollView) as ScrollView

        mRoot = view.findViewById<View?>(R.id.editTextRoot) as LinearLayout

        mContentTextView = view.findViewById<TextView>(R.id.content_text)

        view.findViewById<View?>(R.id.addEditText)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    addEditText(v)
                }
            })

        view.findViewById<View?>(R.id.removeEditText)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    removeEditText(v)
                }
            })

        view.findViewById<View?>(R.id.requestFocus)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    requestFocus(v)
                }
            })

        mContentTextView!!.setText(GsonBuilder().create().toJson(itemData))
    }

    fun addEditText(v: View?) {
        val item = mInflater!!.inflate(R.layout.item_with_edittext, null)
        item.findViewById<View?>(R.id.requestItemFocus)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    item.findViewById<View?>(R.id.editText).requestFocus()
                }
            })
        item.findViewById<View?>(R.id.removeItemView)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    mRoot!!.removeView(item)
                    //                itemViews.remove(item);
                    if (item.findViewById<View?>(R.id.editText).isFocused()) {
                        Log.e("raymond", "is focused")
                    } else {
                        Log.e("raymond", "do not have focused")
                    }
                }
            })
        val editText = item.findViewById<View?>(R.id.editText) as EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("raymond", "beforeTextChanged: " + s)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("raymond", "onTextChanged: " + s)
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("raymond", "afterTextChanged: " + s)
            }
        })
        editTextList.add(editText)
        itemViews.add(item)
        mRoot!!.addView(
            item,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    fun removeEditText(v: View?) {
        itemViews.clear()
        mRoot!!.removeAllViews()
        editTextList.clear()
        //        View item = itemViews.remove(0);
//        mRoot.removeView(item);
//        if(item.findViewById(R.id.editText).isFocused()) {
//            Log.e("raymond", "is focused");
//        } else {
//            Log.e("raymond", "do not have focused");
//        }
    }

    fun requestFocus(v: View?) {
        try {
//            editTextList.get(0).requestFocus();
//            mScrollView.requestChildFocus(itemViews.get(0), editTextList.get(0));
            mScrollView!!.offsetDescendantRectToMyCoords(editTextList.get(0), Rect())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val KEY_PARAMS = "params"

        fun newInstance(): InputFocusSampleFragment {
            val fragment = InputFocusSampleFragment()
            val args = Bundle()
            args.putSerializable(KEY_PARAMS, createItemData())
            fragment.setArguments(args)
            return fragment
        }

        private fun createItemData(): InputSampleData {
            val itemData = InputSampleData()
            itemData.name = "item name"
            itemData.description = "item description"
            itemData.type = 1
            val subItems: MutableList<SubItem?> = ArrayList<SubItem?>()
            subItems.add(SubItem("sub item name", "sub item description"))
            subItems.add(SubItem("sub item name", "sub item description"))
            subItems.add(SubItem("sub item name", "sub item description"))
            itemData.subItems = subItems
            return itemData
        }
    }
}
