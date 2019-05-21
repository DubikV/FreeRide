package com.gmail.vanyadubik.freeride.common

object Consts {

    const val TAGLOG = "FreeRide"
    const val TAGLOG_SYNC = "FreeRide_Sync"

    // Settings
    const val CONNECT_TIMEOUT_SECONDS_RETROFIT = 180
    const val CONNECT_SERVER_URL = "https://yuryks.dev/freeride/api/v0.2/"
    const val POI_URL = "poi"
    const val POI_DETAIL_URL = "poi/{id}"
    const val POI_REVIEW_URL = "poi/{id}/review"
    const val REGISTER_URL = "user/register"
    const val LOGIN_URL = "user/login"
    const val LOGOUT_URL = "user/logout"
    const val RECOVERY_URL = "user/recovery"
    const val USER_DETAIL_URL = "user/{id}"
    const val USER_AVATAR_URL = "user/{id}/photo"

    const val TOKEN_HEADER = "X-Authorization-Token"
    const val TYPE_LOGIN_EMAIL = "email"
    const val TYPE_LOGIN_FB = "fb"

    // Cash
    const val TOKEN = "_token"
    const val USER_ID = "_user_id"

    // Review evaluation
    const val EVALUATION_AVAILABLE = 5
    const val EVALUATION_TROUBLESOME = 3
    const val EVALUATION_INACCESSIBLE = 1
    const val EVALUATION_NO_INFO = 0

}