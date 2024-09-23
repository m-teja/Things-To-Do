package com.project

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.base.BaseActivity

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.activity_home_main_content, fragment)
                .commit()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_home_main_content) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.activity_home_bottom_navigation).setupWithNavController(navController)

    }
}