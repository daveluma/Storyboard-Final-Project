package com.example.myapplication

import android.R.attr.radius
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.storyboardapp.Post
import com.example.storyboardapp.R
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.synnapps.carouselview.CarouselView
import java.time.LocalDateTime


class BoardActivity : Activity() {
    private lateinit var mListViewComments: ListView
    private lateinit var mCommentsAdapter: CommentsAdapter
    private lateinit var mLikeButton: ImageButton
    private lateinit var mCommentEditText: EditText
    private lateinit var mLikesTextView: TextView
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private var numLikes = "1,230"

    var carouselView: CarouselView? = null

    private var isLiked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val postId = "-MN_57rcUwEs4yal7Ve7"

        storage = FirebaseStorage.getInstance()

        mLikeButton = findViewById(R.id.buttonLike)
        mListViewComments = findViewById(R.id.listViewComments)
        mCommentEditText = findViewById(R.id.editTextComment)
        mLikesTextView = findViewById(R.id.textViewNumLikes)

        var sampleImages = arrayOf(
            "https://raw.githubusercontent.com/sayyam/carouselview/master/sample/src/main/res/drawable/image_3.jpg",
            "https://raw.githubusercontent.com/sayyam/carouselview/master/sample/src/main/res/drawable/image_1.jpg",
            "https://raw.githubusercontent.com/sayyam/carouselview/master/sample/src/main/res/drawable/image_2.jpg"
        )

        carouselView = findViewById(R.id.carouselView);
        carouselView!!.pageCount = sampleImages.size;

        // Lot of the carousel code from the library's docs (citing)
        carouselView!!.setImageListener { position, imageView ->
            Picasso.get().load(sampleImages[position]).into(imageView)
        }

        // Comments
//        database = FirebaseDatabase.getInstance()
//        val commentsRef = database.getReference("comments").child(postId)
//
//        // Slide show
//        commentsRef.addValueEventListener(object : ValueEventListener {
//
//            override fun onCancelled(p0: DatabaseError) {
//                Toast.makeText(this@BoardActivity, "Failed to get the post", Toast.LENGTH_LONG)
//                    .show()
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                mCommentsAdapter = CommentsAdapter(applicationContext)
//                mListViewComments.adapter = mCommentsAdapter
//                mCommentsAdapter.add(Comment("asdasd", "qweqweqwe"))
//                mCommentsAdapter.add(Comment("asdqweasd", "qwe"))
//                mCommentsAdapter.add(Comment("asdqweasd", "qwe"))
//                mCommentsAdapter.add(Comment("asdqweasd", "qwe"))
//                mCommentsAdapter.notifyDataSetChanged()
//
//                updateListSize()
//            }
//        })
//        // Likes

        mLikesTextView.text = numLikes;
        setColor()
    }

    private fun updateListSize() {
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
    }

    fun handleLikeBtnOnPress(view: View) {
        if (isLiked) {
            isLiked = false
            // remove person from list of likes in firebase
        } else {
            isLiked = true
            // add person from list of likes in firebase
        }
        setColor()
    }

    fun handleCommentBtnOnPress(view: View) {
        // TODO: need to add username from firebase
        mCommentsAdapter.add(Comment("asdqweasd", mCommentEditText.text.toString(), LocalDateTime.now().toString()))
        mCommentsAdapter.notifyDataSetChanged()
        updateListSize()
        Toast.makeText(this, "Comment posted", Toast.LENGTH_LONG).show()
        mCommentEditText.setText("")
    }

    private fun setColor() {
        if (isLiked) {
            DrawableCompat.setTint(
                mLikeButton!!.background, ContextCompat.getColor(
                    this,
                    R.color.color_red
                )
            );
        } else {
            DrawableCompat.setTint(
                mLikeButton!!.background, ContextCompat.getColor(
                    this,
                    R.color.color_paragraph
                )
            );
        }
    }
}