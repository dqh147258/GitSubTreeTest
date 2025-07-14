package com.yxf.androidfile.media

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.yxf.androidfile.*
import com.yxf.androidfile.getNameFromPath
import com.yxf.androidfile.getParentFromPath
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@RequiresApi(Build.VERSION_CODES.Q)
class MediaStoreFile(
    private val relativePath: String,
    private val folderName: String = Environment.DIRECTORY_DOWNLOADS,
    private val uri: Uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
) : AndroidFile {


    private val context by lazy { AndroidFileProvider.applicationContext }

    private val name = relativePath.getNameFromPath()
    private var path = relativePath.getParentFromPath()

    private val desc by lazy {
        val jo = JSONObject()
        jo.put("relative_path", relativePath)
        jo.put("uri", uri.toString())
        jo.put("folder_name", folderName)
        return@lazy jo.toString()
    }


    @Volatile
    private var fileInfoUpdated = false

    @Volatile
    private var fileLength = 0L
    @Volatile
    private var fileExist = false


    @Volatile
    private var fileUri: Uri? = null


    @SuppressLint("Range")
    override fun update() {
        var projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH,
            MediaStore.MediaColumns.SIZE
        )
        var selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?"
        var args = arrayOf(folderName.appendPath(path).addPathSuffix())
        var fu = context.contentResolver.query(uri, projection, selection, args, null)?.use {
            if (it.count == 0) {
                return@use null
            } else {
                while (it.moveToNext()) {
                    val fileName = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                    if (fileName == name || fileName.substringBeforeLast("(") == name) {
                        val fileId = it.getLong(it.getColumnIndex(MediaStore.MediaColumns._ID))
                        fileLength = it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))

                        return@use ContentUris.withAppendedId(uri, fileId)
                    }
                }
                return@use null
            }
        }
        if (fu == null) {
            fileExist = false
            fileUri = null
        } else {
            fileExist = true
            fileUri = fu
        }
        fileInfoUpdated = true
    }

    private fun create() {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, folderName.appendPath(path).addPathSuffix())
        val uri = context.contentResolver.insert(uri, values)
        if (uri != null) {
            fileExist = true
            fileLength = 0L
            fileUri = uri
        } else {
            throw RuntimeException("create MediaStoreFile(${folderName}/${relativePath.fixPath()}) failed")
        }
    }


    override fun requestAccessPermission(activity: FragmentActivity, callback: AndroidFile.(result: Boolean) -> Unit) {
        runOnMainThread {
            callback(this, true)
        }
    }

    override fun hasPermission(): Boolean {
        return true
    }

    override fun exist(): Boolean {
        if (!fileInfoUpdated) {
            update()
        }
        return fileExist
    }

    override fun length(): Long {
        if (!fileInfoUpdated) {
            update()
        }
        return fileLength
    }

    override fun name(): String {
        return name
    }

    override fun getDescription(): String {
        return desc
    }

    override fun getInputStream(): InputStream {
        if (!fileInfoUpdated) {
            update()
        }
        if (!fileExist) {
            throw FileNotFoundException("MediaStoreFile(${folderName}/${relativePath.fixPath()}) not exist")
        }
        return context.contentResolver.openInputStream(fileUri!!)!!

    }

    override fun getOutputStream(append: Boolean): OutputStream {
        if (!fileInfoUpdated) {
            update()
        }
        if (!fileExist) {
            create()
        }
        if (!fileExist) {
            throw RuntimeException("get output stream for MediaStoreFile(${folderName}/${relativePath.fixPath()}) failed may caused by create failed")
        }
        return context.contentResolver.openOutputStream(fileUri!!, if (append) "wa" else "w")!!
    }

    override fun delete(): Boolean {
        if (!fileInfoUpdated) {
            update()
        }
        if (fileUri == null) {
            return true
        }
        var result = true
        try {
            context.contentResolver.delete(fileUri!!, null, null)
        } catch (e: IOException) {
            e.printStackTrace()
            result = false
        }
        if (result) {
            fileExist = false
        }
        return result
    }
}