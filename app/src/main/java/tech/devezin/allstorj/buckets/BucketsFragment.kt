package tech.devezin.allstorj.buckets

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.storj.Bucket
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
    }

}
