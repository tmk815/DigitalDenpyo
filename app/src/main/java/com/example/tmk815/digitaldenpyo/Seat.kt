package com.example.tmk815.digitaldenpyo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("seat").child(user!!.uid).child(editSeat.text.toString())

                myRef.setValue((editCount.text.toString()).toInt())
            }
        }
    }
}
