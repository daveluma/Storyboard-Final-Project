package com.example.storyboardapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.BoardActivity
import com.example.postactivity.WritePostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class Home : AppCompatActivity() {
    private lateinit var createPost : Button
    private lateinit var boardPost : Button
    private lateinit var dashboard : Button
    private lateinit var gridView: GridView
    private lateinit var images: IntArray
    private lateinit var posts: ArrayList<Post>
    private lateinit var database: FirebaseDatabase
    private  lateinit var postRef: DatabaseReference
    private lateinit var logoutButton: Button
    private var mAuth: FirebaseAuth? = null
    private  lateinit var uid : String
//    private lateinit var titles: Array<String>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        createPost = findViewById(R.id.buttonCreatePost)
        boardPost = findViewById(R.id.buttonBoardPost)
        dashboard = findViewById(R.id.buttonDashboard)
        logoutButton = findViewById(R.id.buttonLogout)

        createPost.setOnClickListener {
            startActivity(Intent(this, WritePostActivity::class.java))
        }

        boardPost.setOnClickListener {
            startActivity(Intent(this, BoardActivity::class.java))

        }

        dashboard.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
        }
        logoutButton.setOnClickListener {
            mAuth!!.signOut()
            finish()
        }



        // dynamic grid implementation https://www.youtube.com/watch?v=RtitGGmvvTI
        posts = ArrayList()
        gridView = findViewById(R.id.gridView)
        gridView.adapter = CustomAdapter(this ,posts)
        // replace contents of intArrayOf(...) with user's images7
        images = intArrayOf(R.drawable.ic_launcher_background)

        database = FirebaseDatabase.getInstance();
        postRef = database.getReference("posts")
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth!!.currentUser!!.uid
        val reference: DatabaseReference = postRef
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val postFromDB = Post(
                        snapshot.child("uid").value as String,
                        snapshot.child("title").value as String,
                        snapshot.child("body").value as String,
                        snapshot.child("genre").value as String,
                        snapshot.child("images").value as List<String>)
                    posts.add(postFromDB)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    // class for grid view adapter
    public class CustomAdapter: BaseAdapter {
        private lateinit var context: Context
        private lateinit var layoutInflater: LayoutInflater
        private lateinit var posts: ArrayList<Post>
        constructor(context: Context, posts : ArrayList<Post>) : super() {
            this.context = context
            this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as (LayoutInflater)
            this.posts = posts
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate(R.layout.story_board_cover_view, parent, false)
            val title = view.findViewById<TextView>(R.id.textViewCoverTitle)
            val description = view.findViewById<TextView>(R.id.textViewCoverDescription)

            title.text = posts[position].title
            description.text = posts[position].body

            return view

        }

        override fun getItem(position: Int): Any {
            TODO("Get post from db")
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return posts.size
        }

    }
}