package com.gmail.vanyadubyk.freeride.fragment.register

import com.gmail.vanyadubyk.freeride.model.dto.*

interface RegisterMVPContract {


    interface View {

        fun onRegister()

        fun onErrorApi(textError: String)

    }

    interface Repository {

        fun register(registerRequest: RegisterRequest)

        fun loginFB()

    }


}
