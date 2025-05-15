package github.detrig.corporatekanbanboard.presentation.boardSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.databinding.FragmentBoardSettingsBinding
import github.detrig.corporatekanbanboard.domain.model.BoardMember

class BoardSettingsFragment : AbstractFragment<FragmentBoardSettingsBinding>() {

    private lateinit var membersRcViewAdapter: MembersRcViewAdapter

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardSettingsBinding =
        FragmentBoardSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRcView()
    }

    private fun initRcView() {
        membersRcViewAdapter = MembersRcViewAdapter(object : MembersRcViewAdapter.OnMemberClickListener {
            override fun onClick(member: BoardMember) {

            }
        })
        binding.membersRcView.adapter = membersRcViewAdapter
    }
}