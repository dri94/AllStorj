package tech.devezin.allstorj.files.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.storj.Bucket
import kotlinx.android.synthetic.main.dialog_create_bucket.*
import kotlinx.android.synthetic.main.dialog_upload_file.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.expandHeight
import tech.devezin.allstorj.utils.observeEvent
import tech.devezin.allstorj.utils.text
import tech.devezin.allstorj.utils.viewModels

class FileUploadOptionsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val BROADCAST_FILE_UPLOADED = "file_uploaded"
        private const val EXTRA_BUCKET_NAME = "bucket_name"
        private const val EXTRA_URI = "uri"
        fun newInstance(bucketName: String, uri: Uri) : FileUploadOptionsBottomSheet {
            val fragment = FileUploadOptionsBottomSheet()
            fragment.arguments = bundleOf(EXTRA_BUCKET_NAME to bucketName, EXTRA_URI to uri)
            return fragment
        }
    }

    private val viewModel: FileUploadOptionsViewModel by viewModels {
        FileUploadOptionsViewModel(requireArguments().getString(EXTRA_BUCKET_NAME, ""), requireArguments().getParcelable<Uri>(EXTRA_URI) as Uri, requireActivity().contentResolver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_upload_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandHeight()
        uploadFileExpirationButton.setOnClickListener {
            MaterialDialog(requireContext()).show {
                datePicker(requireFutureDate = true) { _, calendar ->
                    viewModel.onExpirationDateSelected(calendar.time)
                }
            }
        }
        uploadFileConfirm.setOnClickListener {
            viewModel.onCreateFileClicked(
                blockSizeString = uploadFileEncryptionBlockSizeInput.text(),
                encryptionChipId = uploadFileEncryptionCipherChipGroup.checkedChipId,
                path = uploadFilePathInput.text(),
                totalSharesString = uploadFileRedundancyTotalSharesInput.text(),
                successSharesString = uploadFileRedundancySuccessSharesInput.text(),
                shareSizeString = uploadFileRedundancyShareSizeInput.text(),
                requiredSharesString = uploadFileRedundancyRequiredSharesInput.text(),
                repairSharesString = uploadFileRedundancyRepairSharesInput.text(),
                mimeType = uploadFileMimeInput.text()
            )
        }
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            uploadFileTitle.text = it.fileName
            uploadFileError.text = it.error
            uploadFileMimeInput.setText(it.mimeType, TextView.BufferType.EDITABLE)
            setAdvancedOptionsVisibility(it.showAdvancedOptions)
        })
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is FileUploadOptionsViewModel.Events.DismissDialogOnSuccess -> {
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(BROADCAST_FILE_UPLOADED))
                    dismiss()
                    true
                }
            }
        }
        uploadFileAdvancedOptionsLabel.setOnClickListener {
            viewModel.onAdvancedOptionsClick()
        }
    }

    private fun setAdvancedOptionsVisibility(isVisible: Boolean) {
        uploadFileAdvancedOptionsLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            if (isVisible) R.drawable.ic_expand_less else R.drawable.ic_expand_more,
            0
        )
        val optionsVisibility = if (isVisible) View.VISIBLE else View.GONE
        uploadFileMimeLayout.visibility = optionsVisibility
        uploadFileMimeLabel.visibility = optionsVisibility
        uploadFileEncryptionCipherChipGroup.visibility = optionsVisibility
        uploadFileEncryptionParametersLabel.visibility = optionsVisibility
        uploadFileEncryptionBlockSizeLayout.visibility = optionsVisibility
        uploadFileExpirationLabel.visibility = optionsVisibility
        uploadFileExpirationLayout.visibility = optionsVisibility
        uploadFileRedundancyLabel.visibility = optionsVisibility
        uploadFileRedundancyShares1Layout.visibility = optionsVisibility
        uploadFileRedundancyShares2Layout.visibility = optionsVisibility
    }
}
