package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FeedDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "feeds.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Feeds"
        const val COLUMN_ID = "id"
        const val COLUMN_IMAGE_URL = "image"
        const val COLUMN_HEADING = "heading"
        const val COLUMN_DESC = "description"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_IMAGE_URL TEXT, " +
                "$COLUMN_HEADING TEXT, " +
                "$COLUMN_DESC TEXT)")
        db.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    // Insert data into the database
    fun insertFeed(imageUrl: String, heading: String, description: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IMAGE_URL, imageUrl)
            put(COLUMN_HEADING, heading)
            put(COLUMN_DESC, description)
        }
        return db.insert(TABLE_NAME, null, values)
    }
    // Fetch all feeds from the database
    fun getAllFeeds(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
}
