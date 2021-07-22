package com.hsu.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hsu.mapapp.databinding.ActivityLoginBinding
import com.hsu.mapapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appbarc: AppBarConfiguration
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var loginBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setNavigation()
    }


    private fun setNavigation() {
        val nhf =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        appbarc = AppBarConfiguration(nhf.navController.graph)
        setupActionBarWithNavController(nhf.navController, appbarc)

        mainBinding.bottomNavigationView.setupWithNavController(nhf.navController)

    }





}