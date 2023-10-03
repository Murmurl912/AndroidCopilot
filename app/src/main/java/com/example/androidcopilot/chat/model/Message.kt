package com.example.androidcopilot.chat.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Conversation::class,
            parentColumns = ["id"],
            childColumns = ["conversation"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            "parent"
        ),
        Index("child"),
        Index("conversation")
    ]
)
@Parcelize
@Keep
data class Message(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(defaultValue = "NULL")
    val parent: Long? = null,
    @ColumnInfo(defaultValue = "NULL")
    val child: Long? = null,
    val conversation: Long,
    val type: MessageType,
    val content: String,
    val includeInMemory: Boolean = true,
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


fun Message.isError(): Boolean {
    return status == Message.MessageStatus.StatusError
}

fun Message.isCompleted(): Boolean {
    return status == Message.MessageStatus.StatusError || status == Message.MessageStatus.StatusStopped ||
            status == Message.MessageStatus.StatusSuccess
}