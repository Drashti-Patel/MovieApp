package com.movie.marvel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.google.gson.reflect.TypeToken
import com.movie.marvel.api.ApiData
import com.movie.marvel.api.ApiResponse
import com.movie.marvel.movies.MovieListRepository
import com.movie.marvel.movies.characters.CharacterListViewModel
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.UIResponse
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.*
import org.junit.Assert.assertEquals

@Suppress("UNCHECKED_CAST")
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class CharacterListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var charactersResponseObserver: Observer<UIResponse<List<Movies>>>

    @Mock
    private lateinit var movieListRepository: MovieListRepository

    private lateinit var viewModel: CharacterListViewModel

    @Mock
    lateinit var lifeCycleOwner: LifecycleOwner

    private lateinit var lifeCycle: LifecycleRegistry

    @Captor
    private var captor: ArgumentCaptor<UIResponse.Data<List<Movies>>> = TestExtensions.mock()

    @Captor
    private var errorCaptor: ArgumentCaptor<UIResponse.Error> = TestExtensions.mock()

    private val mockGetCharactersListFlow = MutableSharedFlow<ApiResponse<Movies>>()

    private val charactersResponse = TestResponseFactory.jsonFileToResponse(
        "FetchCharactersOkResponse.json",
        object : TypeToken<List<Movies>>() {}.type
    ) as List<Movies>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(dispatcher)

        viewModel = CharacterListViewModel(movieListRepository)

        lifeCycle = LifecycleRegistry(lifeCycleOwner)
        Mockito.`when`(lifeCycleOwner.lifecycle).thenReturn(lifeCycle)
        lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        viewModel.charactersListData.observe(lifeCycleOwner, charactersResponseObserver)
        Mockito.`when`(movieListRepository.getCharacters(0)).thenReturn(mockGetCharactersListFlow)

        viewModel.getCharacters()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Loading object should be returned`() {
        assert(viewModel.charactersListData.hasObservers())
        Mockito.verify(charactersResponseObserver).onChanged(UIResponse.Loading)
    }

    @Test
    fun `Error object should be returned in an error case` ()= runBlockingTest {
        val errorMessage = ApiResponse<Movies>(
            code = 401,
            status = "Unauthorized"
        )

        mockGetCharactersListFlow.emit(errorMessage)
        Mockito.verify(charactersResponseObserver, Mockito.times(2))
            .onChanged(errorCaptor.capture())

        assertEquals(errorMessage.status, errorCaptor.value.error.status)
    }

    @Test
    fun `list of characters should be returned in an success case` () = runBlockingTest {
        mockGetCharactersListFlow.emit(ApiResponse(code = 200,status = "ok",data = ApiData(total=20,results = charactersResponse)))
        Mockito.verify(charactersResponseObserver, Mockito.times(2)).onChanged(captor.capture())
        assertEquals(3, captor.value.data.size)
    }

    @Test
    fun `search returns an character with the given searchString`() {
        val searchCharacter = charactersResponse[2]
        val searchResponse =ApiResponse(code = 200,status = "ok",data = ApiData(total=20,results = charactersResponse))
        whenever(movieListRepository.getCharacters(0, searchCharacter.name?.substring(0,3)))
            .thenReturn(flowOf(searchResponse))
        Mockito.verify(charactersResponseObserver, Mockito.times(1)).onChanged(captor.capture())

        searchCharacter.name?.substring(0,3)?.let { viewModel.searchCharacters(it) }
        Mockito.verify(charactersResponseObserver, Mockito.times(3)).onChanged(captor.capture())
        assertEquals(3, captor.value.data.size)
    }

}