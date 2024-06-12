package com.dicoding.asclepius.data.local.entity

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity class ClassificationHistory(
    @PrimaryKey(autoGenerate = false)
    val imageUri: String,
    val classifications: String,
    val timestamp: String
)
