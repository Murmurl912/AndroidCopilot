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
data class ChatConversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String = "",
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long,
    val messageCount: Long,
    val tokenCount: Long,
): Serializable, Parcelable

