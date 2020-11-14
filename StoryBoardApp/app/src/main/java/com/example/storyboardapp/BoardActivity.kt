package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.storyboardapp.R


class BoardActivity : Activity() {
    private lateinit var mListViewComments : ListView
    private lateinit var mCommentsAdapter: CommentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        mListViewComments = findViewById(R.id.listViewComments)
        mCommentsAdapter = CommentsAdapter(applicationContext)
        mListViewComments.adapter = mCommentsAdapter
        mCommentsAdapter.add(Comment("asdasd", "qweqweqwe"))
        mCommentsAdapter.add(Comment("asdqweasd", "qwe"))
        mCommentsAdapter.add(Comment("asdqweasd", "qwe"))
        mCommentsAdapter.add(Comment("asdqweasd", "qwe"))
        mCommentsAdapter.notifyDataSetChanged()
        var totalHeight = 0
        for (i in 0 until mCommentsAdapter.getCount()) {
            val listItem: View? = mCommentsAdapter.getView(i, null, mListViewComments)
            listItem!!.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = mListViewComments.layoutParams
        params.height = totalHeight + mListViewComments.dividerHeight * (mCommentsAdapter.count - 1)
        mListViewComments.layoutParams = params
        mListViewComments.requestLayout()
        Log.d("ASD", mCommentsAdapter.count.toString())
    }
}