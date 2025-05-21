package github.detrig.corporatekanbanboard.presentation.columnInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import github.detrig.corporatekanbanboard.databinding.BottomSheetWorkersBinding
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.User
import github.detrig.corporatekanbanboard.presentation.addtask.WorkersRcViewAdapter

class UserSelectBottomSheet(
    private val boardMembers: List<BoardMember>,
    private val selectedMembers: List<BoardMember>,
    private val boardUsersList: List<User>,
    private val onUserSelected: (BoardMember) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetWorkersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WorkersRcViewAdapter
    private var filteredList = boardMembers.toList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetWorkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRcView()

        adapter.selectedWorkersList = ArrayList(selectedMembers)
        adapter.update(ArrayList(boardMembers))

        binding.searchUserEditText.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()
            val filtered = boardMembers.filter {
                it.name.lowercase().contains(query) || it.email.lowercase()
                    .contains(query)
            }
            adapter.update(ArrayList(filtered))
        }

        binding.confirmButton.setOnClickListener { dismiss() }
    }

    private fun initRcView() {
        adapter = WorkersRcViewAdapter(object : WorkersRcViewAdapter.OnWorkerClickListener {
            override fun onClick(worker: BoardMember) {

            }

            override fun onSelectWorker(worker: BoardMember) {
                onUserSelected(worker)
            }
        }, boardUsersList)
        binding.usersRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
