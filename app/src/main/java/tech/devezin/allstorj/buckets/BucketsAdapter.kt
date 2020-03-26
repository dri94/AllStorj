package tech.devezin.allstorj.buckets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.devezin.allstorj.R

class BucketsAdapter(val listener: BucketClickListener): RecyclerView.Adapter<BucketsAdapter.Companion.BucketViewHolder>() {

    interface BucketClickListener {
        fun onBucketClick(bucketPresentable: BucketPresentable)
        fun onBucketMenuClick(bucketPresentable: BucketPresentable)
    }

    var buckets: List<BucketPresentable> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketViewHolder {
        return BucketViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bucket, parent, false))
    }

    override fun getItemCount(): Int {
        return buckets.size
    }

    override fun onBindViewHolder(holder: BucketViewHolder, position: Int) {
        val bucket = buckets[position]
        holder.name.text = bucket.name
        holder.description.text = bucket.description
        holder.moreButton.setOnClickListener {
            listener.onBucketMenuClick(bucket)
        }
        holder.itemView.setOnClickListener {
            listener.onBucketClick(bucket)
        }
    }

    companion object {
        class BucketViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.itemBucketTitle)
            val description: TextView = view.findViewById(R.id.itemBucketDescription)
            val moreButton: ImageButton = view.findViewById(R.id.itemBucketMoreButton)
        }
    }
}
