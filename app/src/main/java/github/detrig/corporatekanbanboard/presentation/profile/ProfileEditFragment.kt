package github.detrig.corporatekanbanboard.presentation.profile

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ImageHelper.bitmapToBase64
import github.detrig.corporatekanbanboard.core.ImageUtils.base64ToBitmap
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentEditProfileBinding

class ProfileEditFragment : AbstractFragment<FragmentEditProfileBinding>() {

    private lateinit var viewModel: ProfileEditViewModel
    private var selectedPhotoBase64: String? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                selectedPhotoBase64 = bitmapToBase64(bitmap)
                binding.avatarImageButton.setImageBitmap(bitmap)
            }
        }

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditProfileBinding = FragmentEditProfileBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(ProfileEditViewModel::class.java)

        initViews()
    }

    private fun initViews() {
        val currentUser = viewModel.getCurrentUser()
        binding.nameEditText.setText(currentUser.name)
        binding.emailTextView.text = currentUser.email
        if (currentUser.avatarBase64.isNotEmpty()) {
            val avatarBitmap = base64ToBitmap(currentUser.avatarBase64)
            selectedPhotoBase64 = currentUser.avatarBase64
            Glide.with(requireContext())
                .load(avatarBitmap)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(binding.avatarImageButton)
        }

        binding.avatarImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.saveProfileButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val avatar = selectedPhotoBase64 ?: ""

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateProfile(name, avatar)
            Toast.makeText(requireContext(), "Профиль обновлён", Toast.LENGTH_SHORT).show()
        }

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход из аккаунта")
            .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            .setPositiveButton("Выйти") { _, _ -> viewModel.logout() }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }
}