package tech.devezin.allstorj.buckets.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_create_bucket.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.expandHeight
import tech.devezin.allstorj.utils.observeEvent
import tech.devezin.allstorj.utils.text
import tech.devezin.allstorj.utils.viewModels

class CreateBucketBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: CreateBucketViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_create_bucket, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            setAdvancedOptionsVisibility(it.showAdvancedOptions)
            createBucketError.text = it.error
        })
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is CreateBucketViewModel.Events.GoToBucket -> {
                    this.dismiss()
                    false
                }
            }
        }
        createBucketAdvancedOptionsLabel.setOnClickListener {
            viewModel.onAdvancedOptionsClicked()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandHeight()
        createBucketConfirm.setOnClickListener {
            viewModel.onCreateGroupClicked(
                bucketName = createBucketInput.text(),
                encryptionChipId = createBucketEncryptionCipherChipGroup.checkedChipId,
                blockSizeString = createBucketEncryptionBlockSizeInput.text(),
                cipherChipId = createBucketPathCipherChipGroup.checkedChipId,
                segmentSizeString = createBucketSegmentSizeInput.text(),
                repairSharesString = createBucketRedundancyRepairSharesInput.text(),
                requiredSharesString = createBucketRedundancyRequiredSharesInput.text(),
                shareSizeString = createBucketRedundancyShareSizeInput.text(),
                successSharesString = createBucketRedundancySuccessSharesInput.text(),
                totalSharesString = createBucketRedundancyTotalSharesInput.text()
            )
        }
    }

    private fun setAdvancedOptionsVisibility(isVisible: Boolean) {
        createBucketAdvancedOptionsLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            if (isVisible) R.drawable.ic_expand_less else R.drawable.ic_expand_more,
            0
        )
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        createBucketEncryptionParametersLabel.visibility = visibility
        createBucketEncryptionCipherChipGroup.visibility = visibility
        createBucketEncryptionBlockSizeLayout.visibility = visibility
        createBucketPathCipherLabel.visibility = visibility
        createBucketPathCipherChipGroup.visibility = visibility
        createBucketSegmentSizeLabel.visibility = visibility
        createBucketSegmentSizeLayout.visibility = visibility
        createBucketRedundancyLabel.visibility = visibility
        createBucketRedundancyShares1Layout.visibility = visibility
        createBucketRedundancyShares2Layout.visibility = visibility
    }
}
