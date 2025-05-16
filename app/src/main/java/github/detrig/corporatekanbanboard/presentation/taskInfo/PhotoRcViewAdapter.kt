package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.databinding.RcViewItemCommentPhotosBinding

class PhotoRcViewAdapter(private val listener: OnPhotoClickListener) :
    RecyclerView.Adapter<PhotoRcViewAdapter.ViewHolder>() {

    val list: ArrayList<String> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewItemCommentPhotosBinding.bind(view)

        fun bind(photo: String, listener: OnPhotoClickListener) = with(binding) {

            val bitmap = ImageUtils.base64ToBitmap(photo)
            if (bitmap != null) {
                Glide.with(itemView.context)
                    .load(bitmap)
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .into(commentImage)

                itemView.setOnClickListener {
                    listener.onClick(photo)
                }
            }
        }
    }

    fun update(newList: ArrayList<String>) {
        val diffUtil = DiffUtilCallBack(list, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rc_view_item_comment_photos, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], listener)
    }

    class DiffUtilCallBack(
        private val old: List<String>,
        private val new: List<String>
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

    interface OnPhotoClickListener {
        fun onClick(photo: String)
    }
}