package com.dq.android.file

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

abstract class AndroidFile(val context: Context, private val path: String) {

    abstract fun exists(): Boolean
    abstract fun getName(): String?
    abstract fun getParent(): String?
    abstract fun getParentFile(): AndroidFile?
    abstract fun getPath(): String
    abstract fun isDirectory(): Boolean
    abstract fun isFile(): Boolean
    abstract fun length(): Long
    abstract fun list(): Array<String>?
    abstract fun listFiles(): Array<AndroidFile>?
    abstract fun mkdir(): Boolean
    abstract fun mkdirs(): Boolean
    abstract fun renameTo(dest: AndroidFile): Boolean
    abstract fun delete(): Boolean
    abstract fun createFile(): Boolean
    abstract fun getInputStream(): InputStream?
    abstract fun getOutputStream(): OutputStream?

    companion object {
        fun fromFile(context: Context, file: File): AndroidFile {
            return FileAndroidFile(context, file)
        }
    }
}

class FileAndroidFile(context: Context, private val file: File) : AndroidFile(context, file.path) {

    override fun exists(): Boolean = file.exists()

    override fun getName(): String? = file.name

    override fun getParent(): String? = file.parent

    override fun getParentFile(): AndroidFile? = file.parentFile?.let { FileAndroidFile(context, it) }

    override fun getPath(): String = file.path

    override fun isDirectory(): Boolean = file.isDirectory

    override fun isFile(): Boolean = file.isFile

    override fun length(): Long = file.length()

    override fun list(): Array<String>? = file.list()

    override fun listFiles(): Array<AndroidFile>? = file.listFiles()?.map { FileAndroidFile(context, it) }?.toTypedArray()

    override fun mkdir(): Boolean = file.mkdir()

    override fun mkdirs(): Boolean = file.mkdirs()

    override fun renameTo(dest: AndroidFile): Boolean {
        if (dest is FileAndroidFile) {
            return file.renameTo(dest.file)
        }
        return false
    }

    override fun delete(): Boolean = file.delete()

    override fun createFile(): Boolean = file.createNewFile()

    override fun getInputStream(): InputStream = FileInputStream(file)

    override fun getOutputStream(): OutputStream = FileOutputStream(file)
}
