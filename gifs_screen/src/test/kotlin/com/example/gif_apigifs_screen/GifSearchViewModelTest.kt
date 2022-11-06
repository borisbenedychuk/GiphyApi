package com.example.gif_apigifs_screen

import com.example.gif_api.domain.gif.model.GifModel
import com.example.gif_api.domain.gif.usecase.AddToBlacklistUseCase
import com.example.gif_api.domain.gif.usecase.Pager
import com.example.gif_api.domain.utils.errorResult
import com.example.gif_api.domain.utils.successResult
import com.example.gif_api.gifs_screen.models.gif.BoundSignal
import com.example.gif_api.gifs_screen.models.gif.GifSearchAction
import com.example.gif_api.gifs_screen.models.gif.ListPositionInfo
import com.example.gif_api.gifs_screen.models.gif.asItem
import com.example.gif_api.gifs_screen.ui.gif.GifSearchViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class GifSearchViewModelTest {

    private fun models(pages: Int = 3): List<GifModel> = List(PAGE_SIZE * pages) {
        GifModel(
            id = it.toString(),
            originalUrl = "url$it",
            smallUrl = "smallUrl$it",
            title = it.toString(),
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `test new query action`() = runTest {
        val viewModel = mockedViewModel(
            pager = mock {
                val flow = MutableStateFlow(emptyList<GifModel>())
                on { pagesFlow } doReturn flow
                onBlocking { newPages(QUERY1, 0) } doAnswer {
                    flow.tryEmit(models().take(PAGE_SIZE * 2))
                    successResult(true)
                }
            }
        )
        viewModel.handleAction(GifSearchAction.NewQuery(QUERY1))
        val state = viewModel.gifSearchState.drop(1).first()
        assertThat(state.query).isEqualTo(QUERY1)
        assertThat(state.items).isEqualTo(models().take(PAGE_SIZE * 2).map { it.asItem() })
        assertThat(state.loading).isFalse()
        assertThat(state.showFooter).isTrue()
    }

    @Test
    fun `test navigation actions`() = runTest {
        val viewModel = mockedViewModel()
        viewModel.gifSearchState.take(3).collectIndexed { i, state ->
            when (i) {
                0 -> {
                    assertThat(state.isDetailsScreen).isFalse()
                    viewModel.handleAction(GifSearchAction.NavigateToPager(ListPositionInfo()))
                }
                1 -> {
                    assertThat(state.isDetailsScreen).isTrue()
                    viewModel.handleAction(GifSearchAction.NavigateToList)
                }
                2 -> {
                    assertThat(state.isDetailsScreen).isFalse()
                }
            }
        }
    }

    @Test
    fun `test new current item action`() = runTest {
        val viewModel = mockedViewModel(
            pager = mock { on { pagesFlow } doReturn flowOf(models()) }
        )
        viewModel.handleAction(GifSearchAction.NewCurrentItem(TARGET_ITEM_ID))
        val state = viewModel.gifSearchState.drop(1).first()
        assertThat(state.currentIndex).isEqualTo(TARGET_ITEM_ID.toIntOrNull())
        assertThat(state.listPositionInfo.itemId).isEqualTo(TARGET_ITEM_ID)
    }

    @Test
    fun `test delete item action`() = runTest {
        val flow = MutableStateFlow(models())
        val viewModel = mockedViewModel(
            pager = mock { on { pagesFlow } doReturn flow },
            addToBlacklistUseCase = mock {
                onBlocking { invoke(TARGET_ITEM_ID) } doAnswer {
                    flow.update { list -> list.filterNot { it.id == TARGET_ITEM_ID } }
                }
            }
        )
        viewModel.handleAction(GifSearchAction.DeleteItem(TARGET_ITEM_ID))
        viewModel.gifSearchState.take(3).collectIndexed { i, state ->
            when (i) {
                1 -> {
                    assertThat(state.items).isNotEmpty()
                    assertThat(state.items.find { it.id == TARGET_ITEM_ID }).isNotNull()
                }
                2 -> {
                    assertThat(state.items).hasSize(PAGE_SIZE * 3 - 1)
                    assertThat(state.items.find { it.id == TARGET_ITEM_ID }).isNull()
                }
            }
        }
    }

    @Test
    fun `test bounds reached actions`() = runTest {
        val models = models(pages = 4)
        val items = models.map { it.asItem() }
        val flow = MutableStateFlow(emptyList<GifModel>())
        val viewModel = mockedViewModel(
            pager = mock {
                on { pagesFlow } doReturn flow
                onBlocking { newPages(eq(QUERY1), any()) } doAnswer {
                    val requestPage = it.getArgument<Int>(1)
                    val result = when (requestPage) {
                        0 -> models.take(PAGE_SIZE * 2)
                        2 -> models.take(PAGE_SIZE * 3)
                        3 -> models.drop(PAGE_SIZE)
                        else -> throw AssertionError("Invalid request page: $requestPage")
                    }
                    val isListFinished = requestPage != 3
                    flow.tryEmit(result)
                    successResult(isListFinished)
                }
            }
        )
        viewModel.handleAction(GifSearchAction.NewQuery(QUERY1))
        val result = viewModel.gifSearchState.drop(1).first()
        assertThat(result.items).hasSize(PAGE_SIZE * 2)
        assertThat(result.showFooter).isTrue()
        assertThat(result.query).isEqualTo(QUERY1)
        viewModel.handleAction(GifSearchAction.BoundsReached(BoundSignal.BOTTOM_REACHED))
        val result2 = viewModel.gifSearchState.drop(1).first()
        assertThat(result2.items).hasSize(PAGE_SIZE * 3)
        assertThat(result2.items).isEqualTo(items.take(PAGE_SIZE * 3))
        assertThat(result2.showFooter).isTrue()
        viewModel.handleAction(GifSearchAction.BoundsReached(BoundSignal.BOTTOM_REACHED))
        val result3 = viewModel.gifSearchState.drop(1).first()
        assertThat(result3.items).hasSize(PAGE_SIZE * 3)
        assertThat(result3.items).isEqualTo(items.drop(PAGE_SIZE))
        assertThat(result3.showFooter).isFalse()
        viewModel.handleAction(GifSearchAction.BoundsReached(BoundSignal.TOP_REACHED))
        val result4 = viewModel.gifSearchState.drop(1).first()
        assertThat(result4.items).hasSize(PAGE_SIZE * 3)
        assertThat(result4.items).isEqualTo(items.take(PAGE_SIZE * 3))
        assertThat(result4.showFooter).isTrue()
    }

    @Test
    fun `test retry action`() = runTest {
        val models = models()
        val items = models.map { it.asItem() }
        var counter = 1
        val flow = MutableStateFlow(emptyList<GifModel>())
        val viewModel = mockedViewModel(
            pager = mock {
                on { pagesFlow } doReturn flow
                onBlocking { newPages(eq(QUERY1), any()) } doAnswer {
                    val requestPage = it.getArgument<Int>(1)
                    val result = when (requestPage) {
                        0 -> models.take(PAGE_SIZE * 2)
                        2 -> {
                            if (counter != 0) {
                                counter--
                                return@doAnswer errorResult(ERROR_MSG)
                            } else {
                                models
                            }
                        }
                        else -> throw AssertionError("Invalid request page: $requestPage")
                    }
                    flow.tryEmit(result)
                    successResult(true)
                }
            }
        )
        viewModel.handleAction(GifSearchAction.NewQuery(QUERY1))
        val result = viewModel.gifSearchState.drop(1).first()
        assertThat(result.items).hasSize(PAGE_SIZE * 2)
        assertThat(result.showFooter).isTrue()
        assertThat(result.query).isEqualTo(QUERY1)
        viewModel.handleAction(GifSearchAction.BoundsReached(BoundSignal.BOTTOM_REACHED))
        val result2 = viewModel.gifSearchState.drop(1).first()
        assertThat(result2.items).hasSize(PAGE_SIZE * 2)
        assertThat(result2.items).isEqualTo(items.take(PAGE_SIZE * 2))
        assertThat(result2.showFooter).isTrue()
        assertThat(result2.errorMsg).isEqualTo(ERROR_MSG)
        viewModel.handleAction(GifSearchAction.RetryLoad)
        val result3 = viewModel.gifSearchState.drop(1).first()
        assertThat(result3.items).hasSize(PAGE_SIZE * 3)
        assertThat(result3.items).isEqualTo(items)
        assertThat(result3.showFooter).isTrue()
        assertThat(result3.errorMsg).isNull()
    }

    private fun mockedViewModel(
        pager: Pager = mock { on { pagesFlow } doReturn flowOf() },
        addToBlacklistUseCase: AddToBlacklistUseCase = mock()
    ) = GifSearchViewModel(pager, addToBlacklistUseCase)

    companion object {
        private const val QUERY1 = "query_1"
        private const val PAGE_SIZE = 25
        private const val TARGET_ITEM_ID = "50"
        private const val ERROR_MSG = "error"
    }
}