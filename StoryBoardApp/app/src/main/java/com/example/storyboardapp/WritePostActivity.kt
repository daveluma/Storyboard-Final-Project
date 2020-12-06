package com.example.postactivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.storyboardapp.Post
import com.example.storyboardapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.type.DateTime
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class WritePostActivity : AppCompatActivity() {


    private var position = 0
    private val PICK_IMAGES_CODE = 0
    var images : ArrayList<Uri>? = null
    private lateinit var imageView : ImageView
    private lateinit var imageSwitcher : ImageSwitcher
    private var selected : Boolean = false
    private lateinit var no_text : TextView
    private lateinit var db : FirebaseFirestore

    private var mAuth: FirebaseAuth? = null
    private  lateinit var uid : String

    private lateinit var body: EditText
    private lateinit var title: EditText
    private lateinit var post_btn: Button
    private lateinit var add_btn: ImageButton
    private lateinit var arrow_up: ImageView
    private lateinit var arrow_down: ImageView
    private lateinit var spin: String
    private lateinit var spinner: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var storageReference: StorageReference
    private lateinit var imageURLs : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        db = FirebaseFirestore.getInstance()

        mAuth = FirebaseAuth.getInstance()
        uid = mAuth!!.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference


        no_text = findViewById(R.id.No_im)
        body = findViewById(R.id.Body)
        title = findViewById(R.id.Title)
        post_btn = findViewById(R.id.Post_button)
        add_btn = findViewById(R.id.Add_button)
        progressBar = findViewById(R.id.progressBar1)
        progressBar.visibility = View.GONE

        arrow_up = findViewById(R.id.arrow_up)
        arrow_down = findViewById(R.id.arrow_down)

        imageView = findViewById(R.id.imageViewTest)
        imageSwitcher = findViewById<ImageSwitcher>(R.id.imageView2)
        imageSwitcher.setFactory { ImageView(applicationContext) }

        images = ArrayList()
        imageURLs = ArrayList()
        spinner = findViewById(R.id.GenreSpin)


        ArrayAdapter.createFromResource(
                this,
                R.array.GenreSpin,
                R.layout.spinner_simp
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }


        add_btn.setOnClickListener{
            pickImageIntent()
        }

        arrow_down.setOnClickListener{
            if (position < images!!.size-1){
                position++
                imageSwitcher.setImageURI(images!![position])
            }
            else{
                Toast.makeText(this,"No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        arrow_up.setOnClickListener{
            if (position > 0){
                position--
                imageSwitcher.setImageURI(images!![position])
            }
            else{
                Toast.makeText(this,"No more images...", Toast.LENGTH_SHORT).show()
            }
        }


        post_btn.setOnClickListener{
            if (body.text.isEmpty())
            {
                Toast.makeText(applicationContext,"There is no body!",Toast.LENGTH_LONG).show()
            }
            else if(title.text.isEmpty())
            {
                Toast.makeText(applicationContext,"There is no title!",Toast.LENGTH_LONG).show()
            }
            else{
                progressBar.visibility = View.VISIBLE
                spin = spinner.selectedItem.toString()

                //TODO send title, body, and genre to the next view
                Toast.makeText(applicationContext,"Success!",Toast.LENGTH_LONG).show()
                uploadImages()
                val mAuth = FirebaseAuth.getInstance()
                var name = ""

                FirebaseFirestore.getInstance().collection("users").document(mAuth.uid.toString()).get().addOnSuccessListener {
                    name = it.data?.get("author").toString()
                    val postDoc = db.collection("posts").document()
                    val newPost = Post(postDoc.id, uid, name,  title.text.toString(), body.text.toString(), spin, imageURLs, LocalDateTime.now().toString())
                    postDoc.set(newPost).addOnSuccessListener() {
                        finish()
                    }.addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Something went wrong with uploading your images", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Something went wrong with uploading your name", Toast.LENGTH_LONG).show()
                }

            }

        }


    }

    private fun pickImageIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Images(s)"), PICK_IMAGES_CODE)
        selected = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        no_text.text = ""
        images = ArrayList()
        //multiple
        if (requestCode == PICK_IMAGES_CODE && resultCode == Activity.RESULT_OK && data!!.clipData != null){
            val count = data.clipData!!.itemCount
            for (i in 0 until count){
                val imageUri = data.clipData!!.getItemAt(i).uri
                images!!.add(imageUri)
            }
            imageSwitcher.setImageURI(images!![0])
            position = 0
        }
        //single
        else {
            val imageUri = data?.data
            println("here")
            println(imageUri)
            images!!.add(imageUri!!)
            imageSwitcher.setImageURI(imageUri)
            position = 0
        }
    }

    private fun uploadImages() {
        for (i in 0 until images!!.size) {
            val img = images!![i]
            val name = "${UUID.randomUUID()}"
            imageURLs.add(name)
            storageReference.child("images/${name}").putFile(img)

        }
    }

}