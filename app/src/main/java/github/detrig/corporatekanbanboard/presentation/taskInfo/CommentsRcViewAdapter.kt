package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.databinding.RcViewItemCommentBinding
import github.detrig.corporatekanbanboard.domain.model.Comment

class CommentsRcViewAdapter(private val listener : OnCommentClickListener) : RecyclerView.Adapter<CommentsRcViewAdapter.ViewHolder>() {

    val list : ArrayList<Comment> = arrayListOf()

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewItemCommentBinding.bind(view)

        fun bind(comment: Comment, listener : OnCommentClickListener) = with(binding) {

            binding.commentContentTextView.text = comment.content
            binding.dateTextView.text = comment.dateCreated
            binding.authorNameTextView.text = comment.authorName

            itemView.setOnClickListener {
                listener.onClick(comment)
            }
        }
    }

    fun update(newList : ArrayList<Comment>) {
        val diffUtil = DiffUtilCallBack(list, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        Log.d("rcView", "comments from rcViewUpdate: $newList")
        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], listener)
    }

    class DiffUtilCallBack(
        private val old : List<Comment>,
        private val new : List<Comment>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem == newItem
        }

    }

    interface OnCommentClickListener {
        fun onClick(comment: Comment)
    }
}