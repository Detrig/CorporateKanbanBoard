package github.detrig.corporatekanbanboard.presentation.addBoard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.databinding.RcViewColumnItemBinding
import github.detrig.corporatekanbanboard.domain.model.Column

class ColumnsRcViewAdapter(
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<ColumnsRcViewAdapter.ViewHolder>() {

    val list: ArrayList<Column> = arrayListOf()

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = RcViewColumnItemBinding.bind(itemView)

        fun bind(column: Column) = with(binding) {
            columnNameEditText.setText(column.title)

            removeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveClick(position)
                }
            }

            columnNameEditText.doAfterTextChanged { text ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && text != null) {
                    list[position].title = text.toString()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rc_view_column_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun addItem(column: Column) {
        list.add(column)
        notifyItemInserted(list.size - 1)
    }

    fun removeItem(position: Int) {
        if (position in list.indices) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun moveItem(from: Int, to: Int) {
        if (from in list.indices && to in list.indices) {
            val movedItem = list.removeAt(from)
            list.add(to, movedItem)
            notifyItemMoved(from, to)
        }
    }

    fun update(newList: ArrayList<Column>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}