package com.barrytu.mediastoreretriever

import android.content.ContentResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaRetriever() {

    private var photoRetriever : PhotoRetriever

    private var videoRetriever : VideoRetriever

    init {
        photoRetriever = PhotoRetriever(AppApplication.getAppContext().contentResolver)
        videoRetriever = VideoRetriever(AppApplication.getAppContext().contentResolver)
    }

    suspend fun scanMediaItem() = withContext(Dispatchers.IO) {
        launch{
            photoRetriever.scanItem()
        }
        launch{
            videoRetriever.scanItem()
        }
    }

}