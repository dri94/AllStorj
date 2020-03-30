package tech.devezin.allstorj.files

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.devezin.allstorj.R

class FilesAdapter(private val listener: FileClickListener) :
    RecyclerView.Adapter<FilesAdapter.Companion.FilesViewHolder>() {

    interface FileClickListener {
        fun onClick(presentable: FilePresentable)
        fun onMenuClick(presentable: FilePresentable)
    }

    var items: List<FilePresentable> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        return FilesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val presentable = items[position]
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
    }
}
