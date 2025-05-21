package github.detrig.corporatekanbanboard.presentation.columnInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentColumnInfoBinding
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.presentation.taskInfo.WorkersHorizontalRcViewAdapter

class ColumnInfoFragment : AbstractFragment<FragmentColumnInfoBinding>() {

    private lateinit var viewModel: ColumnInfoViewModel
    private lateinit var responsibilityPersonsRcViewAdapter: WorkersHorizontalRcViewAdapter
    private lateinit var workersRcViewAdapter: WorkersHorizontalRcViewAdapter
    private var selectedResponsibilityPersons = mutableSetOf<BoardMember>()
    private var selectedWorkers = mutableSetOf<BoardMember>()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentColumnInfoBinding = FragmentColumnInfoBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(ColumnInfoViewModel::class.java)
        selectedWorkers = viewModel.currentColumn().workers.toMutableSet()
        selectedResponsibilityPersons = viewModel.currentColumn().responsiblePerson.toMutableSet()

        initRcView()
        initViews()

        viewModel.currentColumnLiveData().observe(viewLifecycleOwner) {
            responsibilityPersonsRcViewAdapter.update(ArrayList(it.responsiblePerson))
            workersRcViewAdapter.update(ArrayList(it.workers))
        }
    }

    private fun initRcView() {
        responsibilityPersonsRcViewAdapter = WorkersHorizontalRcViewAdapter(object :
            WorkersHorizontalRcViewAdapter.OnWorkerClickListener {
            override fun onClick(member: BoardMember) {

            }
        }, viewModel.getUsers())
        binding.responsibleRcView.adapter = responsibilityPersonsRcViewAdapter

        workersRcViewAdapter = WorkersHorizontalRcViewAdapter(object :
            WorkersHorizontalRcViewAdapter.OnWorkerClickListener {
            override fun onClick(member: BoardMember) {

            }
        }, viewModel.getUsers())
        binding.workersRcView.adapter = workersRcViewAdapter
    }

    private fun initViews() {
        val currentColumn = viewModel.currentColumn()
        val currentBoard = viewModel.currentBoard()

        binding.columnTitleEditText.setText(currentColumn.title)
        responsibilityPersonsRcViewAdapter.update(ArrayList(currentColumn.responsiblePerson))
        workersRcViewAdapter.update(ArrayList(currentColumn.workers))


        binding.responsibleSearchEditText.setOnClickListener {
            val searchResponsibilityUsers = UserSelectBottomSheet(
                boardMembers = currentBoard.members,
                selectedMembers = ArrayList(selectedResponsibilityPersons),
                viewModel.getUsers(),
                onUserSelected = { worker ->
                    if (selectedResponsibilityPersons.contains(worker))
                        selectedResponsibilityPersons.remove(worker)
                    else
                        selectedResponsibilityPersons.add(worker)
                    responsibilityPersonsRcViewAdapter.update(
                        ArrayList(
                            selectedResponsibilityPersons
                        )
                    )
                }
            )
            searchResponsibilityUsers.show(parentFragmentManager, "SearchResponsibilityPersons")
        }

        binding.workersSearchEditText.setOnClickListener {
            val searchWorkers = UserSelectBottomSheet(
                boardMembers = currentBoard.members,
                selectedMembers = ArrayList(selectedWorkers),
                viewModel.getUsers(),
                onUserSelected = { worker ->
                    if (selectedWorkers.contains(worker))
                        selectedWorkers.remove(worker)
                    else
                        selectedWorkers.add(worker)
                    workersRcViewAdapter.update(
                        ArrayList(
                            selectedWorkers
                        )
                    )
                }
            )
            searchWorkers.show(parentFragmentManager, "SearchResponsibilityPersons")
        }

        binding.saveColumnButton.setOnClickListener {
            val updatedColumn = viewModel.currentColumn().copy(
                title = binding.columnTitleEditText.text.toString(),
                responsiblePerson = selectedResponsibilityPersons.toList(),
                workers = selectedWorkers.toList()
            )
            Log.d("alz-04", "saveColumnButton clicked: ${updatedColumn.title}")
            viewModel.updateColumn(updatedColumn)
        }
    }
}