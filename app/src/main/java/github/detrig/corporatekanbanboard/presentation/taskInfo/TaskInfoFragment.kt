package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentTaskInfoBinding
import github.detrig.corporatekanbanboard.domain.model.Comment
import github.detrig.corporatekanbanboard.domain.model.Priority
import github.detrig.corporatekanbanboard.domain.model.Task
import github.detrig.corporatekanbanboard.domain.model.TaskProgress
import java.text.SimpleDateFormat
import java.util.Date

class TaskInfoFragment : AbstractFragment<FragmentTaskInfoBinding>() {

    private lateinit var viewModel: TaskInfoViewModel
    private lateinit var commentsRcViewAdapter: CommentsRcViewAdapter
    private lateinit var photosRcViewAdapter: PhotoRcViewAdapter

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskInfoBinding = FragmentTaskInfoBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ProvideViewModel).viewModel(TaskInfoViewModel::class.java)

        initRcView()

        binding.sendCommentButton.setOnClickListener {
            addComment()
        }

        viewModel.currentTaskLiveData().value?.let {
            initViews(it)
        }

        viewModel.currentTaskLiveData().observe(viewLifecycleOwner) {
            photosRcViewAdapter.update(ArrayList(it.photosBase64))
            commentsRcViewAdapter.update(it.comments)
        }
    }

    private fun addComment() {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        if (binding.commentEditText.text.isNotBlank()) {
            val comment = Comment(
                content = binding.commentEditText.text.toString(),
                dateCreated = currentDate
                )
            viewModel.addComment(comment)
        } else {
            Toast.makeText(requireContext(), "Поле комментария не может быть пустым", Toast.LENGTH_SHORT).show()
        }
        binding.commentEditText.setText("")
    }

    private fun initViews(task: Task) = with(binding) {
        taskTitle.text = task.title
        taskCreator.text = taskCreator.text.toString() + task.creator
        taskDate.text = taskDate.text.toString() + task.dateCreated
        taskDescription.text = task.description
        taskProgress.text = when(task.taskProgress) {
            TaskProgress.IN_WORK ->  taskProgress.text.toString() + "В работе"
            TaskProgress.NEED_REVIEW -> taskProgress.text.toString() + "Нуждается в проверке"
            TaskProgress.DONE -> taskProgress.text.toString() + "Выполнено"
        }
        taskPriority.text = when(task.priority) {
            Priority.LOW_EMERGENCY -> taskPriority.text.toString() + "Низкий"
            Priority.MEDIUM_EMERGENCY -> taskPriority.text.toString() + "Средний"
            Priority.HIGH_EMERGENCY -> taskPriority.text.toString() + "Высокий"
        }
        photosRcViewAdapter.update(ArrayList(task.photosBase64))
        commentsRcViewAdapter.update(ArrayList(task.comments))

        if (viewModel.currentTaskLiveData().value?.creator == App.currentUserEmail) {
            binding.deleteTaskButton.visibility = View.VISIBLE
        }

        binding.deleteTaskButton.setOnClickListener {
            showDeleteConfirmationDialog(task.id)
        }
    }

    private fun initRcView() {
        commentsRcViewAdapter = CommentsRcViewAdapter(object : CommentsRcViewAdapter.OnCommentClickListener {
            override fun onClick(comment: Comment) {

            }
        })
        binding.commentsRecyclerView.adapter = commentsRcViewAdapter

        photosRcViewAdapter = PhotoRcViewAdapter(object : PhotoRcViewAdapter.OnPhotoClickListener {
            override fun onClick(photo: String) {

            }
        })
        binding.photoRcView.adapter = photosRcViewAdapter
    }

    private fun showDeleteConfirmationDialog(taskId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление задачи")
            .setMessage("Вы уверены, что хотите удалить эту задачу?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteTask(taskId)
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

}