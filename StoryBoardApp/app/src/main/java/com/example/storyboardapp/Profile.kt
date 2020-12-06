package com.example.storyboardapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.BoardActivity
import com.example.postactivity.WritePostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.time.LocalDateTime


class Profile : AppCompatActivity() {
    private lateinit var createPost: ImageView
    private lateinit var homeButton: ImageView
    private lateinit var profileButton: ImageView
    private lateinit var gridView: GridView
    private lateinit var images: IntArray
    private lateinit var posts: ArrayList<Post>
    protected lateinit var db: FirebaseFirestore
    private lateinit var logoutButton: Button
    private var mAuth: FirebaseAuth? = null
    private lateinit var uid: String
    private lateinit var myUser: TextView

    //    private lateinit var titles: Array<String>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        createPost = findViewById(R.id.buttonCreatePost)
        logoutButton = findViewById(R.id.buttonLogout)
        profileButton = findViewById(R.id.profileButton)
        homeButton = findViewById(R.id.homeButton)
        myUser = findViewById(R.id.pageTextView)



        createPost.setOnClickListener {
            startActivity(Intent(this, WritePostActivity::class.java))
        }

        homeButton.setOnClickListener {
            finish()
        }

        profileButton.setOnClickListener {
            var intent = Intent(this@Profile,Profile::class.java)
            intent.putExtra("uid",mAuth!!.uid.toString())
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            mAuth!!.signOut()
            finish()
        }


        // dynamic grid implementation https://www.youtube.com/watch?v=RtitGGmvvTI
        posts = ArrayList()
        gridView = findViewById(R.id.gridView)

        // replace contents of intArrayOf(...) with user's images7
        images = intArrayOf(R.drawable.ic_launcher_background)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        uid = intent.getStringExtra("uid").toString()

        FirebaseFirestore.getInstance().collection("users").document(uid.toString()).get().addOnSuccessListener {
            myUser.text = it.data?.get("author").toString()

        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong with getting the user's name", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        db.collection("posts").orderBy("createdAt").get().addOnSuccessListener {
            posts.clear()
            for (document in it.documents) {
                if ((document.data?.get("uid") as String) == uid) {
                    val newPost = Post(
                        document.data?.get("postId") as String,
                        document.data?.get("uid") as String,
                        document.data?.get("author") as String,
                        document.data?.get("title") as String,
                        document.data?.get("body") as String,
                        document.data?.get("genre") as String,
                        document.data?.get("images") as ArrayList<String>,
                        document.data?.get("createdAt") as String
                    )
                    posts.add(newPost)
                }
            }
            gridView.adapter = CustomAdapter(this, posts, db)
        }
    }


    // class for grid view adapter
    public class CustomAdapter : BaseAdapter {
        private lateinit var context: Context
        private lateinit var layoutInflater: LayoutInflater
        private lateinit var posts: ArrayList<Post>
        private lateinit var db : FirebaseFirestore

        constructor(context: Context, posts: ArrayList<Post>, db: FirebaseFirestore) : super() {
            this.db = db
            this.context = context
            this.layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as (LayoutInflater)
            this.posts = posts
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate(R.layout.story_board_cover_view, parent, false)
            val title = view.findViewById<TextView>(R.id.textViewCoverTitle)
            val description = view.findViewById<TextView>(R.id.textViewCoverDescription)
            val genre = view.findViewById<ImageView>(R.id.imageViewGenre)
            val author = view.findViewById<TextView>(R.id.textViewAuthor)
            val numLikes = view.findViewById<TextView>(R.id.textViewNumLikes)
            val image = view.findViewById<ImageView>(R.id.imageViewCoverImage)

            title.text = posts[position].title
            if (posts[position].images.size > 0) {
                var storageRef = FirebaseStorage.getInstance().reference
                storageRef.child("images/" + posts[position].images[0]).downloadUrl.addOnSuccessListener { url ->
                    Picasso.get().load(url).into(image)
                }.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Something went wrong with getting images",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            author.text = posts[position].author
            if (posts[position].body.length < 162)
                description.text = posts[position].body
            else
                description.text = posts[position].body.subSequence(0, 162).toString() + "..."

            when (posts[position].genre) {
                "Comedy" -> genre.setColorFilter(Color.parseColor("#fcd703"))
                "Action" -> genre.setColorFilter(Color.parseColor("#0356fc"))
                "Romance" -> genre.setColorFilter(Color.parseColor("#ff45ae"))
                "Adventure" -> genre.setColorFilter(Color.parseColor("#1fff17"))
                "Mystery" -> genre.setColorFilter(Color.parseColor("#ff8c00"))
                "Thriller" -> genre.setColorFilter(Color.parseColor("#7b07f0"))
                "Horror" -> genre.setColorFilter(Color.parseColor("#fc0303"))
                "Educational" -> genre.setColorFilter(Color.parseColor("#ffffff"))
                else -> genre.setColorFilter(Color.parseColor("#000000"))
            }

            view.setOnClickListener {
                val postIntent = Intent(context, BoardActivity::class.java)
                postIntent.putExtra("postId", posts[position].postId)
                postIntent.putExtra("author", posts[position].author)
                postIntent.putExtra("title", posts[position].title)
                postIntent.putExtra("body", posts[position].body)
                postIntent.putExtra("genre", posts[position].genre)
                postIntent.putExtra("authorUid", posts[position].uid)
                postIntent.putStringArrayListExtra("images", posts[position].images)
                context.startActivity(postIntent)
            }
            setNumLikes(posts[position].postId, numLikes)
            return view
        }

        override fun getItem(position: Int): Any {
            return "not needed"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return posts.size
        }

        private fun setNumLikes(postId: String, view : TextView) {
            FirebaseFirestore.getInstance().collection("likes")
                .document(postId).collection("userLikes").get().addOnSuccessListener {
                    view.text = it.size().toString() + " likes"
                }.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Something went wrong with getting the likes",
                        Toast.LENGTH_LONG
                    ).show()
                }

        }

    }
}