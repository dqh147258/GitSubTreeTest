package com.yxf.androidfilesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dq.android.file.SAFAndroidFile
import com.yxf.androidfile.AndroidFileFactory

class MainActivity : AppCompatActivity() {

    private val CREATE_FILE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //testWritePictureFile()
        //testWriteDCIMFile()
        //testWriteDownloadFile()
        testSAF()
    }

    private fun testSAF() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "test.txt")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                val safFile = SAFAndroidFile(this, uri)
                Log.d("SAF", "File created: ${safFile.getPath()}")

                safFile.getOutputStream()?.use { outputStream ->
                    outputStream.bufferedWriter().use {
                        it.write("Hello from SAF!")
                    }
                }

                safFile.getInputStream()?.use { inputStream ->
                    val content = inputStream.bufferedReader().readText()
                    Log.d("SAF", "File content: $content")
                }
            }
        }
    }

    private fun testWriteDownloadFile() {
        val file = AndroidFileFactory.createDownloadFile("test/test.txt")
        file.requestAccessPermission(this) { result ->
            if (result) {
                Log.d("Debug", "get dcim file successfully")
                val out = getOutputStream()
                out.bufferedWriter().use {
                    it.write("Hello world!")
                }
            }
        }
    }

    private fun testWriteDCIMFile() {
        val file = AndroidFileFactory.createDCIMFile("test/test.png")
        file.requestAccessPermission(this) { result ->
            if (result) {
                Log.d("Debug", "get dcim file successfully")
                val out = getOutputStream()
                out.bufferedWriter().use {
                    it.write("Hello world!")
                }
            }
        }
    }

    private fun testWritePictureFile() {
        val file = AndroidFileFactory.createPictureFile("test/test.png")
        file.requestAccessPermission(this) { result ->
            if (result) {
                Log.d("Debug", "get picture file successfully")
                val out = getOutputStream()
                out.bufferedWriter().use {
                    it.write("Hello world!")
                }
            }
        }
    }
}