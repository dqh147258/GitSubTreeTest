package com.yxf.androidfile

import android.content.Context
import android.content.pm.ProviderInfo
import androidx.core.content.FileProvider

internal class AndroidFileProvider: FileProvider() {


    companion object {

        lateinit var applicationContext: Context

    }


    override fun attachInfo(context: Context, info: ProviderInfo) {
        applicationContext = context.applicationContext
        super.attachInfo(context, info)
    }



}