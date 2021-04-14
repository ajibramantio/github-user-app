package com.dicoding.mygithubuserapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.dicoding.mygithubuserapp.db.DatabaseContract.AUTHORITY
import com.dicoding.mygithubuserapp.db.DatabaseContract.FavoriteColumns.Companion.TABLE_NAME
import com.dicoding.mygithubuserapp.helper.FavoriteHelper

class FavoriteProvider : ContentProvider() {
    companion object {
        private const val FAVORITE = 1
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favHelper: FavoriteHelper
        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAVORITE)
        }
    }

    override fun onCreate(): Boolean {
        favHelper = FavoriteHelper.getInstance(context as Context)
        favHelper.open()
        return true

    }
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return when (sUriMatcher.match(uri)) {
            FAVORITE -> favHelper.queryAll()
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }
}