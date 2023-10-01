package com.example.androidcopilot.chat.model

import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.androidcopilot.chat.model.Message.MessageType.Companion.MESSAGE_TYPE_ASSISTANT
import com.example.androidcopilot.chat.model.Message.MessageType.Companion.MESSAGE_TYPE_FUNCTION_CALL_REQUEST
import com.example.androidcopilot.chat.model.Message.MessageType.Companion.MESSAGE_TYPE_FUNCTION_CALL_RESPONSE
import com.example.androidcopilot.chat.model.Message.MessageType.Companion.MESSAGE_TYPE_HUMAN
import com.example.androidcopilot.chat.model.Message.MessageType.Companion.MESSAGE_TYPE_SYSTEM
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity
@Parcelize
@Keep
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parent: Long = 0,
    val child: Long = 0,
    val conversation: Long,
    @MessageType
    val type: Int,
    val content: String,
    val status: Status = Status.StatusRequesting,
    val token: Int = 0,
    val functionName: String? = null,
    val functionArgs: String? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "messageId"
    )
    val attachment: List<ChatAttachment> = emptyList(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createAt: Long = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updateAt: Long = 0,
) : Parcelable {


    @Keep
    sealed interface Status: Serializable, Parcelable {

        @Parcelize
        @Keep
        object StatusPending: Status

        @Parcelize
        @Keep
        object StatusSuccess: Status

        @Parcelize
        @Keep
        object StatusRequesting: Status

        @Parcelize
        @Keep
        object StatusReceiving: Status

        @Parcelize
        @Keep
        object StatusExecuteFunction: Status

        @Parcelize
        @Keep
        object StatusStopped: Status

        @Parcelize
        @Keep
        object StatusError: Status
    }

    @IntDef(MESSAGE_TYPE_HUMAN,
        MESSAGE_TYPE_ASSISTANT,
        MESSAGE_TYPE_SYSTEM,
        MESSAGE_TYPE_FUNCTION_CALL_REQUEST,
        MESSAGE_TYPE_FUNCTION_CALL_RESPONSE
    )
    annotation class MessageType {
        companion object {
            const val MESSAGE_TYPE_HUMAN = 1
            const val MESSAGE_TYPE_ASSISTANT = 2
            const val MESSAGE_TYPE_SYSTEM = 3
            const val MESSAGE_TYPE_FUNCTION_CALL_REQUEST = 4
            const val MESSAGE_TYPE_FUNCTION_CALL_RESPONSE = 5
        }
    }
}
