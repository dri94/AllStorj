package tech.devezin.allstorj.buckets

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BucketPresentable(val name: String, val description: String) : Parcelable
