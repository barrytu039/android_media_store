package com.barrytu.mediastoreretriever

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class MediaRetriever() {

    private var photoRetriever : PhotoRetriever

    private var videoRetriever : VideoRetriever

    val mediaMutableLiveData : MutableLiveData<List<MediaEntity>> = MutableLiveData()

    init {
        photoRetriever = PhotoRetriever(AppApplication.getAppContext().contentResolver)
        videoRetriever = VideoRetriever(AppApplication.getAppContext().contentResolver)
    }

    suspend fun scanMediaItem() = withContext(Dispatchers.IO) {
        val photoUris = async { photoRetriever.scanItem() }
        val videoUris = async { videoRetriever.scanItem() }
        val mediaUris = mutableListOf<MediaEntity>()
        mediaUris.addAll(photoUris.await())
        mediaUris.addAll(videoUris.await())
        mediaMutableLiveData.postValue(mediaUris)
    }

}