package com.movie.marvel.movies.characters

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.movie.marvel.api.ApiError
import com.movie.marvel.movies.MovieListRepository
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.PAGE_LIMIT
import com.movie.marvel.utils.UIResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CharacterListViewModel @ViewModelInject constructor(private val movieListRepo: MovieListRepository) :
    ViewModel() {

    var charactersListData: MediatorLiveData<UIResponse<List<Movies>>> = MediatorLiveData()
    private var charactersArrayList = arrayListOf<Movies>()
    private var currentPage = 0
    private var lastPage = 0
    private var searchParam: String? = null

    fun getCharacters(nameStartsWith: String? = null): MediatorLiveData<UIResponse<List<Movies>>> {
        charactersListData.value = UIResponse.Loading
        if (nameStartsWith != null && nameStartsWith.isNotEmpty()) {
            searchParam = nameStartsWith
            charactersArrayList.clear()
        }
        charactersListData.addSource(
            movieListRepo.getCharacters(currentPage, searchParam).asLiveData()
        ) { response ->

            if (response.code != 200) {
                charactersListData.value = UIResponse.Error(
                    ApiError(
                        code = response.code?.toString(),
                        status = response.status
                    )
                )
            } else {
                response?.let { characters ->
                    characters.data?.let {
                        lastPage = it.total!!
                    }
                    charactersArrayList.addAll(characters.data?.results as ArrayList<Movies>)
                    charactersListData.value = UIResponse.Data(data = charactersArrayList)
                }
            }
        }
        return charactersListData
    }

    fun getNextPageCharacters() {
        currentPage += PAGE_LIMIT
        getCharacters()
    }

    fun isLastPage(): Boolean {
        return currentPage == lastPage
    }

    @SuppressLint("DefaultLocale")
    fun searchCharacters(searchQuery: String) {
        charactersArrayList.clear()
        getCharacters(searchQuery)
    }

    fun clearSearch() {
        charactersArrayList.clear()
        searchParam = null
        currentPage = 0
        getCharacters()
    }
}