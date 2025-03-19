package com.plcoding.bookpedia.book.presentation.book_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.app.Route
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailsViewModel(
    private val bookRepository: BookRepository,
    //predstavlja mapu item-a, funkcionise tako sto ce ovde biti prosledjeni argumenti koji se koriste u navigaciji
    private val savedStateHandle: SavedStateHandle
):ViewModel() {

    private val _state = MutableStateFlow(BookDetailsState())
    val state = _state
        .onStart {
            fetchBookDescription()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private val bookId = savedStateHandle.toRoute<Route.BookDetail>().id

    fun onAction(action: BookDetailsAction){

        when(action){
            is BookDetailsAction.OnSelectedBookChange -> {
                _state.update { it.copy(book = action.book) }
            }
            is BookDetailsAction.OnBackClick -> {

            }
            is BookDetailsAction.OnFavoriteClick -> {

            }
        }
    }

    private fun fetchBookDescription(){

        viewModelScope.launch {

            bookRepository.getBookDescription(bookId)
                .onSuccess { description ->
                    _state.update {
                        it.copy(
                            book = it.book?.copy(
                                description = description
                            ),
                            isLoading = false
                        )
                    }
                }
        }
    }
}