package com.barrytu.mediastoreretriever

import android.net.Uri

data class MediaEntity(
    var uri : Uri,
    var id : Long,
    var name : String,
    var mimeType : String,
    var modifyTime : Long,
    var addTime : Long
)