package com.example.postactivity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TestLooperManager
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import com.example.storyboardapp.R
import java.util.jar.Manifest

class WritePostActivity : AppCompatActivity() {
    private val  REQUEST_CODE = 100

    private var position = 0

    private val PICK_IMAGES_CODE = 0

    var images : ArrayList<Uri>? = null
    private lateinit var imageView : ImageView
    private lateinit var imageSwitcher : ImageSwitcher
    private var selected : Boolean = false
    private lateinit var no_text : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        no_text = findViewById(R.id.No_im)
        val body = findViewById<EditText>(R.id.Body)
        val title = findViewById<EditText>(R.id.Title)
        val post_btn = findViewById<Button>(R.id.Post_button)
        val add_btn = findViewById<ImageButton>(R.id.Add_button)

        val arrow_up = findViewById<ImageView>(R.id.arrow_up)
        val arrow_down = findViewById<ImageView>(R.id.arrow_down)

        imageView = findViewById(R.id.imageView)
        imageSwitcher = findViewById<ImageSwitcher>(R.id.imageView2)
        imageSwitcher.setFactory { ImageView(applicationContext) }

        images = ArrayList()

        val spinner: Spinner = findViewById(R.id.GenreSpin)
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

        arrow_up.setOnClickListener{
            if (position < images!!.size-1){
                position++
                imageSwitcher.setImageURI(images!![position])
            }
            else{
                Toast.makeText(this,"No more images...", Toast.LENGTH_SHORT).show()
            }
        }

        arrow_down.setOnClickListener{
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
                var spin = spinner.selectedItem.toString()
                if (selected)
                {
                    //TODO send imageView to the next activity
                    Toast.makeText(applicationContext,"Title: ${title.text} \nBody: ${body.text} \nGenre: ${spin}",Toast.LENGTH_LONG).show()
                }

                //TODO send title, body, and genre to the next view
                Toast.makeText(applicationContext,"Success!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImageIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Images(s)"), PICK_IMAGES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        no_text.text = ""
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

            imageSwitcher.setImageURI(imageUri)
            position = 0
        }
    }

}