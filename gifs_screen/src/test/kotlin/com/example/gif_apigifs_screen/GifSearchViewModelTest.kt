package com.example.gif_apigifs_screen

import com.example.gif_api.domain.gif.model.GifModel
import com.example.gif_api.domain.gif.usecase.AddToBlacklistUseCase
import com.example.gif_api.domain.gif.usecase.Pager
import com.example.gif_api.domain.utils.successResult
import com.example.gif_apigifs_screen.presentation.models.gif.GifSearchAction
import com.example.gif_apigifs_screen.presentation.models.gif.ListPositionInfo
import com.example.gif_apigifs_screen.presentation.models.gif.asItem
import com.example.gif_apigifs_screen.presentation.ui.gif.GifSearchViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class GifSearchViewModelTest {

    private val models: List<GifModel>
        get() = List(PAGE_SIZE * 3) {
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
                onBlocking { newPages(QUERY1, 0) } doSuspendableAnswer {
                    flow.emit(models.take(PAGE_SIZE * 2))
                    successResult(true)
                }
            }
        )
        viewModel.handleAction(GifSearchAction.NewQuery(QUERY1))
        val state = viewModel.gifSearchState.drop(1).first()
        assertThat(state.query).isEqualTo(QUERY1)
        assertThat(state.items).isEqualTo(models.take(PAGE_SIZE * 2).map { it.asItem() })
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
            pager = mock { on { pagesFlow } doReturn flowOf(models) }
        )
        viewModel.handleAction(GifSearchAction.NewCurrentItem(TARGET_ITEM_ID))
        val state = viewModel.gifSearchState.drop(1).first()
        assertThat(state.currentIndex).isEqualTo(TARGET_ITEM_ID.toIntOrNull())
        assertThat(state.listPositionInfo.itemId).isEqualTo(TARGET_ITEM_ID)
    }

    @Test
    fun `test delete item action`() = runTest {
        val flow = MutableStateFlow(models)
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

    private fun mockedViewModel(
        pager: Pager = mock { on { pagesFlow } doReturn flowOf() },
        addToBlacklistUseCase: AddToBlacklistUseCase = mock()
    ) = GifSearchViewModel(pager, addToBlacklistUseCase)

    companion object {
        private const val QUERY1 = "query_1"
        private const val PAGE_SIZE = 25
        private const val TARGET_ITEM_ID = "50"
    }
}