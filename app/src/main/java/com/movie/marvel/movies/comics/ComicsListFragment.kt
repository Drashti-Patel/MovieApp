package com.movie.marvel.movies.comics

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.movie.marvel.R
import com.movie.marvel.base.BaseFragment
import com.movie.marvel.base.GenericViewBindingAdapter
import com.movie.marvel.base.PaginationListener
import com.movie.marvel.databinding.FragmentMovieListBinding
import com.movie.marvel.databinding.MovieListItemBinding
import com.movie.marvel.movies.model.FilterComicsByDate
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ComicsListFragment :
    BaseFragment<FragmentMovieListBinding>(FragmentMovieListBinding::inflate) {

    lateinit var comicListViewModel: ComicListViewModel
    private lateinit var comicListAdapter: GenericViewBindingAdapter<Movies>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comicListViewModel =
            ViewModelProvider(requireActivity()).get(ComicListViewModel::class.java)
        setupComicList()
        comicListViewModel.fetchComics()
        observeComicList()
    }

    private fun setupComicList() {
        comicListAdapter = object : GenericViewBindingAdapter<Movies>() {
            override fun getLayoutId(position: Int, obj: Movies): Int {
                return R.layout.movie_list_item
            }

            override fun getViewHolder(binding: ViewBinding): RecyclerView.ViewHolder {
                return when (binding) {
                    is MovieListItemBinding -> ComicListItemViewHolder(binding)
                    else -> throw IllegalArgumentException("Unknown ViewBinding")
                }
            }

            override val bindingInflater: (LayoutInflater, ViewGroup?, Int) -> ViewBinding
                get() = { inflater, parent, _ ->
                    MovieListItemBinding.inflate(inflater, parent, false)
                }

        }

        binding.movieListRv.apply {

            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = comicListAdapter

            addOnScrollListener(object :
                PaginationListener(binding.movieListRv.layoutManager as GridLayoutManager) {
                override fun isLoading(): Boolean {
                    return binding.progressBar.isVisible
                }

                override fun isLastPage(): Boolean {
                    return comicListViewModel.isLastPage()
                }

                override fun loadMoreItems() {
                    comicListViewModel.getNextPageComics()
                }
            })
        }
    }

    private fun observeComicList() {
        comicListViewModel.comicListData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is UIResponse.Loading -> binding.progressBar.show()
                is UIResponse.Error -> {
                    binding.progressBar.setGone()
                    onError(response)
                }
                is UIResponse.Data<List<Movies>> -> {
                    binding.progressBar.setGone()
                    showComics(response.data)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.released_last_week -> filterComics(FilterComicsByDate.LAST_WEEK)
            R.id.released_this_week -> filterComics(FilterComicsByDate.THIS_WEEK)
            R.id.releasing_next_week -> filterComics(FilterComicsByDate.NEXT_WEEK)
            R.id.release_this_month -> filterComics(FilterComicsByDate.THIS_MONTH)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showComics(comicList: List<Movies>) {
        binding.movieListEmptyView.visibility = comicList.isEmpty().toViewVisibility()
        binding.movieListRv.visibility = comicList.isNotEmpty().toViewVisibility()
        comicListAdapter.setItems(comicList)
    }

    private fun filterComics(filterBy: FilterComicsByDate) {
        comicListViewModel.filterComicsBy(filterByDate = filterBy)
    }

    private fun onError(response: UIResponse.Error) {
        showError(response.error.status ?: getString(R.string.error_unspecified))
    }
}