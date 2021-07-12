package com.hsu.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hsu.mapapp.databinding.ActivityLoginBinding

class loginActivity : AppCompatActivity() {
    private lateinit var loginBinding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
    }
}