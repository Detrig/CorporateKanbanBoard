package github.detrig.corporatekanbanboard.presentation.boardSettings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentBoardSettingsBinding
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.Column
import github.detrig.corporatekanbanboard.presentation.addBoard.ColumnsRcViewAdapter

class BoardSettingsFragment : AbstractFragment<FragmentBoardSettingsBinding>() {

    private lateinit var membersRcViewAdapter: MembersRcViewAdapter
    private lateinit var columnsRcViewAdapter: ColumnsRcViewAdapter
    private lateinit var viewModel: BoardSettingsViewModel
    private var newColumnsList = mutableListOf<Column>()
    private var newMembersList = mutableListOf<BoardMember>()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardSettingsBinding =
        FragmentBoardSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ProvideViewModel).viewModel(BoardSettingsViewModel::class.java)

        initRcView()

        viewModel.currentBoardLiveData().value?.let {
            newColumnsList = it.columns.toMutableList()
            newMembersList = it.members.toMutableList()
            setUpViews(it)
        }

        addColumnClickListener()
        addMemberClickListener()

        viewModel.currentBoardLiveData().observe(viewLifecycleOwner) {
            membersRcViewAdapter.update(ArrayList(it.members))
        }

        binding.saveButton.setOnClickListener {
            saveBoard()
        }

        binding.deleteBoardButton.setOnClickListener {
            showDeleteBoardConfirmationDialog()
        }
    }

    private fun initRcView() {
        membersRcViewAdapter =
            MembersRcViewAdapter(object : MembersRcViewAdapter.OnMemberClickListener {
                override fun onClick(member: BoardMember) {

                }

                override fun deleteMemberClickListener(member: BoardMember) {
                    showDeleteMemberConfirmationDialog(member)
                }
            }, viewModel.getUsers())
        binding.membersRcView.adapter = membersRcViewAdapter

        columnsRcViewAdapter = ColumnsRcViewAdapter { position ->
            columnsRcViewAdapter.removeItem(position)
        }
        binding.allColumnsRcView.adapter = columnsRcViewAdapter
    }

    private fun setUpViews(board: Board) {
        binding.boardNameEditText.setText(board.title)
        binding.accessSpinner.setSelection(1)
        binding.deleteBoardButton.visibility =
            if (App.currentUserEmail == board.creatorEmail) View.VISIBLE else View.GONE
        membersRcViewAdapter.update(ArrayList(board.members))
        columnsRcViewAdapter.update(ArrayList(board.columns))
    }

    private fun saveBoard() {
        val updatedBoard = viewModel.currentBoard()
            .copy(
                columns = columnsRcViewAdapter.list,
                members = newMembersList,
                title = binding.boardNameEditText.text.toString()
            )
        viewModel.updateBoardAndUsers(newMembersList, updatedBoard)
    }

    private fun addMemberClickListener() {
        binding.addMemberButton.setOnClickListener {
            val email = binding.emailEditText.text
            if (email.isNotBlank()) {
                Toast.makeText(requireContext(), "Идет проверка пользователя", Toast.LENGTH_SHORT)
                    .show()
                viewModel.isUserExistByEmail(email.toString())
                viewModel.isUserExistLiveData.observe(viewLifecycleOwner) {
                    if (it.second != null) {
                        val access = when (binding.accessSpinner.selectedItem.toString()) {
                            "Админ" -> BoardAccess.ADMIN
                            "Лидер" -> BoardAccess.LEADER
                            "Работник" -> BoardAccess.WORKER
                            "Наблюдатель" -> BoardAccess.VIEWER
                            else -> {
                                BoardAccess.VIEWER
                            }
                        }
                        val newBoardMember = BoardMember(
                            viewModel.isUserExistLiveData.value?.second?.id ?: "",
                            viewModel.isUserExistLiveData.value?.second?.email ?: "",
                            viewModel.isUserExistLiveData.value?.second?.name ?: "",
                            access
                        )

                        var isMemberAlreadyAdded = false
                        membersRcViewAdapter.list.forEach {
                            if (it.email == newBoardMember.email) {
                                isMemberAlreadyAdded = true
                            }
                        }

                        if (!isMemberAlreadyAdded) {
                            newMembersList.add(
                                newBoardMember
                            )
                            membersRcViewAdapter.update(ArrayList(newMembersList))
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Данный пользователь уже добавлен",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Введите почтовый адрес нового участника",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addColumnClickListener() {
        binding.addColumnButton.setOnClickListener {
            if (binding.columnNameEditText.text.isNotBlank()) {
                newColumnsList.add(Column.newInstance(binding.columnNameEditText.text.toString()))
                columnsRcViewAdapter.update(ArrayList(newColumnsList))
                binding.columnNameEditText.setText("")
            } else {
                Toast.makeText(requireContext(), "Введите название столбца", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showDeleteMemberConfirmationDialog(member: BoardMember) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление участника")
            .setMessage("Вы уверены, что хотите удалить участника ${member.email}?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteUserFromBoard(member.userId)
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    private fun showDeleteBoardConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление доски")
            .setMessage("Вы уверены, что хотите удалить доску?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteCurrentBoard()
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }
}