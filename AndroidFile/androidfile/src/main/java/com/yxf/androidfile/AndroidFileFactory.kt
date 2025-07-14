package com.yxf.androidfile

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.yxf.androidfile.java.JavaFile
import com.yxf.androidfile.java.JavaFileParser
import com.yxf.androidfile.media.MediaStoreFile
import com.yxf.androidfile.media.MediaStoreFileParser
import java.lang.RuntimeException

object AndroidFileFactory {

    private val parserMap = HashMap<Class<*>, AndroidFileParser<*>>()

    init {
        addParser(JavaFileParser())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addParser(MediaStoreFileParser())
        }
    }


    fun addParser(parser: AndroidFileParser<*>) {
        parserMap[parser.associatedClass()] = parser
    }


    /**
     * If you want to use Android file in onCreate of application you should call the method.
     */
    fun initContext(context: Context) {
        AndroidFileProvider.applicationContext = context.applicationContext
    }

    fun create(clazz: Class<*>, description: String): AndroidFile {
        val parser = parserMap[clazz] ?: throw RuntimeException("not support class: $clazz")
        return parser.parse(description)
    }


    fun createFileByPath(path: String): AndroidFile {
        return create(JavaFile::class.java, path)
    }

    /**
     * Create external file
     */
    fun createExternalFile(relativePath: String): AndroidFile {
        var externalPath = AndroidFileProvider.applicationContext.getExternalFilesDir(null)!!.absolutePath
        if (!externalPath.endsWith("/")) {
            externalPath += "/"
        }
        var path = externalPath + relativePath
        return createFileByPath(path)
    }


    fun createEnvironmentFile(relativePath: String, directory: String, uri: Uri?): AndroidFile {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStoreFile(relativePath, directory, uri ?: MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL))
        } else {
            var downloadPath = Environment.getExternalStoragePublicDirectory(directory).absolutePath
            if (!downloadPath.endsWith("/")) {
                downloadPath += "/"
            }
            var path = downloadPath + relativePath
            return createFileByPath(path)
        }
    }

    /**
     * Create download file
     */
    fun createDownloadFile(relativePath: String): AndroidFile {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Downloads.EXTERNAL_CONTENT_URI else null
        return createEnvironmentFile(relativePath, Environment.DIRECTORY_DOWNLOADS, uri)
    }

    /**
     * Create picture file
     */
    fun createPictureFile(relativePath: String): AndroidFile {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else null
        return createEnvironmentFile(relativePath, Environment.DIRECTORY_PICTURES, uri)
    }


    /**
     * Create DCIM file
     */
    fun createDCIMFile(relativePath: String): AndroidFile {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else null
        return createEnvironmentFile(relativePath, Environment.DIRECTORY_DCIM, uri)
    }


}

