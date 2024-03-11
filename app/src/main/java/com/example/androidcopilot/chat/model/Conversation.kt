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
    @PrimaryKey
    val id: String,
    val title: String = "",
    val model: String = "",
    val prompt: String = "",
    val type: ConversationType = ConversationType.TypeDraft,
    val status: ConversationStatus = ConversationStatus.StatusEmpty,
    val firstMessageId: String? = null,
    val lastMessageId: String? = null,
    val messageCount: Int = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long = 0,
) : Serializable, Parcelable {

    enum class ConversationStatus {
        /**
         * indicate conversation is empty, no messages is sent or received
         */
        StatusEmpty,

        /**
         * indicate conversation is failure, last message is failure
         */
        StatusFailure,
        /**
         * indicate conversation is is send or receiving message, last message is running
         */
        StatusRunning,
        /**
         * indicate conversation is complete, last message is success
         */
        StatusComplete
    }

    enum class ConversationType {
        TypePersistent,
        TypeDraft,
    }

}

