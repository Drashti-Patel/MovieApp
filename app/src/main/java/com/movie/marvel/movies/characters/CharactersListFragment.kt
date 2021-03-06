package com.movie.marvel.movies.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.movie.marvel.R
import com.movie.marvel.base.BaseFragment
import com.movie.marvel.base.GenericViewBindingAdapter
import com.movie.marvel.base.PaginationListener
import com.movie.marvel.databinding.FragmentMovieListBinding
import com.movie.marvel.databinding.MovieListItemBinding
import com.movie.marvel.movies.model.Movies
import androidx.navigation.fragment.findNavController
import com.movie.marvel.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CharactersListFragment :
    BaseFragment<FragmentMovieListBinding>(FragmentMovieListBinding::inflate) {

    private lateinit var characterListAdapter: GenericViewBindingAdapter<Movies>
    lateinit var movieListViewModel: CharacterListViewModel
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        movieListViewModel = ViewModelProvider(requireActivity()).get(CharacterListViewModel::class.java)
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
                    is MovieListItemBinding -> CharacterListItemViewHolder(
                        binding,
                        onItemClick = {
                            navController.navigate(
                                R.id.navigation_character_detail,
                                bundleOf(
                                    MOVIE_DATA to it
                                )
                            )
                        })
                    else -> throw IllegalArgumentException("Unknown ViewBinding")
                }
            }
        }

        binding.movieListRv.apply {

            layoutManager = GridLayoutManager(requireContext(), 2)
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
