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
import com.movie.marvel.movies.comics.ComicListViewModel
import com.movie.marvel.movies.model.FilterComicsByDate
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

@Suppress("UNCHECKED_CAST")
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class ComicListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var comicsResponseObserver: Observer<UIResponse<List<Movies>>>

    @Mock
    private lateinit var movieListRepository: MovieListRepository

    private lateinit var viewModel: ComicListViewModel

    @Mock
    lateinit var lifeCycleOwner: LifecycleOwner

    private lateinit var lifeCycle: LifecycleRegistry

    @Captor
    private var captor: ArgumentCaptor<UIResponse.Data<List<Movies>>> = TestExtensions.mock()

    @Captor
    private var errorCaptor: ArgumentCaptor<UIResponse.Error> = TestExtensions.mock()

    private val mockGetComicsListFlow = MutableSharedFlow<ApiResponse<Movies>>()

    private val comicsResponse = TestResponseFactory.jsonFileToResponse(
        "FetchComicsOkResponse.json",
        object : TypeToken<List<Movies>>() {}.type
    ) as List<Movies>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(dispatcher)

        viewModel = ComicListViewModel(movieListRepository)

        lifeCycle = LifecycleRegistry(lifeCycleOwner)
        Mockito.`when`(lifeCycleOwner.lifecycle).thenReturn(lifeCycle)
        lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        viewModel.comicListData.observe(lifeCycleOwner, comicsResponseObserver)
        Mockito.`when`(movieListRepository.getComics(0, null)).thenReturn(mockGetComicsListFlow)

        viewModel.fetchComics()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Loading object should be returned`() {
        assert(viewModel.comicListData.hasObservers())
        Mockito.verify(comicsResponseObserver).onChanged(UIResponse.Loading)
    }

    @Test
    fun `Get Error object should be returned in an error case`() = runBlockingTest {
        val errorMessage = ApiResponse<Movies>(
            code = 401,
            status = "Unauthorized"
        )
        mockGetComicsListFlow.emit(errorMessage)
        Mockito.verify(comicsResponseObserver, Mockito.times(2))
            .onChanged(errorCaptor.capture())

        Assert.assertEquals(errorMessage.status, errorCaptor.value.error.status)
    }


    @Test
    fun `List of comics should be returned in an success case`() = runBlockingTest {
        mockGetComicsListFlow.emit(
            ApiResponse(
                code = 200,
                status = "ok",
                data = ApiData(total = 20, results = comicsResponse)
            )
        )
        Mockito.verify(comicsResponseObserver, Mockito.times(2)).onChanged(captor.capture())
        Assert.assertEquals(3, captor.value.data.size)
    }

    @Test
    fun `List of comics should be filtered as per options`() {
        val searchResponse = ApiResponse(
            code = 200,
            status = "ok",
            data = ApiData(total = 20, results = comicsResponse)
        )
        whenever(movieListRepository.getComics(0, "lastWeek"))
            .thenReturn(flowOf(searchResponse))
        Mockito.verify(comicsResponseObserver, Mockito.times(1)).onChanged(captor.capture())

        viewModel.filterComicsBy(FilterComicsByDate.LAST_WEEK)
        Mockito.verify(comicsResponseObserver, Mockito.times(3)).onChanged(captor.capture())
        Assert.assertEquals(3, captor.value.data.size)
    }
}