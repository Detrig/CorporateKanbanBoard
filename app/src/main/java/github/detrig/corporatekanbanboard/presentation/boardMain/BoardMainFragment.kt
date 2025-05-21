package github.detrig.corporatekanbanboard.presentation.boardMain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentBoardMainBinding
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.Column
import github.detrig.corporatekanbanboard.domain.model.Task

class BoardMainFragment : AbstractFragment<FragmentBoardMainBinding>() {

    private lateinit var viewModel: BoardMainViewModel
    private lateinit var columnWithTasksRcViewAdapter: ColumnWithTasksRcViewAdapter

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardMainBinding = FragmentBoardMainBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ProvideViewModel).viewModel(BoardMainViewModel::class.java)
        val currentUserRoleForThisBoard = viewModel.getUserRoleForCurrentBoard()

        initViews(currentUserRoleForThisBoard)
        initRcView(currentUserRoleForThisBoard)

        viewModel.currentBoard().value?.let {
            columnWithTasksRcViewAdapter.update(ArrayList(it.columns))
            binding.boardTitleTextView.text = it.title
        }

        viewModel.currentBoard().observe(viewLifecycleOwner) {
            // Log.d("alz04LiveData", "currentBoardTasks updated: ${it.columns[0].tasks}")
            columnWithTasksRcViewAdapter.update(ArrayList(it.columns))
            binding.boardTitleTextView.text = it.title
        }

        binding.settingsButton.setOnClickListener {
            viewModel.boardSettingsScreen()
        }
    }

    private fun initRcView(currentUserRoleForThisBoard : BoardAccess) {
        columnWithTasksRcViewAdapter =
            ColumnWithTasksRcViewAdapter(object :
                ColumnWithTasksRcViewAdapter.OnAddTaskButtonClickListener {
                override fun clickAddTaskButton(column: Column) {
                    viewModel.addTaskScreen(column)
                }
            }, object : TasksRcViewAdapter.OnTaskClickListener {
                override fun onClick(task: Task) {
                    viewModel.clickedTaskScreen(task)
                }
            }, onTaskMoved = { columnId, tasksList ->
                viewModel.updateTasks(columnId, tasksList)
            }, currentUserRoleForThisBoard)
        binding.columnsRcView.adapter = columnWithTasksRcViewAdapter
    }

    private fun initViews(currentUserRoleForThisBoard : BoardAccess) {
        when (currentUserRoleForThisBoard) {
            BoardAccess.ADMIN -> binding.settingsButton.visibility = View.VISIBLE
            BoardAccess.LEADER -> binding.settingsButton.visibility = View.VISIBLE
            else -> binding.settingsButton.visibility = View.GONE
        }
    }
}