package com.slicky.gitcompare.core

import java.io.File
import java.util.*

/**
 * Created by SlickyPC on 25.5.2017
 */
class PropertyHandler(val path: String) {

    val properties = Properties().apply {
        load(File(path).inputStream())
    }

    operator fun get(key: String): String = properties.getProperty(key)
}