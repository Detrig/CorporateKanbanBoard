package github.detrig.corporatekanbanboard.presentation.boardMain

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.databinding.RcViewColumnPresentationItemBinding
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.Column
import github.detrig.corporatekanbanboard.domain.model.Task

class ColumnWithTasksRcViewAdapter(
    private val addTaskButtonClickListener: OnAddTaskButtonClickListener,
    private val onColumnClickListener: OnColumnsClickListener,
    private val taskClickListener: TasksRcViewAdapter.OnTaskClickListener,
    private val onTaskMoved: (columnId: String, newTasksOrder: List<Task>) -> Unit,
    private val currentUserRole: BoardAccess
) : RecyclerView.Adapter<ColumnWithTasksRcViewAdapter.ViewHolder>() {

    val columnsList: ArrayList<Column> = arrayListOf()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewColumnPresentationItemBinding.bind(view)
        private val tasksRcViewAdapter = TasksRcViewAdapter(taskClickListener, onTaskMoved = { columnId, newTasks ->
            onTaskMoved(columnId, newTasks)
        })

        init {
            binding.columnRcView.apply {
                adapter = tasksRcViewAdapter
                //setupDragAndDrop()
            }
        }

//        private fun RecyclerView.setupDragAndDrop() {
//            val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
//                override fun getMovementFlags(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder
//                ): Int {
//                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//                    return makeMovementFlags(dragFlags, 0)
//                }
//
//                override fun onMove(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    target: RecyclerView.ViewHolder
//                ): Boolean {
//                    val fromPosition = viewHolder.adapterPosition
//                    val toPosition = target.adapterPosition
//                    (adapter as? TasksRcViewAdapter)?.moveItem(fromPosition, toPosition)
//                    tasksList =
//                    return true
//                }
//
//                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//                    super.onSelectedChanged(viewHolder, actionState)
//                    when (actionState) {
//                        ItemTouchHelper.ACTION_STATE_DRAG -> {
//                            viewHolder?.itemView?.alpha = 0.7f
//                            viewHolder?.itemView?.setBackgroundColor(
//                                ContextCompat.getColor(context, R.color.drag_background)
//                            )
//                        }
//                    }
//                }
//
//                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//                    super.clearView(recyclerView, viewHolder)
//                    viewHolder.itemView.alpha = 1.0f
//                    viewHolder.itemView.background = null
//                }
//
//                override fun isLongPressDragEnabled(): Boolean = true
//
//                override fun onSwiped(
//                    viewHolder: RecyclerView.ViewHolder,
//                    direction: Int
//                ) {
//                }
//            })
//            touchHelper.attachToRecyclerView(this)
//        }

        fun bind(column: Column) = with(binding) {
            columnsTitle.text = column.title
            tasksRcViewAdapter.update(column.tasks, column.id)
            Log.d("alzZ", "currentUserRole: $currentUserRole")
            when (currentUserRole) {
                BoardAccess.VIEWER -> binding.addTaskButton.visibility = View.GONE
                else -> binding.addTaskButton.visibility = View.VISIBLE
            }
            binding.addTaskButton.setOnClickListener {
                addTaskButtonClickListener.clickAddTaskButton(column)
            }

            when(currentUserRole) {
                BoardAccess.VIEWER -> binding.columnsTitle.isClickable = false
                BoardAccess.WORKER -> binding.columnsTitle.isClickable = false
                else -> binding.addTaskButton.visibility = View.VISIBLE
            }

            binding.columnsTitle.setOnClickListener {
                if (currentUserRole != BoardAccess.VIEWER && currentUserRole != BoardAccess.WORKER) {
                    onColumnClickListener.clickColumn(column)
                }
            }
        }
    }

    fun update(newList: ArrayList<Column>) {
        val diffUtil = DiffUtilCallBack(columnsList, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        columnsList.clear()
        columnsList.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rc_view_column_presentation_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = columnsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(columnsList[position])
    }

    class DiffUtilCallBack(
        private val old: List<Column>,
        private val new: List<Column>
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

    interface OnAddTaskButtonClickListener {
        fun clickAddTaskButton(column: Column)
    }

    interface OnColumnsClickListener {
        fun clickColumn(column: Column)
    }
}
