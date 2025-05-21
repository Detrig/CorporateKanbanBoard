package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.databinding.BottomSheetPhotoPreviewBinding

class PhotoPreviewBottomSheet(
    private val photo: String,
    private val onDeleteClicked: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPhotoPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPhotoPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bitmap = ImageUtils.base64ToBitmap(photo)
        if (bitmap != null) {
            Glide.with(requireContext())
                .load(bitmap)
                .fitCenter()
                .into(binding.previewImage)
        }

        binding.deleteButton.setOnClickListener {
            onDeleteClicked()
            dismiss()
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
