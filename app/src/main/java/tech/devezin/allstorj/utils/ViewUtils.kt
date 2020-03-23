package tech.devezin.allstorj.utils

import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.text(): String {
    return this.text?.toString() ?: ""
}
