package com.gmail.vanyadubik.freeride.activity.login


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.fragment.MenuFragment

class LoginActivity : AppCompatActivity(){


    val mFragmentManager : FragmentManager = supportFragmentManager
    lateinit var selectedFragment : Fragment;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.elevation = 0f
        selectedFragment =  MenuFragment().getInstance()
        initSelectedFragment()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (mFragmentManager.backStackEntryCount== 0) {
            finish()
        } else {
            mFragmentManager.popBackStack()
        }
    }

    private fun initSelectedFragment() {

        val ft = mFragmentManager.beginTransaction()
        ft.replace(R.id.content_main_frame, selectedFragment).commit()

    }

    fun showNextFragment(newFragment: Fragment) {

        val ft = mFragmentManager.beginTransaction()
        selectedFragment = newFragment
        ft.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        ft.replace(R.id.content_main_frame, selectedFragment)
        ft.addToBackStack(null)
        ft.commit()

    }

    fun showDownFragment(newFragment: Fragment) {

        val ft = mFragmentManager.beginTransaction()
        selectedFragment = newFragment
        ft.setCustomAnimations(
            R.anim.enter_from_bottom,
            R.anim.exit_to_top,
            R.anim.enter_from_top,
            R.anim.exit_to_bottom
        )
        ft.replace(R.id.content_main_frame, selectedFragment)
        ft.addToBackStack(null)
        ft.commit()

    }

}
