package github.detrig.corporatekanbanboard.presentation.globalchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.databinding.RcViewItemMessageOtherChatItemBinding
import github.detrig.corporatekanbanboard.databinding.RcViewItemMessageSelfGlobalChatItemBinding
import github.detrig.corporatekanbanboard.domain.model.chat.GlobalChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageRcViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list: ArrayList<GlobalChatMessage> = arrayListOf()

    companion object {
        private const val TYPE_SELF = 0
        private const val TYPE_OTHER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId == App.currentUserId) TYPE_SELF else TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SELF -> SelfViewHolder(
                inflater.inflate(R.layout.rc_view_item_message_self_global_chat_item, parent, false)
            )
            TYPE_OTHER -> OtherViewHolder(
                inflater.inflate(R.layout.rc_view_item_message_other_chat_item, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = list[position]
        when (holder) {
            is SelfViewHolder -> holder.bind(message)
            is OtherViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = list.size

    fun update(newList: ArrayList<GlobalChatMessage>) {
        val diffUtil = DiffUtilCallBack(list, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    // --- ViewHolders ---

    class SelfViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewItemMessageSelfGlobalChatItemBinding.bind(view)

        fun bind(msg: GlobalChatMessage) = with(binding) {
            messageTextView.text = msg.text
            timestampTextView.text = formatTimestamp(msg.timestamp)

            // Показ изображения, если оно есть
            if (msg.imageBase64.isNotBlank()) {
                messageImageView.visibility = View.VISIBLE
                val bitmap = ImageUtils.base64ToBitmap(msg.imageBase64)
                Glide.with(itemView.context)
                    .load(bitmap)
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .into(messageImageView)
            } else {
                messageImageView.visibility = View.GONE
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    class OtherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewItemMessageOtherChatItemBinding.bind(view)

        fun bind(msg: GlobalChatMessage) = with(binding) {
            senderNameTextView.text = msg.senderName
            messageTextView.text = msg.text
            timestampTextView.text = formatTimestamp(msg.timestamp)

            if (msg.imageBase64.isNotBlank()) {
                messageImageView.visibility = View.VISIBLE
                val bitmap = ImageUtils.base64ToBitmap(msg.imageBase64)
                Glide.with(itemView.context)
                    .load(bitmap)
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .into(messageImageView)
            } else {
                messageImageView.visibility = View.GONE
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    class DiffUtilCallBack(
        private val old: List<GlobalChatMessage>,
        private val new: List<GlobalChatMessage>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size
        override fun getNewListSize(): Int = new.size
        override fun areItemsTheSame(oldPos: Int, newPos: Int) = old[oldPos] == new[newPos]
        override fun areContentsTheSame(oldPos: Int, newPos: Int) = old[oldPos] == new[newPos]
    }
}