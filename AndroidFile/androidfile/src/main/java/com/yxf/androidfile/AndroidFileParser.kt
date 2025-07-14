package com.yxf.androidfile

interface AndroidFileParser<T: AndroidFile> {

    fun parse(description: String): T


    fun associatedClass(): Class<T>


}