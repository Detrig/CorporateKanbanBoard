package github.detrig.corporatekanbanboard.presentation.boardMain

import android.view.LayoutInflater
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.databinding.FragmentBoardMainBinding

class BoardMainFragment : AbstractFragment<FragmentBoardMainBinding>() {

    private lateinit var viewModel : BoardMainViewModel
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardMainBinding = FragmentBoardMainBinding.inflate(inflater, container, false)
}