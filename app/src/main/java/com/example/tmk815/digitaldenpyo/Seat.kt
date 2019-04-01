package com.example.tmk815.digitaldenpyo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_seat.*


class Seat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")

        submitSeat.setOnClickListener {
            submitSeat.setOnClickListener {
                val user = FirebaseAuth.getInstance().currentUser

                user?.let {
                    // Name, email address, and profile photo Url
                    val database = FirebaseDatabase.getInstance()
                    val uid = user.uid
                    val myRef = database.getReference("seat").child(uid).child(editSeat.text.toString())

                    myRef.setValue((editCount.text.toString()).toInt())
                }
            }
        }
    }
}
