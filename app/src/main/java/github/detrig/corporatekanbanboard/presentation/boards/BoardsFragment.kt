package github.detrig.corporatekanbanboard.presentation.boards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentBoardsBinding
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.main.MainActivity

class BoardsFragment : AbstractFragment<FragmentBoardsBinding>() {

    private lateinit var viewModel: BoardsViewModel
    private lateinit var boardsRcViewAdapter: BoardsRcViewAdapter
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardsBinding = FragmentBoardsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showHeaderBottomNav()

        viewModel = (activity as ProvideViewModel).viewModel(BoardsViewModel::class.java)

        viewModel.getBoards()
        initRcView()

        viewModel.savedBoard.value?.let {
            boardsRcViewAdapter.update(ArrayList(it))
        }

        viewModel.observe(viewLifecycleOwner, { boardList ->
            boardsRcViewAdapter.update(ArrayList(boardList))
        })

        binding.addBoardButton.setOnClickListener {
            viewModel.addBoardScreen()
        }
    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.getBoards()
//    }

    private fun initRcView() {
        boardsRcViewAdapter =
            BoardsRcViewAdapter(object : BoardsRcViewAdapter.OnBoardClickListener {
                override fun onClick(board: Board) {
                    viewModel.clickedBoardScreen(board)
                }
            })
        binding.boardsRcView.adapter = boardsRcViewAdapter
    }
}