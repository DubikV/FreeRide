package com.gmail.vanyadubik.freeride.fragment.register


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
import com.gmail.vanyadubik.freeride.model.dto.RegisterRequest
import com.gmail.vanyadubik.freeride.utils.ActivityUtils
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment : Fragment() , RegisterMVPContract.View {

    private val LAYOUT = R.layout.fragment_registration
    private lateinit var registerRepository: RegisterRepository

    fun getInstance(): RegistrationFragment {

        val args = Bundle()
        val fragment = RegistrationFragment()
        fragment.setArguments(args)
        return fragment
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(LAYOUT, container, false)

        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_white)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.registration)

        registerRepository = RegisterRepository(activity as AppCompatActivity, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        regEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                registrationBtn.isEnabled = p0?.length!! >5 && regName.text.toString().length>2 && regPassword.text.toString().length>2
            }
        })

        regName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                registrationBtn.isEnabled = p0?.length!! >5 && regEmail.text.toString().length>2 && regPassword.text.toString().length>2
            }
        })

        regPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                registrationBtn.isEnabled = p0?.length!! >5 && regName.text.toString().length>2 && regEmail.text.toString().length>2
            }
        })

        registrationBtn.setOnClickListener {
            registerRepository?.register(
                RegisterRequest(
                    regEmail.text.toString(),
                    regName.text.toString(),
                    regPassword.text.toString(), "")
            )
        }

        loginFacebook.setOnClickListener {
            registerRepository?.loginFB()
        }


    }

    override fun onErrorApi(textError: String) {
        ActivityUtils.showMessage(activity, "", null, textError)
    }

    override fun onRegister() {
        activity?.setResult(Activity.RESULT_OK, Intent())
    }

}
