package com.gmail.vanyadubik.freeride.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gmail.vanyadubik.freeride.R

class RestorePassActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_restore_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)
        supportActionBar?.title = getString(R.string.restoring_access)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view : View = inflater.inflate(LAYOUT, container, false)
//
//        (activity as AppCompatActivity).setTheme(R.style.AppTheme_Map)
//        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)
//        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.login_email)
//
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }
}
