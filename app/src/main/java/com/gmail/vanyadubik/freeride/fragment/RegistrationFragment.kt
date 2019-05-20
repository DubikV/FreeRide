package com.gmail.vanyadubik.freeride.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.vanyadubik.freeride.R

class RegistrationFragment : Fragment(){
    private val LAYOUT = R.layout.fragment_registration

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
