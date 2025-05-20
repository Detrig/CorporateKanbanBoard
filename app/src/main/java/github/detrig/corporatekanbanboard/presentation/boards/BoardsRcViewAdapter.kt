package github.detrig.corporatekanbanboard.presentation.boards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.databinding.RcViewBoardItemBinding
import github.detrig.corporatekanbanboard.domain.model.Board

class BoardsRcViewAdapter(private val listener: OnBoardClickListener) :
    RecyclerView.Adapter<BoardsRcViewAdapter.ViewHolder>() {

    val list: ArrayList<Board> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewBoardItemBinding.bind(view)

        fun bind(board: Board, listener: OnBoardClickListener) = with(binding) {
            boardTitle.text = board.title
            boardCreator.text = "${binding.boardCreator.text}${board.creatorEmail}"
            boardDescription.text = board.description
            boardDateCreated.text = board.dateCreated
            if (board.photoBase64.isNotEmpty()) {
                val bitmap = ImageUtils.base64ToBitmap(board.photoBase64)
                if (bitmap != null) {
                    Glide.with(boardImage.context)
                        .load(bitmap)
                        .centerCrop()
                        .placeholder(R.drawable.user)
                        .into(boardImage)
                } else {
                    boardImage.setImageResource(R.drawable.kanbanlogo)
                }
            } else {
                boardImage.setImageResource(R.drawable.kanbanlogo)
            }

            itemView.setOnClickListener {
                listener.onClick(board)
            }
        }
    }

    fun update(newList: ArrayList<Board>) {
        val diffUtil = DiffUtilCallBack(list, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rc_view_board_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], listener)
    }

    class DiffUtilCallBack(
        private val old: List<Board>,
        private val new: List<Board>
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

    interface OnBoardClickListener {
        fun onClick(board: Board)
    }
}