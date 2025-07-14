package com.yxf.androidfile.java

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.FileUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.yxf.androidfile.*
import com.yxf.androidfile.startContractForResult
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class JavaFile(private val path: String) : AndroidFile {


    private val file by lazy { File(path) }

    private val internalPathList by lazy {
        val context = AndroidFileProvider.applicationContext
        listOf(
            context.cacheDir.absolutePath,
            context.filesDir.absolutePath,
            context.externalCacheDir!!.absolutePath,
            context.getExternalFilesDir(null)!!.absolutePath
        )
    }


    override fun requestAccessPermission(activity: FragmentActivity, callback: AndroidFile.(result: Boolean) -> Unit) {
        if (hasPermission()) {
            runOnMainThread {
                callback(this, true)
            }
        } else {
            activity.startContractForResult(ActivityResultContracts.RequestPermission(), Manifest.permission.WRITE_EXTERNAL_STORAGE) { result ->
                runOnMainThread {
                    callback(this, result)
                }
            }
        }
    }

    override fun hasPermission(): Boolean {
        val filePath = path
        internalPathList.forEach {
            if (filePath.contains(it)) {
                return true
            }
        }
        val absolutePath = file.absolutePath
        if (absolutePath != path) {
            internalPathList.forEach {
                if (absolutePath.contains(it)) {
                    return true
                }
            }
        }
        if (AndroidFileProvider.applicationContext == null) {
            return false
        }
        return ContextCompat.checkSelfPermission(AndroidFileProvider.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun exist(): Boolean {
        return file.exists()
    }

    override fun length(): Long {
        return file.length()
    }

    override fun name(): String {
        return file.name
    }

    override fun getDescription(): String {
        return file.absolutePath
    }

    override fun getInputStream(): InputStream {
        return FileInputStream(file)
    }

    override fun getOutputStream(append: Boolean): OutputStream {
        if (!file.exists()) {
            val parent = file.parentFile
            if (!parent.exists()) {
                parent.mkdirs()
            }
            file.createNewFile()
        }
        return FileOutputStream(file, append)
    }

    override fun delete(): Boolean {
        return file.delete()
    }


}