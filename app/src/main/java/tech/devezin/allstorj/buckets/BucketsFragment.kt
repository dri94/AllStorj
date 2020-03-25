package tech.devezin.allstorj.buckets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_buckets.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.buckets.create.CreateBucketBottomSheet
import tech.devezin.allstorj.utils.viewModels

class BucketsFragment : Fragment() {

    companion object {
        fun newInstance() = BucketsFragment()
    }

    private val viewModel: BucketsViewModel by viewModels()

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
        val adapter = BucketsAdapter {
            viewModel.onBucketClick(it)
        }
        bucketsList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            adapter.buckets = it.buckets
        })
    }

}
