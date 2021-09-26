package com.movie.marvel.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.movie.marvel.R
import java.math.BigInteger
import java.security.MessageDigest

const val PAGE_LIMIT = 20
const val STR_LAST_WEEK = "lastWeek"
const val STR_THIS_WEEK = "thisWeek"
const val STR_NEXT_WEEK = "nextWeek"
const val STR_THIS_MONTH = "thisMonth"
const val MOVIE_DATA = "movieDetail"
const val DEFAULT_TITLE = "marvel character"

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun Boolean.toViewVisibility(): Int {
    return if (this) View.VISIBLE else View.GONE
}

fun Fragment.showError(errorMessage: String?) {
    context?.let { showError(it, errorMessage) }
}

private fun showError(context: Context, errorMessage: String?) {
    with(context as? AppCompatActivity) {
        val viewGroup = (this?.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        Snackbar.make(viewGroup, errorMessage ?: getString(R.string.error_unspecified), Snackbar.LENGTH_LONG).show()
    }
}

