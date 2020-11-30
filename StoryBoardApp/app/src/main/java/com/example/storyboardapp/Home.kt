package com.example.storyboardapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import com.example.myapplication.BoardActivity
import com.example.postactivity.WritePostActivity

class Home : AppCompatActivity() {
    private lateinit var createPost : Button
    private lateinit var boardPost : Button
    private lateinit var dashboard : Button
    private lateinit var gridView: GridView
    private lateinit var images: IntArray;
//    private lateinit var titles: Array<String>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        createPost = findViewById(R.id.buttonCreatePost)
        boardPost = findViewById(R.id.buttonBoardPost)
        dashboard = findViewById(R.id.buttonDashboard)

        createPost.setOnClickListener {
            startActivity(Intent(this, WritePostActivity::class.java))
        }

        boardPost.setOnClickListener {
            startActivity(Intent(this, BoardActivity::class.java))

        }

        dashboard.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
        }


        // dynamic grid implementation https://www.youtube.com/watch?v=RtitGGmvvTI
        gridView = findViewById(R.id.gridView)
        gridView.adapter = CustomAdapter(this)
        // replace contents of intArrayOf(...) with user's images
        images = intArrayOf(R.drawable.ic_launcher_background)
//        titles = Array<String>(1);
//        titles[0] = "test name"

    }

    // class for grid view adapter
    public class CustomAdapter: BaseAdapter {
        private lateinit var imageNames: Array<String>
        private lateinit var imagePhotos: Array<String>
        private lateinit var context: Context
        private lateinit var layoutInflater: LayoutInflater

//        constructor(imageNames: Array<String>, imagePhotos: Array<String>, context: Context) : super() {
//            this.imageNames = imageNames
//            this.imagePhotos = imagePhotos
//            this.context = context
//            this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as (LayoutInflater)
//        }
        constructor(context: Context) : super() {
            this.context = context
            this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as (LayoutInflater)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate(R.layout.story_board_cover_view, parent, false)
            val title = view.findViewById<TextView>(R.id.textViewCoverTitle)
            title.text = "Sonic the HedgeHog lmao"
            val description = view.findViewById<TextView>(R.id.textViewCoverDescription)
            description.text = "Sonic the Hedgehog is a really cool movie you should totally see it"

            return view

        }

        override fun getItem(position: Int): Any {
            TODO("Get post from db")
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return 20
        }

    }
}