package github.detrig.corporatekanbanboard.presentation.addBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.data.local.model.BoardEntity
import github.detrig.corporatekanbanboard.databinding.FragmentAddBoardBinding
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.Column

class AddBoardFragment : AbstractFragment<FragmentAddBoardBinding>() {

    private lateinit var viewModel: AddBoardViewModel
    private lateinit var columnsRcViewAdapter: ColumnsRcViewAdapter

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddBoardBinding = FragmentAddBoardBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(AddBoardViewModel::class.java)

        initRcView()

        binding.addBoardButton.setOnClickListener {
            addBoard()
        }
    }

    private fun addBoard() {
        val newBoard = Board(
            title = binding.boardNameEditText.text.toString(),
            description = binding.boardDescriptionEditText.text.toString(),
            columns = columnsRcViewAdapter.list
        )
        viewModel.addBoard(newBoard)
    }

    private fun initRcView() {
        columnsRcViewAdapter = ColumnsRcViewAdapter { position ->
            columnsRcViewAdapter.removeItem(position)
        }

        binding.columnsRcView.apply {
            adapter = columnsRcViewAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        columnsRcViewAdapter.update(arrayListOf(
            Column(title = "To-do"),
            Column(title = "Do today"),
            Column(title = "In progress"),
            Column(title = "Done")
        ))

        binding.addColumnButton.setOnClickListener {
            columnsRcViewAdapter.addItem(Column(""))
        }
    }
}