package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.storyboardapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class BoardActivity : Activity() {
    private lateinit var mListViewComments : ListView
    private lateinit var mCommentsAdapter: CommentsAdapter
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private  lateinit var postRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var imageViewTest: ImageView
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
        imageViewTest = findViewById(R.id.imageViewTest)
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

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance();
        storageReference = storage.reference;

        postRef = database.getReference("posts")
        val postID = "na258b64b-64ca-4b49-babd-f44d9eecced0"
        val uid = "nRyfUuCwFeg6oo7bDaCpucehPXJ3"
        val imagesRef = storageReference.child("images/${postID}")
        val uploadTask = imagesRef.getBytes(10)

    }
}