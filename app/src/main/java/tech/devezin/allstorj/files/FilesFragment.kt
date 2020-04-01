package tech.devezin.allstorj.files

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.invoke
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_files.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.buckets.create.CreateBucketBottomSheet
import tech.devezin.allstorj.buckets.detail.BucketDetailBottomSheet
import tech.devezin.allstorj.files.upload.FileUploadOptionsBottomSheet
import tech.devezin.allstorj.utils.*

class FilesFragment : Fragment() {

    companion object {
        private const val EXTRA_BUCKET_NAME = "bucket_name"
        private const val RC_PICK_FILE = 0
        fun newInstance(bucketName: String): FilesFragment {
            val fragment = FilesFragment()
            fragment.arguments = bundleOf(EXTRA_BUCKET_NAME to bucketName)
            return fragment
        }
    }

    private val viewModel: FilesViewModel by viewModels {
        FilesViewModel(requireArguments().getString(EXTRA_BUCKET_NAME, ""))
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.onRefresh()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_files, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FilesAdapter(viewModel)
        filesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            adapter.items = it.files
            filesSwipeRefreshLayout.isRefreshing = it.isLoading
        })
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is FilesViewModel.Events.ShowFileMenuBottomSheet -> {
                    false
                }
                is FilesViewModel.Events.ShowFileDetailBottomSheet -> {
                    false
                }
                is FilesViewModel.Events.ShowNewFileDialog -> {
                    FileUploadOptionsBottomSheet.newInstance(it.bucketName, it.uri).show(parentFragmentManager, FileUploadOptionsBottomSheet::class.java.simpleName)
                    true
                }
                is FilesViewModel.Events.OpenSystemFilePicker -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                        type = "*/*"
                    }
                    safelyStartIntent(ActivityResultContracts.StartActivityForResult(), intent) {
                        val uri = it.data?.data ?: return@safelyStartIntent
                        viewModel.onFileChosen(uri)
                    }
                    true
                }
            }
        }
        filesCreateButton.setOnClickListener {
            viewModel.onCreateFileClicked()
        }
        filesSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onRefresh()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_PICK_FILE -> {

            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter().also {
            it.addAction(FileUploadOptionsBottomSheet.BROADCAST_FILE_UPLOADED)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

}
