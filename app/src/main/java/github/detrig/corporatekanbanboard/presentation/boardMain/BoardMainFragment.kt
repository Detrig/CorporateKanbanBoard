package github.detrig.corporatekanbanboard.presentation.boardMain

import android.view.LayoutInflater
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.databinding.FragmentBoardMainBinding
import github.detrig.corporatekanbanboard.presentation.addBoard.ColumnsRcViewAdapter

class BoardMainFragment : AbstractFragment<FragmentBoardMainBinding>() {

    private lateinit var viewModel : BoardMainViewModel
    private lateinit var columnsRcViewAdapter: ColumnsRcViewAdapter

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardMainBinding = FragmentBoardMainBinding.inflate(inflater, container, false)

    private fun initRcView() {
        columnsRcViewAdapter = ColumnsRcViewAdapter
    }
}