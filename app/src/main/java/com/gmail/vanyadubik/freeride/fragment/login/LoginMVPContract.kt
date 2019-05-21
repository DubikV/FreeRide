package com.gmail.vanyadubik.freeride.fragment.login

import com.gmail.vanyadubik.freeride.model.dto.*

interface LoginMVPContract {


    interface View {

        fun onLogin()

        fun onErrorApi(textError: String)

    }

    interface Repository {

        fun login(loginRequest: LoginRequest)

    }


}
