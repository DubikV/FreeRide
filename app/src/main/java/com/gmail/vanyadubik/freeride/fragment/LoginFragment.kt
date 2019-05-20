package com.gmail.vanyadubik.freeride.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.activity.RestorePassActivity
import com.gmail.vanyadubik.freeride.activity.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(){
    private val LAYOUT = R.layout.fragment_login

    fun getInstance(): LoginFragment {

        val args = Bundle()
        val fragment = LoginFragment()
        fragment.setArguments(args)
        return fragment
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(LAYOUT, container, false)

        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_white)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.login_email)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forgotPassword.setOnClickListener {
            startActivity(Intent(activity, RestorePassActivity::class.java))
        }

        loginBtn.setOnClickListener {

        }
    }
}
