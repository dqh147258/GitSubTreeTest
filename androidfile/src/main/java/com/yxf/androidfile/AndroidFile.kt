package com.yxf.androidfile

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import java.io.InputStream
import java.io.OutputStream

interface AndroidFile {

    fun requestAccessPermission(activity: FragmentActivity, callback: AndroidFile.(result: Boolean) -> Unit)

    fun hasPermission(): Boolean

    fun exist(): Boolean

    fun length(): Long

    fun name(): String

    fun getDescription(): String

    fun getInputStream(): InputStream

    fun getOutputStream(append: Boolean = false): OutputStream

    fun delete(): Boolean

    fun update() {

    }

}