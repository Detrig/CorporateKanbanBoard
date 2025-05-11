package github.detrig.corporatekanbanboard.presentation.boardMain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.databinding.RcViewTaskItemBinding
import github.detrig.corporatekanbanboard.domain.model.Priority
import github.detrig.corporatekanbanboard.domain.model.Task
import github.detrig.corporatekanbanboard.domain.model.TaskProgress
import java.util.Collections

class TasksRcViewAdapter(
    private val listener: OnTaskClickListener,
    private val onTaskMoved: (columnId: String, newTasksOrder: List<Task>) -> Unit
) : RecyclerView.Adapter<TasksRcViewAdapter.ViewHolder>() {

    val tasksList: ArrayList<Task> = arrayListOf()
    private var columnId: String = ""
    private var draggedItemPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewTaskItemBinding.bind(view)

        init {
            itemView.setOnLongClickListener {
                draggedItemPosition = adapterPosition
                true
            }
        }

        fun bind(task: Task, listener: OnTaskClickListener) = with(binding) {
            taskTitleTV.text = task.title

            when (task.taskProgress) {
                TaskProgress.IN_WORK -> {
                    when (task.priority) {
                        Priority.LOW_EMERGENCY -> taskContainer.setBackgroundResource(
                            R.color.light_green

                        )

                        Priority.MEDIUM_EMERGENCY -> taskContainer.setBackgroundResource(
                            R.color.light_orange
                        )

                        Priority.EMERGENCY -> taskContainer.setBackgroundResource(
                            R.color.light_red
                        )
                    }
                }

                TaskProgress.DONE ->
                    taskContainer.setBackgroundResource(R.color.medium_light_gray)

                TaskProgress.NEED_REVIEW -> {
                    taskContainer.setBackgroundResource(R.drawable.border)
                    taskTitleTV.setTextColor(itemView.context.getColor(R.color.black))
                }
            }


            itemView.setOnClickListener {
                listener.onClick(task)
            }
        }
    }


    fun update(newTasks: List<Task>, columnId: String) {
        this.columnId = columnId
        val diff = DiffUtil.calculateDiff(DiffUtilCallBack(tasksList, newTasks))
        tasksList.clear()
        tasksList.addAll(newTasks)
        diff.dispatchUpdatesTo(this)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(tasksList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        onTaskMoved(columnId, tasksList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rc_view_task_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = tasksList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasksList[position], listener)
    }

    class DiffUtilCallBack(
        private val old: List<Task>,
        private val new: List<Task>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem == newItem && oldItem.position == newItem.position
        }

    }

    interface OnTaskClickListener {
        fun onClick(task: Task)
    }
}