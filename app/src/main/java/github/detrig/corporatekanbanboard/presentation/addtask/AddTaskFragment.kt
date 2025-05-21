package github.detrig.corporatekanbanboard.presentation.addtask

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentAddTaskBinding
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.Priority
import github.detrig.corporatekanbanboard.domain.model.Task
import androidx.core.widget.addTextChangedListener

class AddTaskFragment : AbstractFragment<FragmentAddTaskBinding>() {

    private lateinit var viewModel: AddTaskViewModel
    private lateinit var workersRcViewAdapter : WorkersRcViewAdapter
    private val selectedUsers = mutableSetOf<BoardMember>()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddTaskBinding = FragmentAddTaskBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(AddTaskViewModel::class.java)

        binding.workersEditText.setOnClickListener {
            showUsersBottomSheet()
        }

        binding.saveButton.setOnClickListener {
            if (binding.titleEditText.text.toString().isNotBlank()) {
                val priority = when (binding.priorityRadioGroup.checkedRadioButtonId) {
                    R.id.lowPriorityRadio -> Priority.LOW_EMERGENCY
                    R.id.mediumPriorityRadio -> Priority.MEDIUM_EMERGENCY
                    R.id.highPriorityRadio -> Priority.HIGH_EMERGENCY
                    else -> Priority.LOW_EMERGENCY
                }
                val task = Task(
                    title = binding.titleEditText.text.toString(),
                    description = binding.descriptionEditText.text.toString(),
                    priority = priority,
                    workers = selectedUsers.toList()
                )
                viewModel.addTasks(task)
            }
            else {
                Toast.makeText(requireContext(), "Заполните все необходимые поля", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showUsersBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_workers, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        val searchEditText = dialogView.findViewById<EditText>(R.id.searchUserEditText)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.usersRecyclerView)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)

        val allUsers = BoardMember.sortedByRole(viewModel.getCurrentBoard().members)
        workersRcViewAdapter = WorkersRcViewAdapter(object : WorkersRcViewAdapter.OnWorkerClickListener {
            override fun onClick(worker: BoardMember) {
                // например, показать детали
            }

            override fun onSelectWorker(worker: BoardMember) {
                if (selectedUsers.contains(worker)) {
                    selectedUsers.remove(worker)
                } else {
                    selectedUsers.add(worker)
                }
            }
        }, viewModel.getUsers())
        workersRcViewAdapter.selectedWorkersList = ArrayList(selectedUsers)
        recyclerView.adapter = workersRcViewAdapter
        workersRcViewAdapter.update(ArrayList(allUsers))

        searchEditText.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()
            val filtered = allUsers.filter {
                it.name.lowercase().contains(query) || it.email.lowercase().contains(query)
            }
            workersRcViewAdapter.update(ArrayList(filtered))
        }

        confirmButton.setOnClickListener {
            val names = selectedUsers.joinToString("\n") {
                if (it.name.isNotBlank()) it.name else it.email
            }
            binding.workersEditText.setText(names)
            dialog.dismiss()
        }

        dialog.show()
    }
}