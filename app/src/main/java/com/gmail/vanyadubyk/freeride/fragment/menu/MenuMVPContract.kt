package com.gmail.vanyadubyk.freeride.fragment.menu

import com.gmail.vanyadubyk.freeride.model.dto.*

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
