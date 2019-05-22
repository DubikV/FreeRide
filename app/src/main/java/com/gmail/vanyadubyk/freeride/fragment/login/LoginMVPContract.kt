package com.gmail.vanyadubyk.freeride.fragment.login

import com.gmail.vanyadubyk.freeride.model.dto.*

interface LoginMVPContract {


    interface View {

        fun onLogin()

        fun onErrorApi(textError: String)

    }

    interface Repository {

        fun login(loginRequest: LoginRequest)

    }


}
