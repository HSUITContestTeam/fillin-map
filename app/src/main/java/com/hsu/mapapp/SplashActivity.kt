package com.hsu.mapapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hsu.mapapp.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 7300//7300 // 1000 ->1sec
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        imageView = findViewById(R.id.loadingIMG)
        Glide.with(this).load(R.raw.logo2).into(imageView)

        Handler(Looper.getMainLooper()).postDelayed({
            // SPLASH_TIME_OUT 끝나면 실행됨
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }

    fun LoadingAnimation(view : View){

    }
}