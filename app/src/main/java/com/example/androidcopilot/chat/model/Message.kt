package com.example.androidcopilot.chat.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@Keep
data class Message(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val parent: Long = 0,
    val child: Long = 0,
    val conversation: Long,
    val type: MessageType,
    val content: String,
    val status: MessageStatus = MessageStatus.StatusRequesting,
    val token: Int = 0,
    val functionName: String? = null,
    val functionArgs: String? = null,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long = 0,
) : Parcelable {

    enum class MessageStatus {
        StatusPending,

        StatusSuccess,

        StatusRequesting,

        StatusReceiving,

        StatusStopped,

        StatusError,
    }

    enum class MessageType {
        TypeAssistant,
        TypeFunctionCallRequest,
        TypeFunctionCallResponse,
        TypeHuman,
        TypeSystem,
    }

}
