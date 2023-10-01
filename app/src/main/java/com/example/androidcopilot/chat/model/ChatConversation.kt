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
    val id: Long = 0,
    val title: String = "",
    val model: String = "",
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long = 0,
    val messageCount: Int = 0,
    val tokenCount: Long = 0,
    val tokenLimit: Int = 3500,
    val memoryOffset: Int = 0,
    val memoryLimit: Int = 0
): Serializable, Parcelable

