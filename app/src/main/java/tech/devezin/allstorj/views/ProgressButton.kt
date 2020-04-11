package tech.devezin.allstorj.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_progress_button.view.*
import tech.devezin.allstorj.R

class ProgressButton @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0): FrameLayout(context, attrs, defStyleAttr) {

    private val text: String
    private var onClickListener: OnClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_progress_button, this, true)
        context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0).apply {
            val isDisabled = getBoolean(R.styleable.ProgressButton_disabled, false)
            text = getString(R.styleable.ProgressButton_text) ?: ""
            progressButton.isEnabled = !isDisabled
            recycle()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        onClickListener = l
        progressButton.setOnClickListener(onClickListener)
    }

    fun setLoading(isLoading: Boolean) {
        progressButton.text = if (isLoading) null else text
        progressButton.setOnClickListener(if (isLoading) null else onClickListener)
        if (isLoading) progressBar.show() else progressBar.hide()
    }
}

