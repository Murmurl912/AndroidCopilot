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
    val prompt: String = "",
    val type: ConversationType = ConversationType.TypeDraft,
    val status: ConversationStatus = ConversationStatus.StatusUnlocked,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long = 0,

    /**
     * Total message count in this conversation, auto updated by sql trigger
     */
    val messageCount: Int = 0,
    val totalTokens: Int = 0,

    val contextTokenSizeLimit: Int = 3500,
    val contextTokenSize: Int = 0,
    val contextMessageOffset: Int = Int.MAX_VALUE,

    /**
     * Last Message in this conversation, Auto updated by sql trigger
     */
    val latestMessageId: Long = 0,
): Serializable, Parcelable {

    enum class ConversationStatus {
        StatusUnlocked,
        StatusLocked
    }

    enum class ConversationType {
        TypePersistent,
        TypeDraft,
    }

}

