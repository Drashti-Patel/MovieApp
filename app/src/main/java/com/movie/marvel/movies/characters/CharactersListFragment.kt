package com.movie.marvel.movies.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.movie.marvel.R
import com.movie.marvel.application.MovieApplication
import com.movie.marvel.base.BaseFragment
import com.movie.marvel.base.GenericViewBindingAdapter
import com.movie.marvel.base.PaginationListener
import com.movie.marvel.databinding.FragmentMovieListBinding
import com.movie.marvel.databinding.MovieListItemBinding
import com.movie.marvel.movies.ListItemViewHolder
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.*

class CharactersListFragment :
    BaseFragment<FragmentMovieListBinding>(FragmentMovieListBinding::inflate) {

    private lateinit var characterListAdapter: GenericViewBindingAdapter<Movies>
    private val movieListViewModel: CharacterListViewModel by viewModels { CharacterViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharacterList()
        movieListViewModel.getCharacters()
        observeCharacterList()
        setupSearch()
    }

    private fun setupCharacterList() {
        characterListAdapter = object : GenericViewBindingAdapter<Movies>() {
            override val bindingInflater: (LayoutInflater, ViewGroup?, Int) -> ViewBinding
                get() = { inflater, parent, _ ->
                    MovieListItemBinding.inflate(inflater, parent, false)
                }

            override fun getLayoutId(position: Int, obj: Movies): Int {
                return R.layout.movie_list_item
            }

            override fun getViewHolder(binding: ViewBinding): RecyclerView.ViewHolder {
                return when (binding) {
                    is MovieListItemBinding -> ListItemViewHolder(binding)
                    else -> throw IllegalArgumentException("Unknown ViewBinding")
                }
            }
        }

        binding.movieListRv.apply {

            layoutManager = GridLayoutManager(MovieApplication.getContext(), 2)
            adapter = characterListAdapter

            addOnScrollListener(object :
                PaginationListener(binding.movieListRv.layoutManager as GridLayoutManager) {
                override fun isLoading(): Boolean {
                    return binding.progressBar.isVisible
                }

                override fun isLastPage(): Boolean {
                    return movieListViewModel.isLastPage()
                }

                override fun loadMoreItems() {
                    movieListViewModel.getNextPageCharacters()
                }
            })
        }
    }

    private fun observeCharacterList() {
        movieListViewModel.charactersListData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is UIResponse.Loading -> binding.progressBar.show()
                is UIResponse.Error -> {
                    binding.progressBar.setGone()
                    onError(response)
                }
                is UIResponse.Data<List<Movies>> -> {
                    binding.progressBar.setGone()
                    showCharacters(response.data)
                }
            }
        })
    }

    private fun showCharacters(charactersList: List<Movies>) {
        binding.movieListEmptyView.visibility = charactersList.isEmpty().toViewVisibility()
        binding.movieListRv.visibility = charactersList.isNotEmpty().toViewVisibility()
        characterListAdapter.setItems(charactersList)
    }

    private fun setupSearch() {
        binding.searchView.show()
        binding.searchView.queryHint = getString(R.string.search_character_hint)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                val searchQuery = binding.searchView.query?.toString()
                if (searchQuery != null) {
                    movieListViewModel.searchCharacters(searchQuery)
                } else {
                    showError(getString(R.string.search_keyword_error))
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    movieListViewModel.clearSearch()
                    return true
                }
                return false
            }
        })
    }

    private fun onError(response: UIResponse.Error) {
        showError(response.error.status ?: getString(R.string.error_unspecified))
    }
}
