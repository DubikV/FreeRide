package com.gmail.vanyadubyk.freeride.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory


object DrawableUtils {

    fun resizeMapIcons(mContext : Context, iconName: String, width: Int, height: Int): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(iconName, "drawable", mContext.packageName)
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

}