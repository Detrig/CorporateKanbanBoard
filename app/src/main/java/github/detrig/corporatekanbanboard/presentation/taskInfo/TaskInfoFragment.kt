package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentTaskInfoBinding
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.Comment
import github.detrig.corporatekanbanboard.domain.model.Priority
import github.detrig.corporatekanbanboard.domain.model.Task
import github.detrig.corporatekanbanboard.domain.model.TaskProgress
import github.detrig.corporatekanbanboard.presentation.addtask.WorkersRcViewAdapter
import java.text.SimpleDateFormat
import java.util.Date

class TaskInfoFragment : AbstractFragment<FragmentTaskInfoBinding>() {

    private lateinit var viewModel: TaskInfoViewModel
    private lateinit var commentsRcViewAdapter: CommentsRcViewAdapter
    private lateinit var workersHorizontalRcViewAdapter: WorkersHorizontalRcViewAdapter
    private lateinit var workersRcViewAdapter: WorkersRcViewAdapter
    private lateinit var photosRcViewAdapter: PhotoRcViewAdapter
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedUsers = mutableSetOf<BoardMember>()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskInfoBinding = FragmentTaskInfoBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ProvideViewModel).viewModel(TaskInfoViewModel::class.java)

        selectedUsers = viewModel.currentTask().workers.toMutableSet()

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val base64 = ImageUtils.uriToBase64(requireContext(), it)
                    val updatePhotoList =
                        viewModel.currentTask().photosBase64.toMutableList().apply {
                            add(base64)
                        }
                    viewModel.updateTaskUniversal(
                        task = viewModel.currentTask().copy(photosBase64 = updatePhotoList)
                    )
                }
            }

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
        val commentText = binding.commentEditText.text.toString()
        if (commentText.isNotBlank()) {
            if (commentText.length <= 200) {
                val comment = Comment(
                    content = commentText.toString(),
                    dateCreated = currentDate
                )
                viewModel.addComment(comment)
                binding.commentEditText.setText("")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Максимальная длина комментария - 200 симмволов",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Поле комментария не может быть пустым",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initViews(task: Task) = with(binding) {
        taskTitle.text = task.title
        taskCreator.text = taskCreator.text.toString() + task.creator
        taskDate.text = taskDate.text.toString() + task.dateCreated
        taskDescription.text = task.description
        taskProgress.text = when (task.taskProgress) {
            TaskProgress.IN_WORK -> taskProgress.text.toString() + "В работе"
            TaskProgress.NEED_REVIEW -> taskProgress.text.toString() + "Нуждается в проверке"
            TaskProgress.DONE -> taskProgress.text.toString() + "Выполнено"
        }

        taskPriority.text = when (task.priority) {
            Priority.LOW_EMERGENCY -> taskPriority.text.toString() + "Низкий"
            Priority.MEDIUM_EMERGENCY -> taskPriority.text.toString() + "Средний"
            Priority.HIGH_EMERGENCY -> taskPriority.text.toString() + "Высокий"
        }

        if (task.creator == App.currentUserEmail || viewModel.getUserRoleForCurrentBoard() == BoardAccess.ADMIN) {
            binding.deleteTaskButton.visibility = View.VISIBLE
            binding.taskAcceptAfterReviewButton.visibility = View.VISIBLE
        }

        when (viewModel.getUserRoleForCurrentBoard()) {
            BoardAccess.VIEWER -> binding.addPhotoButton.visibility = View.GONE
            else -> binding.addPhotoButton.visibility = View.VISIBLE
        }

        photosRcViewAdapter.update(ArrayList(task.photosBase64))
        commentsRcViewAdapter.update(ArrayList(task.comments))
        workersHorizontalRcViewAdapter.update(ArrayList(task.workers))

        binding.deleteTaskButton.setOnClickListener {
            showDeleteConfirmationDialog(task.id)
        }

        //Task done button
        if (viewModel.getUserRoleForCurrentBoard() == BoardAccess.VIEWER) {
            binding.taskDoneButton.visibility = View.INVISIBLE
        }

        binding.taskDoneButton.setOnClickListener {
            val updatedTask = task.copy(taskProgress = TaskProgress.NEED_REVIEW)
            viewModel.updateTaskUniversal(updatedTask)
        }

        binding.taskAcceptAfterReviewButton.setOnClickListener {
            val updatedTask = task.copy(taskProgress = TaskProgress.DONE)
            viewModel.updateTaskUniversal(updatedTask)
        }

        binding.addPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        viewModel.currentTask().workers.forEach {
            if (it.user.id == App.currentUserId && (it.access == BoardAccess.LEADER || it.access == BoardAccess.ADMIN)) {
                binding.addWorkerButton.visibility = View.VISIBLE
            }
        }

        if (viewModel.currentTask().creator == App.currentUserEmail
            || viewModel.currentBoard().creatorId == App.currentUserId
            || viewModel.getUserRoleForCurrentBoard() == BoardAccess.LEADER
            || viewModel.getUserRoleForCurrentBoard() == BoardAccess.ADMIN
        )
            binding.addWorkerButton.visibility = View.VISIBLE


        binding.addWorkerButton.setOnClickListener {
            showUsersBottomSheet()
        }
    }

    private fun initRcView() {
        commentsRcViewAdapter =
            CommentsRcViewAdapter(object : CommentsRcViewAdapter.OnCommentClickListener {
                override fun onClick(comment: Comment) {

                }
            })
        binding.commentsRecyclerView.adapter = commentsRcViewAdapter

        photosRcViewAdapter = PhotoRcViewAdapter(object : PhotoRcViewAdapter.OnPhotoClickListener {
            override fun onClick(photo: String) {

            }
        })
        binding.photoRcView.adapter = photosRcViewAdapter

        workersHorizontalRcViewAdapter = WorkersHorizontalRcViewAdapter(object :
            WorkersHorizontalRcViewAdapter.OnWorkerClickListener {
            override fun onClick(worker: BoardMember) {

            }

            override fun onSelectWorker(worker: BoardMember) {
//                if (selectedUsers.contains(worker)) {
//                    selectedUsers.remove(worker)
//                } else {
//                    selectedUsers.add(worker)
//                }
            }
        })
        binding.workersRcView.adapter = workersHorizontalRcViewAdapter
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

    private fun showUsersBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_workers, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        val searchEditText = dialogView.findViewById<EditText>(R.id.searchUserEditText)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.usersRecyclerView)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)

        // Полный список пользователей (для фильтрации)
        val allUsers = BoardMember.sortedByRole(viewModel.currentBoard().members)
        Log.d("alz-04", "allUsers: ${allUsers.map { it.user.email }}")
        workersRcViewAdapter =
            WorkersRcViewAdapter(object : WorkersRcViewAdapter.OnWorkerClickListener {
                override fun onClick(worker: BoardMember) {

                }

                override fun onSelectWorker(worker: BoardMember) {
                    if (selectedUsers.contains(worker)) {
                        selectedUsers.remove(worker)
                    } else {
                        selectedUsers.add(worker)
                    }
                }
            })
        workersRcViewAdapter.selectedWorkersList = ArrayList(selectedUsers)
        recyclerView.adapter = workersRcViewAdapter
        workersRcViewAdapter.update(ArrayList(allUsers))

        searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()
            val filtered = allUsers.filter {
                it.user.name.lowercase().contains(query) || it.user.email.lowercase()
                    .contains(query)
            }
            workersRcViewAdapter.update(ArrayList(filtered))
        }

        confirmButton.setOnClickListener {
            workersHorizontalRcViewAdapter.update(ArrayList(selectedUsers))
            val newTask = viewModel.currentTask().copy(workers = selectedUsers.toList())
            viewModel.updateTaskUniversal(newTask)
            dialog.dismiss()
        }

        dialog.show()
    }
}