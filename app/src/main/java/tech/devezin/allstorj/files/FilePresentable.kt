package tech.devezin.allstorj.files

import androidx.annotation.DrawableRes

data class FilePresentable(val name: String, val path: String, val description: String, @DrawableRes val drawableRes: Int)
