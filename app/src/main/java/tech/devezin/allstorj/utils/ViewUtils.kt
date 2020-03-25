package tech.devezin.allstorj.utils

import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.text(): String {
    return this.text?.toString() ?: ""
}

fun BottomSheetDialogFragment.expandHeight() {
    this.view?.viewTreeObserver?.addOnGlobalLayoutListener {
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            BottomSheetBehavior.from(it).apply {
                this.state = BottomSheetBehavior.STATE_EXPANDED
                this.peekHeight = 0
            }
        }
    }
}
