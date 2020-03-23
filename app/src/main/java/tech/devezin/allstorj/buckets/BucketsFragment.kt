package tech.devezin.allstorj.buckets

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.devezin.allstorj.R

class BucketsFragment : Fragment() {

    companion object {
        fun newInstance() = BucketsFragment()
    }

    private lateinit var viewModel: BucketsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buckets, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BucketsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
