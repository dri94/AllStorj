package tech.devezin.allstorj.buckets.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_bucket_detail.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.buckets.BucketPresentable
import tech.devezin.allstorj.utils.observeEvent
import tech.devezin.allstorj.utils.viewModels

class BucketDetailBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: BucketDetailViewModel by viewModels {
        BucketDetailViewModel(requireArguments().getParcelable<BucketPresentable>(EXTRA_BUCKET) as BucketPresentable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bucket_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            bucketInfoTitle.text = it.name
            bucketInfoDate.text = it.created
            bucketInfoEncryptionDescription.text = it.encryptionArgs?.let {
                getString(
                    R.string.bucket_info_encryption,
                    getString(it[0] as Int),
                    it[1] as Int
                )
            }
            bucketInfoPathCipherDescription.text =
                it.pathCipher?.let {
                    getString(
                        R.string.bucket_info_path_cipher,
                        getString(it)
                    )
                }
            bucketInfoSegmentSizeDescription.text = it.segment
            bucketInfoRedundancyDescription.text = it.redundancy?.let {
                getString(
                    R.string.bucket_info_redundancy,
                    it[0] as Short,
                    it[1] as Short,
                    it[2] as Short,
                    it[3] as Short,
                    it[4] as Int
                )
            }
            bucketInfoError.text = it.error
        })
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is BucketDetailViewModel.Events.GoToBucketList -> {
                    LocalBroadcastManager.getInstance(requireContext())
                        .sendBroadcast(Intent(BROADCAST_BUCKET_DELETED))
                    dismiss()
                    true
                }
            }
        }
    }

    companion object {
        const val BROADCAST_BUCKET_DELETED = "bucket_deleted"
        const val EXTRA_BUCKET = "bucket_presentable"

        fun newInstance(presentable: BucketPresentable): BucketDetailBottomSheet {
            val fragment = BucketDetailBottomSheet()
            fragment.arguments = bundleOf(EXTRA_BUCKET to presentable)
            return fragment
        }
    }
}
