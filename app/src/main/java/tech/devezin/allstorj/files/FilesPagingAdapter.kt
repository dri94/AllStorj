package tech.devezin.allstorj.files

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tech.devezin.allstorj.R

class FilesPagingAdapter (private val listener: FilesAdapter.FileClickListener) : PagedListAdapter<FilePresentable, FilesPagingAdapter.Companion.FilesViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        return FilesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false))
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val presentable = getItem(position) as FilePresentable
        holder.title.text = presentable.name
        holder.description.text = presentable.description
        holder.icon.setImageResource(presentable.drawableRes)
        holder.menuButton.setOnClickListener {
            listener.onMenuClick(presentable)
        }
        holder.itemView.setOnClickListener {
            listener.onClick(presentable)
        }
    }

    companion object {
        class FilesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.itemFileTitle)
            val icon: ImageView = view.findViewById(R.id.itemFileIcon)
            val description: TextView = view.findViewById(R.id.itemFileDescription)
            val menuButton: ImageButton = view.findViewById(R.id.itemFileMoreButton)
        }
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilePresentable>() {
            override fun areItemsTheSame(oldItem: FilePresentable, newItem: FilePresentable): Boolean {
                return oldItem.name == newItem.name && oldItem.isPrefix == newItem.isPrefix
            }

            override fun areContentsTheSame(oldItem: FilePresentable, newItem: FilePresentable): Boolean {
                return oldItem == newItem
            }
        }

    }
}
