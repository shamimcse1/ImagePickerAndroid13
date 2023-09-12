package com.example.imagepicker

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var imagePicker: Button
    private lateinit var imageView: ImageView
    private lateinit var pickSingleMediaLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: ImagePickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel  = ViewModelProvider(this)[ImagePickerViewModel::class.java]
        imagePicker = findViewById(R.id.button_pick_photo)
        imageView = findViewById(R.id.imageView)

        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                it?.let { it1 -> viewModel.setImageUri(it1) }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        pickSingleMediaLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this, "Failed picking media.", Toast.LENGTH_SHORT).show()
                } else {
                    val uri = it.data?.data
                    uri?.let { it1 -> viewModel.setImageUri(it1) }
                    showSnackBar("SUCCESS: ${uri?.path}")
                }
            }


        imagePicker.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickSingleMediaLauncher.launch(
                    Intent(MediaStore.ACTION_PICK_IMAGES)
                )
            } else {

                galleryLauncher.launch("image/*")
            }

        }

        observeViewState()


      /*  if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Okkkkkkkk.", Toast.LENGTH_SHORT).show()

        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {

                if (it) {

                } else {
                    Snackbar.make(
                        findViewById<View>(android.R.id.content).rootView,
                        "Please grant Notification permission from App Settings",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }*/

    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG,
        )
        // Set the max lines of SnackBar
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines =
            10
        snackBar.show()
    }

    private fun observeViewState() {
        viewModel.imageUri.observe(this) { viewState ->
            Log.d("Uri",viewState.toString())
            imageView.setImageURI(viewState)
        }
    }
}