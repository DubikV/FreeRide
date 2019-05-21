package com.gmail.vanyadubik.freeride.fragment.menu

import com.gmail.vanyadubik.freeride.model.dto.*

interface MenuMVPContract {


    interface View {

        fun onLogin()

        fun onErrorApi(textError: String)

    }

    interface Repository {

        fun loginEmail(loginRequest: LoginRequest)

        fun loginFB()

    }


}
