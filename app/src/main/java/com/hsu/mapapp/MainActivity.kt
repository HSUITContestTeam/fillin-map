package com.hsu.mapapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var appbarc: AppBarConfiguration
    private lateinit var mainBinding: ActivityMainBinding

    //private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebase: FirebaseFirestore
    private val uid = Firebase.auth.currentUser?.uid

    private var welcom: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(mainBinding.root)
        setNavigation()
        firebase = FirebaseFirestore.getInstance()
        if(welcom == 0)
            WelcomeUserMessage()
    }
    private fun WelcomeUserMessage() {
        welcom = 1
        val myRef = firebase.collection("users").document("$uid")
        myRef.get()
            .addOnSuccessListener {
                Snackbar.make(mainBinding.root,it.get("name").toString()+"님 환영합니다😉",Snackbar.LENGTH_LONG).show()
            }
    }

    private fun setNavigation() {
        val nhf =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        appbarc = AppBarConfiguration(nhf.navController.graph)
        setupActionBarWithNavController(nhf.navController, appbarc)

        mainBinding.bottomNavigationView.setupWithNavController(nhf.navController)

        val iconColorStates = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                Color.parseColor("#b5b8bd"),
                Color.parseColor("#5E656B")
            )
        )

        mainBinding.bottomNavigationView.itemIconTintList = iconColorStates
        mainBinding.bottomNavigationView.itemTextColor = iconColorStates

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)

        return navController.navigateUp(appbarc) || super.onSupportNavigateUp()
    }




}