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

    const val TOKEN_HEADER = "X-Authorization-Token"

    // Cash
    const val TOKEN = "_token"

    // Review evaluation
    const val EVALUATION_AVAILABLE = 5
    const val EVALUATION_TROUBLESOME = 3
    const val EVALUATION_INACCESSIBLE = 1
    const val EVALUATION_NO_INFO = 0

}