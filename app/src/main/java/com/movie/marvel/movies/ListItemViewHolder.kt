package com.movie.marvel.movies

import androidx.recyclerview.widget.RecyclerView
import com.movie.marvel.base.GenericViewBindingAdapter
import com.movie.marvel.databinding.MovieListItemBinding
import com.movie.marvel.movies.model.Movies
import com.squareup.picasso.Picasso

class ListItemViewHolder(private val binding: MovieListItemBinding) :
    RecyclerView.ViewHolder(binding.root), GenericViewBindingAdapter.Binder<Movies> {

    override fun bind(data: Movies) {
        itemView.apply {
            Picasso.get().load(data.thumbnail.getUrl()).into(binding.movieImage)
            binding.movieTitle.text = data.name ?: data.title
        }
    }

}