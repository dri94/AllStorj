package tech.devezin.allstorj.files

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_files.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.observeEvent
import tech.devezin.allstorj.utils.viewModels

class FilesFragment : Fragment() {

    companion object {
        private const val EXTRA_BUCKET_NAME = "bucket_name"
        private const val RC_PICK_FILE = 0
        fun newInstance(bucketName: String) : FilesFragment {
            val fragment = FilesFragment()
            fragment.arguments = bundleOf(EXTRA_BUCKET_NAME to bucketName)
            return fragment
        }
    }

    private val viewModel: FilesViewModel by viewModels {
        FilesViewModel(requireArguments().getString(EXTRA_BUCKET_NAME, ""))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_files, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = FilesAdapter(viewModel)
        filesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            adapter.items = it.files
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
                    false
                }
                is FilesViewModel.Events.OpenSystemFilePicker -> {
                    startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }, RC_PICK_FILE)
                    false
                }
            }
        }
        filesCreateButton.setOnClickListener {
            viewModel.onCreateFileClicked()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_PICK_FILE -> {
                val uri = data?.data
                if (resultCode == Activity.RESULT_OK && uri != null) {
                    activity?.contentResolver?.openInputStream(uri)?.let {
                        //viewModel.onFileChosen()
                    }

                }
            }
            else  -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
