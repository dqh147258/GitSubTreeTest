package com.yxf.androidfilesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yxf.androidfile.AndroidFileFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //testWritePictureFile()
        //testWriteDCIMFile()
        testWriteDownloadFile()
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