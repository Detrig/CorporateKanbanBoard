package github.detrig.corporatekanbanboard.presentation.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentBoardsBinding
import github.detrig.corporatekanbanboard.domain.model.Board

class BoardsFragment : AbstractFragment<FragmentBoardsBinding>() {

    private lateinit var viewModel: BoardsViewModel
    private lateinit var boardsRcViewAdapter: BoardsRcViewAdapter
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardsBinding = FragmentBoardsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ProvideViewModel).viewModel(BoardsViewModel::class.java)

        initRcView()
    }

    private fun initRcView() {
        boardsRcViewAdapter = BoardsRcViewAdapter(object : BoardsRcViewAdapter.OnBoardClickListener {
            override fun onClick(board: Board) {
                Toast.makeText(requireContext(), board.title, Toast.LENGTH_SHORT).show()
            }
        })
        binding.boardsRcView.adapter = boardsRcViewAdapter
    }
}