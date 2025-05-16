package github.detrig.corporatekanbanboard.presentation.globalchat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentGlobalChatBinding
import github.detrig.corporatekanbanboard.domain.model.chat.GlobalChatMessage
import java.io.ByteArrayOutputStream

class GlobalChatFragment : AbstractFragment<FragmentGlobalChatBinding>() {

    private lateinit var viewModel: GlobalChatViewModel
    private lateinit var globalChatRcViewAdapter: MessageRcViewAdapter
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val base64Image = uriToBase64(imageUri)
                if (base64Image != null) {
                    sendImageMessage(base64Image)
                } else {
                    Toast.makeText(requireContext(), "Ошибка при обработке изображения", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGlobalChatBinding =
        FragmentGlobalChatBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(GlobalChatViewModel::class.java)

        initRcView()
        setupObservers()
        setupInputControls()
    }

    private fun initRcView() {
        globalChatRcViewAdapter = MessageRcViewAdapter()

        binding.messagesRcView.adapter = globalChatRcViewAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messagesRcView.layoutManager = layoutManager

        binding.messagesRcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                if (layoutManager.findFirstVisibleItemPosition() == 0) {
                    viewModel.loadPreviousMessages()
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            globalChatRcViewAdapter.update(ArrayList(messages))
            // Скролл к последнему сообщению при обновлении
            binding.messagesRcView.scrollToPosition(messages.size - 1)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

    }


    private fun setupInputControls() {
        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val message = GlobalChatMessage(
                id = "",
                senderId = App.currentUserId,
                senderName = App.currentUserName,
                text = text,
                imageBase64 = "",
                timestamp = System.currentTimeMillis()
            )
            viewModel.sendMessage(message)
            binding.messageEditText.text?.clear()
        }

        binding.attachImageButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendImageMessage(base64Image: String) {
        val message = GlobalChatMessage(
            id = "",
            senderId = App.currentUserId,
            senderName = App.currentUserName,
            text = "", // можно оставить пустым или добавить подпись
            imageBase64 = base64Image,
            timestamp = System.currentTimeMillis()
        )
        viewModel.sendMessage(message)
    }
}
