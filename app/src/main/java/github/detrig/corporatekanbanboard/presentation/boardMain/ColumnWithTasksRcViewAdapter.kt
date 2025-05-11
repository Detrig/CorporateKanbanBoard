package github.detrig.corporatekanbanboard.presentation.boardMain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.databinding.RcViewColumnPresentationItemBinding
import github.detrig.corporatekanbanboard.domain.model.Column

class ColumnWithTasksRcViewAdapter : RecyclerView.Adapter<ColumnWithTasksRcViewAdapter.ViewHolder>() {

    val list : ArrayList<Column> = arrayListOf()

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewColumnPresentationItemBinding.bind(view)

        fun bind(column: Column) = with(binding) {
            columnsTitle.text = column.title

        }
    }

    fun update(newList : ArrayList<Column>) {
        val diffUtil = DiffUtilCallBack(list, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_view_column_presentation_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class DiffUtilCallBack(
        private val old : List<Column>,
        private val new : List<Column>
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
}