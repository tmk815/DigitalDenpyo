package com.example.tmk815.digitaldenpyo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")

        list_setting.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                val settingIntent = Intent(this, NewMenu::class.java)
                startActivity(settingIntent)
            } else if (position == 1) {
                val settingIntent = Intent(this, Seat::class.java)
                startActivity(settingIntent)
            }
        }
    }
}
