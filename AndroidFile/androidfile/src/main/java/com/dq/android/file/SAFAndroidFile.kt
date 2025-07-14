package com.dq.android.file

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

class SAFAndroidFile(context: Context, uri: Uri) : AndroidFile(context, uri.toString()) {

    private val documentFile: DocumentFile? = DocumentFile.fromSingleUri(context, uri)

    override fun exists(): Boolean {
        return documentFile?.exists() ?: false
    }

    override fun getName(): String? {
        return documentFile?.name
    }

    override fun getParent(): String? {
        return documentFile?.parentFile?.uri?.toString()
    }

    override fun getParentFile(): AndroidFile? {
        return documentFile?.parentFile?.let { SAFAndroidFile(context, it.uri) }
    }

    override fun getPath(): String {
        return uri.toString()
    }

    override fun isDirectory(): Boolean {
        return documentFile?.isDirectory ?: false
    }

    override fun isFile(): Boolean {
        return documentFile?.isFile ?: false
    }

    override fun length(): Long {
        return documentFile?.length() ?: 0
    }

    override fun list(): Array<String>? {
        return documentFile?.listFiles()?.map { it.name ?: "" }?.toTypedArray()
    }

    override fun listFiles(): Array<AndroidFile>? {
        return documentFile?.listFiles()?.map { SAFAndroidFile(context, it.uri) }?.toTypedArray()
    }

    override fun mkdir(): Boolean {
        // Not directly supported, need to create via parent
        return false
    }

    override fun mkdirs(): Boolean {
        // Not directly supported, need to create via parent
        return false
    }

    override fun renameTo(dest: AndroidFile): Boolean {
        if (dest is SAFAndroidFile) {
            return documentFile?.renameTo(dest.name ?: "") ?: false
        }
        return false
    }

    override fun delete(): Boolean {
        return documentFile?.delete() ?: false
    }

    override fun createFile(): Boolean {
        // Not directly supported, need to create via parent
        return false
    }

    override fun getInputStream() = context.contentResolver.openInputStream(Uri.parse(path))

    override fun getOutputStream() = context.contentResolver.openOutputStream(Uri.parse(path))
}
