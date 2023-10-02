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
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val model: String = "",
    val type: ConversationType = ConversationType.TypePersistent,
    val status: ConversationStatus = ConversationStatus.StatusUnlocked,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long = 0,

    val messageCount: Int = 0,
    val totalTokens: Int = 0,

    val memoryTokenLimit: Int = 3500,
    val memoryToken: Int = 0,
    val memoryMessageLimit: Int = Int.MAX_VALUE,

    val latestMessageId: Long = 0,
): Serializable, Parcelable {

    enum class ConversationStatus {
        StatusUnlocked,
        StatusLocked
    }

    enum class ConversationType {
        TypePersistent
    }

}

