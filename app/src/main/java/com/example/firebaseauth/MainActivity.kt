package com.example.firebaseauth

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseauth.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        //auth.signOut()

        binding.btnRegister.setOnClickListener {
            register()
        }
        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnUpload.setOnClickListener {
            uploadImage()
        }

        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            checkIsLoggedin()
        }


    }

    override fun onStart() {
        super.onStart()
        checkIsLoggedin()
    }

    private fun register() {

        var email = binding.etRegisterEmail.text.toString()
        var password = binding.etRegisterPassword.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                withContext(Dispatchers.Main) {
                    checkIsLoggedin()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error in Auth", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun login() {
        var email = binding.etLoginEmail.text.toString()
        var password = binding.etLoginPassword.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                withContext(Dispatchers.Main) {
                    checkIsLoggedin()
                }
            } catch (e: Exception) {

            }

        }

    }


    private fun uploadImage() {
        auth.currentUser?.let { user ->
            val userName = binding.etUploadUserName.text.toString()
            val photoUri =
                Uri.parse("android.resource://$packageName/${R.drawable.logo_black_square}")
            val updateProfiles = UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(photoUri)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(updateProfiles).await()
                    checkIsLoggedin()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Successfully Upload", Toast.LENGTH_LONG).show()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error in Upload", Toast.LENGTH_LONG).show()
                    }

                }


            }
        }
    }

    private fun checkIsLoggedin() {
        val user = auth.currentUser
        if (user == null) {
            binding.tvResult.text = "The User Not Logged In "
            binding.etLoginEmail.setText("")
            binding.etLoginPassword.setText("")

            binding.ivUpload.setImageURI(user?.photoUrl)
            binding.etUploadUserName.setText(user?.displayName)
        } else {
            binding.tvResult.text = "The User is Logged In "
            binding.ivUpload.setImageURI(user.photoUrl)
            binding.etUploadUserName.setText(user.displayName)
        }
    }


}