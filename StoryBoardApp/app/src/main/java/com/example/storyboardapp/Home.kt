package com.example.storyboardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.BoardActivity
import com.example.postactivity.WritePostActivity

class Home : AppCompatActivity() {
    private lateinit var createPost : Button
    private lateinit var boardPost : Button
    private lateinit var dashboard : Button

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
    }
}