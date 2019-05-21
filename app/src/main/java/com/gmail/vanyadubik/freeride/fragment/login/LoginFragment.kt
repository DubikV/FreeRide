package com.gmail.vanyadubik.freeride.fragment.login


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.activity.RecoveryPassActivity
import com.gmail.vanyadubik.freeride.model.dto.LoginRequest
import com.gmail.vanyadubik.freeride.utils.ActivityUtils
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), LoginMVPContract.View {

    private val LAYOUT = R.layout.fragment_login
    private lateinit var loginRepository: LoginRepository

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

        loginRepository = LoginRepository(activity as AppCompatActivity, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forgotPassword.setOnClickListener {
            startActivity(Intent(activity, RecoveryPassActivity::class.java))
        }

        loginEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginBtn.isEnabled = p0?.length!! >5
            }
        })

        loginBtn.setOnClickListener {
            loginRepository?.login(
                LoginRequest(loginEmail.text.toString(),
                     loginPassword.text.toString(), ""))
        }
    }

    override fun onLogin() {
        activity?.setResult(Activity.RESULT_OK, Intent())
    }

    override fun onErrorApi(textError: String) {
        ActivityUtils.showMessage(activity, "", null, textError)
    }

}
