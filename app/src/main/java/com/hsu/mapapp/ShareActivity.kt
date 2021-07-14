package com.hsu.mapapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.hsu.mapapp.databinding.ActivityShareBinding

class ShareActivity: AppCompatActivity() {
    private lateinit var sharebinding : ActivityShareBinding
    private lateinit var  appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharebinding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(sharebinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shareFragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController,appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.shareFragment)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_friends -> {
                item.onNavDestinationSelected(findNavController(R.id.shareFragment))
                println("친구목록 클릭")
            }
            R.id.item_group -> {
                item.onNavDestinationSelected(findNavController(R.id.shareFragment))
            }
            R.id.item_search ->item.onNavDestinationSelected(findNavController(R.id.shareFragment))
        }
        return true
    }

}