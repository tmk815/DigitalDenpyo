package com.example.tmk815.digitaldenpyo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.menu_new.*

class NewMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_new)

        submitMenu.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("menu").child(editName.text.toString())

            myRef.setValue((editPrice.text.toString()).toInt())
        }
    }
}