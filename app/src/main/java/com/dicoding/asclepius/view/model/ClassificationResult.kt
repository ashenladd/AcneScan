package com.dicoding.asclepius.view.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClassificationResult(
    val imageUri: Uri,
    val classifications: List<String>,
    val highest: Int,
) : Parcelable
