package com.yxf.androidfile.java

import com.yxf.androidfile.AndroidFileParser

class JavaFileParser() : AndroidFileParser<JavaFile>{
    override fun parse(description: String): JavaFile {
        return JavaFile(description)
    }

    override fun associatedClass(): Class<JavaFile> {
        return JavaFile::class.java
    }
}