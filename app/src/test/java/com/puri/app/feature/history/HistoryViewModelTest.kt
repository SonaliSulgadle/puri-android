package com.puri.app.feature.history

import app.cash.turbine.test
import com.puri.app.core.common.Resource
import com.puri.app.domain.usecase.DeleteHistoryItemUseCase
import com.puri.app.domain.usecase.GetHistoryUseCase
import com.puri.app.util.TestFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("HistoryViewModel")
class HistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getHistoryUseCase: GetHistoryUseCase = mockk()
    private val deleteHistoryItemUseCase: DeleteHistoryItemUseCase = mockk()

    private lateinit var viewModel: HistoryViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { getHistoryUseCase() } returns flowOf(emptyList())
        coEvery { deleteHistoryItemUseCase(any()) } returns Resource.Success(Unit)

        viewModel = HistoryViewModel(
            getHistoryUseCase = getHistoryUseCase,
            deleteHistoryItemUseCase = deleteHistoryItemUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("initial state")
    inner class InitialState {

        @Test
        @DisplayName("shows Empty state when history has no items")
        fun showsEmptyWhenNoHistory() = runTest {
            coEvery { getHistoryUseCase() } returns flowOf(emptyList())
            viewModel = HistoryViewModel(getHistoryUseCase, deleteHistoryItemUseCase)
            advanceUntilIdle()
            assertThat(viewModel.uiState.value).isEqualTo(HistoryUiState.Empty)
        }

        @Test
        @DisplayName("transitions to Empty when history is empty")
        fun transitionsToEmptyWithNoHistory() = runTest {
            coEvery { getHistoryUseCase() } returns flowOf(emptyList())
            viewModel = HistoryViewModel(getHistoryUseCase, deleteHistoryItemUseCase)

            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isEqualTo(HistoryUiState.Empty)
        }

        @Test
        @DisplayName("transitions to Content when history has items")
        fun transitionsToContentWithItems() = runTest {
            coEvery { getHistoryUseCase() } returns flowOf(
                listOf(TestFixtures.historyItemToday, TestFixtures.historyItemYesterday)
            )
            viewModel = HistoryViewModel(getHistoryUseCase, deleteHistoryItemUseCase)

            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(HistoryUiState.Content::class.java)
        }
    }

    @Nested
    @DisplayName("content state")
    inner class ContentState {

        @BeforeEach
        fun setupWithItems() {
            coEvery { getHistoryUseCase() } returns flowOf(
                listOf(TestFixtures.historyItemToday, TestFixtures.historyItemYesterday)
            )
            viewModel = HistoryViewModel(getHistoryUseCase, deleteHistoryItemUseCase)
        }

        @Test
        @DisplayName("groups items by TODAY and YESTERDAY")
        fun groupsItemsByDate() = runTest {
            advanceUntilIdle()

            val state = viewModel.uiState.value as HistoryUiState.Content
            assertThat(state.groupedItems.keys).contains("TODAY")
            assertThat(state.groupedItems.keys).contains("YESTERDAY")
        }

        @Test
        @DisplayName("TODAY group contains today's item")
        fun todayGroupContainsTodaysItem() = runTest {
            advanceUntilIdle()

            val state = viewModel.uiState.value as HistoryUiState.Content
            val todayItems = state.groupedItems["TODAY"]
            assertThat(todayItems).isNotNull
            assertThat(todayItems!!.first().id).isEqualTo(TestFixtures.historyItemToday.id)
        }
    }

    @Nested
    @DisplayName("search")
    inner class Search {

        @BeforeEach
        fun setupWithItems() {
            coEvery { getHistoryUseCase() } returns flowOf(
                listOf(TestFixtures.historyItemToday, TestFixtures.historyItemYesterday)
            )
            viewModel = HistoryViewModel(getHistoryUseCase, deleteHistoryItemUseCase)
        }

        @Test
        @DisplayName("filters items matching search query")
        fun filtersItemsMatchingQuery() = runTest {
            advanceUntilIdle()

            // historyItemToday has category TRASH ("Plastic Bin")
            viewModel.onIntent(HistoryIntent.SearchQueryChanged("Plastic"))
            advanceUntilIdle()

            val state = viewModel.uiState.value as HistoryUiState.Content
            val allFiltered = state.filteredItems.values.flatten()
            assertThat(allFiltered).hasSize(1)
        }

        @Test
        @DisplayName("empty query restores all items")
        fun emptyQueryRestoresAllItems() = runTest {
            advanceUntilIdle()

            viewModel.onIntent(HistoryIntent.SearchQueryChanged("Plastic"))
            viewModel.onIntent(HistoryIntent.ClearSearch)
            advanceUntilIdle()

            val state = viewModel.uiState.value as HistoryUiState.Content
            assertThat(state.searchQuery).isEmpty()
            assertThat(state.filteredItems).isEqualTo(state.groupedItems)
        }

        @Test
        @DisplayName("no-match query returns empty filtered items")
        fun noMatchQueryReturnsEmpty() = runTest {
            advanceUntilIdle()

            viewModel.onIntent(
                HistoryIntent.SearchQueryChanged("xyznotfound")
            )
            advanceUntilIdle()

            val state = viewModel.uiState.value as HistoryUiState.Content
            assertThat(state.filteredItems.values.flatten()).isEmpty()
        }
    }

    @Nested
    @DisplayName("delete item")
    inner class DeleteItem {

        @Test
        @DisplayName("calls deleteHistoryItemUseCase with correct id")
        fun callsDeleteWithCorrectId() = runTest {
            viewModel.onIntent(HistoryIntent.DeleteItem(42L))
            advanceUntilIdle()

            coVerify { deleteHistoryItemUseCase(42L) }
        }
    }

    @Nested
    @DisplayName("view item")
    inner class ViewItem {

        @Test
        @DisplayName("emits NavigateToResult effect with correct item")
        fun emitsNavigateEffect() = runTest {
            coEvery { getHistoryUseCase() } returns flowOf(
                listOf(TestFixtures.historyItemToday)
            )
            viewModel = HistoryViewModel(getHistoryUseCase, deleteHistoryItemUseCase)
            advanceUntilIdle()

            viewModel.effects.test {
                viewModel.onIntent(HistoryIntent.ViewItem(TestFixtures.historyItemToday.id))
                advanceUntilIdle()

                val effect = awaitItem()
                assertThat(effect).isInstanceOf(HistoryUiEffect.NavigateToResult::class.java)
                assertThat((effect as HistoryUiEffect.NavigateToResult).item.id)
                    .isEqualTo(TestFixtures.historyItemToday.id)

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        @DisplayName("does nothing when item id not found")
        fun doesNothingWhenItemNotFound() = runTest {
            advanceUntilIdle()

            viewModel.effects.test {
                viewModel.onIntent(HistoryIntent.ViewItem(999L))
                advanceUntilIdle()

                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}