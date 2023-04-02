/*
package com.example.gps_location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gps_location.databinding.ActivityNavigateBinding


private const val TAG_CALENDER = "maps_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_MY_PAGE = "my_page_fragment"

class NavigateActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNavigateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigateBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment-> setFragment(TAG_CALENDER, HomeFragment())
                R.id.mapsFragment-> setFragment(TAG_HOME, MapsFragment())
                R.id.settingFragment-> setFragment(TAG_MY_PAGE, SettingFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val calender = manager.findFragmentByTag(TAG_CALENDER)
        val home = manager.findFragmentByTag(TAG_HOME)
        val myPage = manager.findFragmentByTag(TAG_MY_PAGE)

        if (calender != null){
            fragTransaction.hide(calender)
        }

        if (home != null){
            fragTransaction.hide(home)
        }

        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        if (tag == TAG_CALENDER) {
            if (calender!=null){
                fragTransaction.show(calender)
            }
        }
        else if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_MY_PAGE){
            if (myPage != null){
                fragTransaction.show(myPage)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}*/
