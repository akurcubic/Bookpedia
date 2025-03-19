package com.plcoding.bookpedia.book.presentation.book_details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.bookpedia.book.presentation.book_details.components.BlurredImageBackground

@Composable
fun BookDetailsScreenRoot(

    viewModel: BookDetailsViewModel,
    onBackClick: () -> Unit,
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookDetailScreen(
        state = state,
        onAction = {
            action ->
            when(action){

                is BookDetailsAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun BookDetailScreen(

    state: BookDetailsState,
    onAction: (BookDetailsAction) -> Unit
){
    BlurredImageBackground(
        imageUrl = state.book?.imageUrl,
        isFavorite = state.isFavorite,
        onFavoriteClick = {
            onAction(BookDetailsAction.OnFavoriteClick)
        },
        onBackCLick = {
            onAction(BookDetailsAction.OnBackClick)
        },
        modifier = Modifier.fillMaxSize()
    ){

    }
}