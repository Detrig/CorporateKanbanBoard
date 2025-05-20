package github.detrig.corporatekanbanboard.presentation.addBoard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.core.ImageUtils.uriToBase64
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentAddBoardBinding
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.Column
import java.text.SimpleDateFormat
import java.util.Date

class AddBoardFragment : AbstractFragment<FragmentAddBoardBinding>() {

    private lateinit var viewModel: AddBoardViewModel
    private lateinit var columnsRcViewAdapter: ColumnsRcViewAdapter
    private lateinit var pickImageLauncher : ActivityResultLauncher<String>
    private var pickedImage : String = ""

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddBoardBinding = FragmentAddBoardBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(AddBoardViewModel::class.java)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val base64 = ImageUtils.uriToBase64(requireContext(), it)
                    pickedImage = base64

                    val bitmap = ImageUtils.base64ToBitmap(pickedImage)
                    if (bitmap != null) {
                        Glide.with(requireContext())
                            .load(bitmap)
                            .circleCrop()
                            .placeholder(R.drawable.user)
                            .into(binding.boardImage)
                    }
                }
            }

        initRcView()

        binding.addBoardButton.setOnClickListener {
            addBoard()
        }

        binding.boardImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun addBoard() {
        if (binding.boardNameEditText.text.toString().isNotBlank()) {
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val currentDate = sdf.format(Date())
            val newBoard = Board(
                title = binding.boardNameEditText.text.toString(),
                description = binding.boardDescriptionEditText.text.toString(),
                columns = columnsRcViewAdapter.list,
                dateCreated = currentDate,
                photoBase64 = pickedImage
            )
            viewModel.addBoard(newBoard)
        } else {
            Toast.makeText(requireContext(), "Введите название доски", Toast.LENGTH_SHORT).show()
        }
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
            Column.newInstance("To-do"),
            Column.newInstance("Do today"),
            Column.newInstance("In progress"),
            Column.newInstance("Done"),
        ))

        binding.addColumnButton.setOnClickListener {
            columnsRcViewAdapter.addItem(Column.newInstance(""))
        }
    }
}