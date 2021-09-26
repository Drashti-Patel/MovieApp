package com.movie.marvel.movies.comics

import androidx.recyclerview.widget.RecyclerView
import com.movie.marvel.base.GenericViewBindingAdapter
import com.movie.marvel.databinding.MovieListItemBinding
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.DEFAULT_TITLE
import com.squareup.picasso.Picasso

class ComicListItemViewHolder (private val binding: MovieListItemBinding) :
    RecyclerView.ViewHolder(binding.root), GenericViewBindingAdapter.Binder<Movies> {

    override fun bind(data: Movies) {
            Picasso.get().load(data.thumbnail.getUrl()).into(binding.movieImage)
            binding.movieTitle.text = data.title ?: DEFAULT_TITLE
    }

}