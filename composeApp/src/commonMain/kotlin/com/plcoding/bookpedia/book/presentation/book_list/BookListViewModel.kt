package com.plcoding.bookpedia.book.presentation.book_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookListViewModel(
    private val bookRepository: BookRepository
): ViewModel() {

    private val _state = MutableStateFlow(BookListState())
    val state = _state
        .onStart {
            if(cachedBooks.isEmpty()){
                observeSearchQuery()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )


    //presentation -> domain <- data
    // presenatation ima pristup domain-u
    // data ima pristup domainu
    /*
    Ovo pravilo znači:

Presentation može videti Domain, ali ne zna ništa o Data sloju.
Data zna za Domain, ali Domain ne zna ništa o Data sloju.
Domain je najvažniji i ne zavisi ni od čega drugog.
Zašto je ovo korisno?
✔ Odvajanje UI logike od poslovne logike → Možeš lako menjati UI bez menjanja poslovne logike.
✔ Odvajanje poslovne logike od podataka → Možeš lako menjati bazu podataka ili API bez menjanja poslovne logike.
✔ Testabilnost → Domain sloj možeš testirati nezavisno od UI i podataka.
✔ Održavanje i skalabilnost → Lako menjaš pojedinačne slojeve bez uticaja na ostale.
    */


    private var cachedBooks = emptyList<Book>()
    private var searchJob: Job? = null

    fun onAction(action: BookListAction){

        when(action){

            is BookListAction.OnBookClick -> {

            }
            is BookListAction.OnSearchQueryChange -> {

                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }
            is BookListAction.OnTabSelected -> {

                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery(){

        state.map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when{
                    query.isBlank() -> {
                        _state.update { it.copy(
                            errorMessage = null,
                            searchResult = cachedBooks
                        ) }
                    }
                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchBooks(query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchBooks(query: String) = viewModelScope.launch{
        _state.update { it.copy(
            isLoading = true
        )}
        viewModelScope.launch {
            bookRepository.searchBooks(query)
                .onSuccess {
                searchResults ->
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = null,
                    searchResult = searchResults
                ) }
                }
                .onError { error ->
                    _state.update { it.copy(
                        searchResult = emptyList(),
                        isLoading = false,
                        errorMessage = error.toUiText()
                    ) }
                }
        }
    }
}