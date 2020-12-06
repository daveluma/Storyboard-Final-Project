package com.example.storyboardapp

import com.google.type.DateTime
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Post(
        val postId: String,
        val uid: String,
        val author: String,
        val title: String,
        val body: String,
        val genre: String,
        val images: ArrayList<String>,
        val createdAt: String
)
