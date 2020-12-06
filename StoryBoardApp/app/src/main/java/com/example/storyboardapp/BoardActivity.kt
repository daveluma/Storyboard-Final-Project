package com.example.myapplication

import android.R.attr.radius
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.storyboardapp.Post
import com.example.storyboardapp.Profile
import com.example.storyboardapp.R
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var database: FirebaseFirestore
    private lateinit var mTitleTextView: TextView
    private lateinit var mAuthorTextView: TextView
    private lateinit var mNumLikesTextView: TextView
    private lateinit var mBodyTextView: TextView
    private lateinit var mGenreTextView: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var postId: String
    private var isLiked = false
    private var numLikes = 0
    var carouselView: CarouselView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        postId = intent.getStringExtra("postId").toString()
        val title = intent.getStringExtra("title")
        val body = intent.getStringExtra("body")
        val genre = intent.getStringExtra("genre")
        val author = intent.getStringExtra("author")
        val authorUid = intent.getStringExtra("authorUid")
        val images = intent.getStringArrayListExtra("images")
        isLiked = false

        // Firebase
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // UI
        mLikeButton = findViewById(R.id.buttonLike)
        mAuthorTextView = findViewById(R.id.textViewAuthor)
        mListViewComments = findViewById(R.id.listViewComments)
        mCommentEditText = findViewById(R.id.editTextComment)
        mLikesTextView = findViewById(R.id.textViewNumLikes)
        mTitleTextView = findViewById(R.id.textViewTitle)
        mNumLikesTextView = findViewById(R.id.textViewNumLikes)
        mBodyTextView = findViewById(R.id.textViewBody)
        mGenreTextView = findViewById(R.id.textViewGenre)

        // Set Initial stuff
        mTitleTextView.text = title
        mBodyTextView.text = body
        mGenreTextView.text = genre
        mAuthorTextView.text = author
        setIsLiked()
        setNumLikes()

        mAuthorTextView.setOnClickListener{
            val intent = Intent(this@BoardActivity,Profile::class.java)
            intent.putExtra("uid",authorUid)
            startActivity(intent)
        }

        // if genre is "Challenge", set background to yellow
        if (genre == "Challenge") {
            mTitleTextView.text =  "Challenge: ${mTitleTextView.text}"
            mTitleTextView.setTextColor(Color.parseColor("#fcd703"))
        }
        var storageRef = FirebaseStorage.getInstance().reference
        carouselView = findViewById(R.id.carouselView);

        // Slide show
        // Carousel code from the library's docs (citing)
        if (images!!.size > 0) {
            carouselView!!.pageCount = images!!.size;
            carouselView!!.setImageListener { position, imageView ->
                storageRef.child("images/" + images!![position]).downloadUrl.addOnSuccessListener { url ->
                    Picasso.get().load(url).into(imageView)
                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Something went wrong with getting the images",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            carouselView!!.visibility = View.GONE
        }
        mCommentsAdapter = CommentsAdapter(applicationContext)
        mListViewComments.adapter = mCommentsAdapter



        // Comments
        database.collection("comments").document(postId).collection("comments").orderBy("createdAt").get().addOnSuccessListener {
            for (document in it.documents) {
                mCommentsAdapter.add(Comment(document.get("username").toString(), document.get("comment").toString(), document.get("createdAt").toString()))
            }
            mCommentsAdapter.notifyDataSetChanged()
            updateListSize()
        }

    }

    private fun setNumLikes() {

        database.collection("likes")
            .document(postId).collection("userLikes").get().addOnSuccessListener {
                numLikes = it.size()
                updateLikesTextView()
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Something went wrong with getting the likes",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun setIsLiked() {
        database.collection("likes")
            .document(postId).collection("userLikes").document(mAuth.uid.toString()).get()
            .addOnSuccessListener {
                isLiked = it.data != null
                setColor()
            }.addOnFailureListener {
                isLiked = false
                setColor()
            }
    }

    // From a stack overflow post https://stackoverflow.com/questions/18010300/how-to-get-accurate-listview-height-at-runtime
    private fun updateListSize() {
        var totalHeight = 0
        for (i in 0 until mCommentsAdapter.count) {
            val listItem: View? = mCommentsAdapter.getView(i, null, mListViewComments)
            listItem!!.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = mListViewComments.layoutParams
        params.height = totalHeight + mListViewComments.dividerHeight * (mCommentsAdapter.count - 1) + mBodyTextView.height +  100
        mListViewComments.layoutParams = params
        mListViewComments.requestLayout()
    }

    fun handleLikeBtnOnPress(view: View) {
        if (isLiked) {
            isLiked = false
            // remove person from list of likes in firebase
            database.collection("likes").document(postId).collection("userLikes")
                .document(mAuth.uid.toString()).delete()
            numLikes--;
        } else {
            isLiked = true
            // add person from list of likes in firebase
            database.collection("likes").document(postId).collection("userLikes")
                .document(mAuth.uid.toString()).set(Pair("liked", true))
            numLikes++;
        }
        setColor()
        updateLikesTextView()
    }

    fun handleCommentBtnOnPress(view: View) {
        if (mCommentEditText.text.length == 0) {
            Toast.makeText(
                this,
                "Comment must not be empty",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        database.collection("users").document(mAuth.uid.toString()).get().addOnSuccessListener {
            val name = it.data?.get("author").toString()
            val comment =
                Comment(name, mCommentEditText.text.toString(), LocalDateTime.now().toString())
            database.collection("comments").document(postId).collection("comments").document()
                .set(comment)
            mCommentsAdapter.add(comment)
            mCommentsAdapter.notifyDataSetChanged()
            updateListSize()
            mCommentEditText.setText("")
            Toast.makeText(
                this,
                "Comment successfully posted",
                Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Something went wrong with uploading your images",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun updateLikesTextView() {
        mNumLikesTextView.text = numLikes.toString() + " Likes"
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