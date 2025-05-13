package github.detrig.corporatekanbanboard.presentation.addtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentAddTaskBinding
import github.detrig.corporatekanbanboard.domain.model.Priority
import github.detrig.corporatekanbanboard.domain.model.Task

class AddTaskFragment : AbstractFragment<FragmentAddTaskBinding>() {

    private lateinit var viewModel: AddTaskViewModel
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddTaskBinding = FragmentAddTaskBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(AddTaskViewModel::class.java)

        binding.saveButton.setOnClickListener {
            val priority = when (binding.priorityRadioGroup.checkedRadioButtonId) {
                R.id.lowPriorityRadio -> Priority.LOW_EMERGENCY
                R.id.mediumPriorityRadio -> Priority.MEDIUM_EMERGENCY
                R.id.highPriorityRadio -> Priority.HIGH_EMERGENCY
                else -> Priority.LOW_EMERGENCY
            }
            val task = Task(
                title = binding.titleEditText.text.toString(),
                description = binding.descriptionEditText.text.toString(),
                priority = priority
            )
            viewModel.addTasks(task)
        }
    }
}