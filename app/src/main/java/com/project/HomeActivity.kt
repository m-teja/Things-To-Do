package com.project

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.project.base.BaseActivity
import com.project.base.BaseFragment

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homeFragment = HomeFragment()
        val mapFragment = MapFragment()

        findViewById<NavigationBarView>(R.id.activity_home_bottom_navigation).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.map -> setCurrentFragment(mapFragment)
            }
            true
        }
    }


    private fun setCurrentFragment(fragment: BaseFragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.activity_home_main_content, fragment)
            commit()
    }
}