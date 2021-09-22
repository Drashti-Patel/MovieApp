package com.movie.marvel.base

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movie.marvel.utils.PAGE_LIMIT

abstract class PaginationListener(private var layoutManager: GridLayoutManager?) : RecyclerView.OnScrollListener() {

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    abstract fun loadMoreItems()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (layoutManager == null) {
            return
        }

        val visibleItemCount = layoutManager!!.childCount
        val totalItemCount = layoutManager!!.itemCount
        val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= PAGE_LIMIT && visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
        }
    }
}