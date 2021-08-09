package com.hsu.mapapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hsu.mapapp.databinding.ActivityMainBinding
import com.hsu.mapapp.login.AddUser

class MainActivity : AppCompatActivity() {
    private lateinit var appbarc: AppBarConfiguration
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var firestore : FirebaseFirestore
    private lateinit var fbAuth : FirebaseAuth
    //private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        firestore = FirebaseFirestore.getInstance() //Firestore선언
        fbAuth = FirebaseAuth.getInstance() // Firebase Auth 선언

        if(true){
            var userInfo = AddUser()

            userInfo.uid = fbAuth?.uid //유저 정보 가져오기
            userInfo.userId = fbAuth?.currentUser?.email
            //Firestore데이터 베이스에 업로드
            firestore?.collection("users")?.document(fbAuth?.uid.toString())?.set(userInfo)

        }

        setNavigation()

    }

    private fun setNavigation() {
        val nhf =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        appbarc = AppBarConfiguration(nhf.navController.graph)
        setupActionBarWithNavController(nhf.navController, appbarc)

        mainBinding.bottomNavigationView.setupWithNavController(nhf.navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)

        return navController.navigateUp(appbarc) || super.onSupportNavigateUp()
    }




}