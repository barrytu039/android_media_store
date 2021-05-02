package com.barrytu.mediastoreretriever

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class MediaRetriever {

    private var photoRetriever : PhotoRetriever = PhotoRetriever(AppApplication.getAppContext().contentResolver)

    private var videoRetriever : VideoRetriever

    val mediaMutableLiveData : MutableLiveData<List<MediaEntity>> = MutableLiveData()

    init {
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