package com.ashehata.cat_task1.utility

import android.content.Context
import android.os.Environment
import java.io.File

fun createBackup(context: Context?, databaseName: String) {

    // get database absoluate path as file
    val inputFile = context?.getDatabasePath(databaseName)?.absoluteFile

    // create a temp file

    val outputFile = File.createTempFile(databaseName, null, context?.cacheDir)
    outputFile.mkdir()
    /*
    val outputFile = File(Environment.getDownloadCacheDirectory(), "cat cat");
    if (!outputFile.exists()) {
        outputFile.mkdirs();
    }
    val test = File(outputFile, databaseName)

     */

    // copy
    inputFile?.copyTo(outputFile, true)
}

fun restoreBackup(context: Context, databaseName: String) {
    // find the exported database file path
    val inputFile = context.cacheDir

    // get database absoluate path as file
    val outputFile = context.getDatabasePath(databaseName).absoluteFile

//    outputFile?.let { inputFile.copyTo(it, true) }
    inputFile.copyTo(outputFile, true)
    inputFile.delete()
}

