package tech.devezin.allstorj.buckets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_buckets.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.buckets.create.CreateBucketBottomSheet
import tech.devezin.allstorj.buckets.create.CreateBucketBottomSheet.Companion.BROADCAST_BUCKET_CREATED
import tech.devezin.allstorj.buckets.detail.BucketDetailBottomSheet
import tech.devezin.allstorj.buckets.detail.BucketDetailBottomSheet.Companion.BROADCAST_BUCKET_DELETED
import tech.devezin.allstorj.files.FilesFragment
import tech.devezin.allstorj.utils.observeEvent
import tech.devezin.allstorj.utils.viewModels

class BucketsFragment : Fragment() {

    companion object {
        fun newInstance() = BucketsFragment()
    }

    private val viewModel: BucketsViewModel by viewModels()
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.onRefresh()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buckets, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bucketsCreateButton.setOnClickListener {
            CreateBucketBottomSheet().show(parentFragmentManager, CreateBucketBottomSheet::class.java.simpleName)
        }
        val adapter = BucketsAdapter(viewModel)
        bucketsList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            adapter.buckets = it.buckets
        })
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is BucketsViewModel.Events.ShowBucketDetailDialog -> {
                    BucketDetailBottomSheet.newInstance(it.bucket).show(parentFragmentManager, BucketDetailBottomSheet::class.java.simpleName)
                    true
                }
                is BucketsViewModel.Events.GoToBucketScreen -> {
                    (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.mainContainer, FilesFragment.newInstance(it.bucketPresentable.name)).commit()
                    true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter().also {
            it.addAction(BROADCAST_BUCKET_DELETED)
            it.addAction(BROADCAST_BUCKET_CREATED)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

}
