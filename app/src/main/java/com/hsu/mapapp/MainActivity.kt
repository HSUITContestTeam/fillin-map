package com.hsu.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hsu.mapapp.databinding.ActivityMainBinding
import com.hsu.mapapp.databinding.ActivityMapBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appbarc: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nhf = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        appbarc = AppBarConfiguration(nhf.navController.graph)
        setupActionBarWithNavController(nhf.navController, appbarc)

        binding.bottomNavigationView.setupWithNavController(nhf.navController)
    }
}