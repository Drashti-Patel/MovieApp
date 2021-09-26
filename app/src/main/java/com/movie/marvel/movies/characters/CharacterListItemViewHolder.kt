package com.movie.marvel.movies.characters

import androidx.recyclerview.widget.RecyclerView
import com.movie.marvel.base.GenericViewBindingAdapter
import com.movie.marvel.databinding.MovieListItemBinding
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.DEFAULT_TITLE
import com.squareup.picasso.Picasso

class CharacterListItemViewHolder(private val binding: MovieListItemBinding, val onItemClick: (Movies) -> Unit) :
    RecyclerView.ViewHolder(binding.root), GenericViewBindingAdapter.Binder<Movies> {

    override fun bind(data: Movies) {
            binding.cvMovie.setOnClickListener { onItemClick(data) }
            Picasso.get().load(data.thumbnail.getUrl()).into(binding.movieImage)
            binding.movieTitle.text = data.name ?: DEFAULT_TITLE
    }

}