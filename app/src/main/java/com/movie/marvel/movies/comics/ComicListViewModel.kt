package com.movie.marvel.movies.comics

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.movie.marvel.api.ApiError
import com.movie.marvel.movies.MovieListRepository
import com.movie.marvel.movies.model.FilterComicsByDate
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.*

class ComicListViewModel(private val movieListRepo: MovieListRepository) : ViewModel() {

    var comicListData: MediatorLiveData<UIResponse<List<Movies>>> = MediatorLiveData()
    private var comicArrayList = arrayListOf<Movies>()
    private var currentPage = 0
    private var lastPage = 0
    private var selectedFilter: String? = null

    fun fetchComics(){
        getComics()
    }

    private fun getComics(filterByDate: String? = null): MediatorLiveData<UIResponse<List<Movies>>> {
        comicListData.value = UIResponse.Loading
        if(filterByDate != null) {
            currentPage = 0
            selectedFilter = filterByDate
            comicArrayList.clear()
        }
        comicListData.addSource(
            movieListRepo.getComics(currentPage,selectedFilter).asLiveData()
        ) { response ->

            if (response.code != 200) {
                comicListData.value = UIResponse.Error(
                    ApiError(
                        code = response.code?.toString(),
                        status = response.status
                    )
                )
            } else {
                response?.let { comics ->
                    comics.data?.let {
                        lastPage = it.total!!
                    }
                    comicArrayList.addAll(comics.data?.results as ArrayList<Movies>)
                    comicListData.value = UIResponse.Data(data = comicArrayList)
                }
            }
        }
        return comicListData
    }

    fun getNextPageComics() {
        currentPage += PAGE_LIMIT
        getComics()
    }

    fun isLastPage(): Boolean {
        return currentPage == lastPage
    }

    fun filterComicsBy(filterByDate : FilterComicsByDate) {
        when(filterByDate){
            FilterComicsByDate.LAST_WEEK -> getComics(STR_LAST_WEEK)
            FilterComicsByDate.THIS_WEEK -> getComics(STR_THIS_WEEK)
            FilterComicsByDate.NEXT_WEEK -> getComics(STR_NEXT_WEEK)
            FilterComicsByDate.THIS_MONTH -> getComics(STR_THIS_MONTH)
        }
    }
}