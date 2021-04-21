package com.barrytu.mediastoreretriever

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log

class VideoRetriever(val contentResolver: ContentResolver) {

    fun scanItem() {

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val columns = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DATE_ADDED
        )

        val orderBy = "DATE_MODIFIED DESC"
        val where = MediaStore.Video.Media.DATE_ADDED + ">" + 0
        val cursor = contentResolver.query(uri, columns, where, null, orderBy)
        Log.e("uri::", "scan")
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id              = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val folderId        = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                val folderName      = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val name            = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                val size            = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val width           = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH))
                val height          = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT))
                val addedTime       = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)) * 1000L
                val modifierTime    = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)) * 1000L
                val takenTime       = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN))
                val mimeType        = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE))
                val imageUri        = ContentUris.withAppendedId(uri, id)
                Log.e("uri::", imageUri.toString())
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

}