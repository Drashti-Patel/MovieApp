package com.movie.marvel.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class GenericViewBindingAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listItems: List<T>

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Int) -> ViewBinding

    init {
        listItems = emptyList()
    }

    fun setItems(listItems: List<T>) {
        this.listItems = listItems
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return listItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(
            bindingInflater.invoke(LayoutInflater.from(parent.context), parent, viewType)
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Binder<T>).bind(listItems[position])
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutId(position, listItems[position])
    }

    fun isEmpty() = itemCount == 0

    protected abstract fun getLayoutId(position: Int, obj: T): Int

    abstract fun getViewHolder(binding: ViewBinding): RecyclerView.ViewHolder

    internal interface Binder<T> {
        fun bind(data: T)
    }
}