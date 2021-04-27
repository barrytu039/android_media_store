package com.barrytu.mediastoreretriever

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

class PhotoRetriever(val contentResolver: ContentResolver) {

    fun scanItem() : MutableList<Uri> {

        val uriMutableList = mutableListOf<Uri>()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val columns = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_ADDED
        )

        val orderBy = "DATE_MODIFIED DESC"

        val where = MediaStore.Images.Media.DATE_ADDED + ">" + 0

        val cursor = contentResolver.query(uri, columns, where, null, orderBy)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id              = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                val folderId        = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                val folderName      = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val name            = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                val size            = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                val width           = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
                val height          = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
                val addedTime       = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000L
                val modifierTime    = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)) * 1000L
                val takenTime       = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
                val mimeType        = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                val orientation     = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION))
                val imageUri        = ContentUris.withAppendedId(uri, id)
                uriMutableList.add(imageUri)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return uriMutableList
    }

}