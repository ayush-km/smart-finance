package com.ayushkm.smartfinance

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class StartScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}