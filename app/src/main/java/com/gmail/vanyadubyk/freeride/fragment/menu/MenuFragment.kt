package com.gmail.vanyadubyk.freeride.fragment.menu


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.vanyadubyk.freeride.R
import com.gmail.vanyadubyk.freeride.activity.login.LoginActivity
import com.gmail.vanyadubyk.freeride.fragment.login.LoginFragment
import com.gmail.vanyadubyk.freeride.fragment.register.RegistrationFragment
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : Fragment(){
    private val LAYOUT = R.layout.fragment_menu

    fun getInstance(): MenuFragment {

        val args = Bundle()
        val fragment = MenuFragment()
        fragment.setArguments(args)
        return fragment
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(LAYOUT, container, false)

        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.login)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginEmail.setOnClickListener {
            (activity as LoginActivity).showNextFragment(LoginFragment().getInstance())
        }

        loginFacebook.setOnClickListener {

        }

        registrationBtn.setOnClickListener {
            (activity as LoginActivity).showNextFragment(RegistrationFragment().getInstance())
        }
    }
}
