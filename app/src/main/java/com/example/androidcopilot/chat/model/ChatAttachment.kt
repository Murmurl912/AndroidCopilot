package com.example.androidcopilot.chat.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity
@Parcelize
@Keep
data class ChatAttachment(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: String,
    val title: String? = null,
    val name: String? = null,
    val messageId: Long = 0,
    val thumbnail: String? = null,
    val contentUrl: String? = null,
    val contentFile: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long
) : Parcelable, Serializable