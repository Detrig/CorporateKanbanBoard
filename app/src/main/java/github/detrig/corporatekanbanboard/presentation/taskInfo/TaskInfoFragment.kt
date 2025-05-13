package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.databinding.FragmentTaskInfoBinding

class TaskInfoFragment : AbstractFragment<FragmentTaskInfoBinding>() {
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskInfoBinding = FragmentTaskInfoBinding.inflate(inflater, container, false)
}