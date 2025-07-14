package com.yxf.androidfile.media

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.yxf.androidfile.AndroidFileParser
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.Q)
class MediaStoreFileParser: AndroidFileParser<MediaStoreFile> {


    override fun parse(description: String): MediaStoreFile {
        val jo = JSONObject(description)
        val relativePath = jo.getString("relative_path")
        val uriString = jo.getString("uri")
        val folderName = jo.getString("folder_name")
        val uri = Uri.parse(uriString)
        return MediaStoreFile(relativePath, folderName, uri)
    }

    override fun associatedClass(): Class<MediaStoreFile> {
        return MediaStoreFile::class.java
    }
}