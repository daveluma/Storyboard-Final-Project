package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.storyboardapp.R

class CommentsAdapter(private val mContext: Context) : BaseAdapter() {
    private var mItems = ArrayList<Comment>()

    fun add(item: Comment) {

        mItems.add(item)
        notifyDataSetChanged()

    }
    override fun isEnabled(position: Int): Boolean {
        return false
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mItems.size
    }

    override fun getItem(pos: Int): Any {
        return mItems[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        val currItem = getItem(position) as Comment

        val viewHolder: ViewHolder
        if (null == convertView) {

            val newView = LayoutInflater.from(mContext).inflate(R.layout.comment_row, parent, false)
            viewHolder = ViewHolder()

            viewHolder.mUsernameView = newView.findViewById<TextView>(R.id.textViewUsername)
            viewHolder.mCommentView = newView.findViewById<TextView>(R.id.textViewComment)
            viewHolder.position = position
            viewHolder.mItemLayout = newView as RelativeLayout
            newView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.mUsernameView?.text = currItem.username
        viewHolder.mCommentView?.text = currItem.comment

        return viewHolder.mItemLayout

    }

    internal class ViewHolder {
        var position: Int = 0
        var mUsernameView: TextView? = null
        var mCommentView: TextView? = null
        var mItemLayout: RelativeLayout? = null
    }
}