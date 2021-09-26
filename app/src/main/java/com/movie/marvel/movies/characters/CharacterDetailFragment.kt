package com.movie.marvel.movies.characters

import android.os.Bundle
import android.view.View
import com.movie.marvel.base.BaseFragment
import com.movie.marvel.databinding.FragmentCharacterDetailBinding
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.DEFAULT_TITLE
import com.movie.marvel.utils.MOVIE_DATA
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CharacterDetailFragment :
    BaseFragment<FragmentCharacterDetailBinding>(FragmentCharacterDetailBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val characterInfo : Movies? =  arguments?.getParcelable(MOVIE_DATA)

        characterInfo?.let { character ->
            Picasso.get().load(character.thumbnail.getUrl()).into(binding.imgCharacter)
            character.description?.let {
                binding.characterDescription.text = it
            }
            binding.characterName.text = character.name ?: DEFAULT_TITLE
        }
    }

}

