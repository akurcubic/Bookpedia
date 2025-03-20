package com.plcoding.bookpedia.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.plcoding.bookpedia.book.presentation.SelectedBookViewModel
import com.plcoding.bookpedia.book.presentation.book_details.BookDetailsAction
import com.plcoding.bookpedia.book.presentation.book_details.BookDetailsScreenRoot
import com.plcoding.bookpedia.book.presentation.book_details.BookDetailsViewModel
import com.plcoding.bookpedia.book.presentation.book_list.BookListScreenRoot
import com.plcoding.bookpedia.book.presentation.book_list.BookListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {

    MaterialTheme {

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.BookGraph
        ){
            navigation<Route.BookGraph>(
                startDestination = Route.BookList
            ){
                composable<Route.BookList>(
                    exitTransition = { slideOutHorizontally ()},
                    popEnterTransition = {
                        slideInHorizontally()
                    }
                ) {

                    val viewModel = koinViewModel<BookListViewModel>()
                    val selectedBookViewModel = it.sharedKoinViewModel<SelectedBookViewModel>(navController)

                    LaunchedEffect(true){
                        selectedBookViewModel.onSelectedBook(null)
                    }

                    BookListScreenRoot(
                        viewModel = viewModel,
                        onBookClick = {book ->
                            selectedBookViewModel.onSelectedBook(book)
                            navController.navigate(Route.BookDetail(book.id))
                        }
                    )
                }
                composable<Route.BookDetail>(
                    enterTransition = { slideInHorizontally{
                        initialOffset -> initialOffset
                    }},
                    exitTransition = { slideOutHorizontally{
                            initialOffset -> initialOffset
                    }}
                ) {

                    val selectedBookViewModel = it.sharedKoinViewModel<SelectedBookViewModel>(navController)
                    val selectedBook by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()
                    val viewModel = koinViewModel<BookDetailsViewModel>()

                    LaunchedEffect(selectedBook){
                        selectedBook?.let {
                            viewModel.onAction(BookDetailsAction.OnSelectedBookChange(selectedBook!!))
                        }
                    }

                    BookDetailsScreenRoot(
                        viewModel = viewModel,
                        onBackClick = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

//NavHost je komponenta u Jetpack Navigation-u koja služi kao kontejner za navigaciju u Compose aplikaciji. On određuje početni ekran i definiše sve rute kroz koje korisnik može da se kreće.
//NavBackStackEntry – Ovo je trenutni ekran u navigacionom steku.
//navController: NavController – Navigacioni kontroler se koristi za dohvatanje informacija o navigacionom steku.

@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route?: return koinViewModel <T>()
    val parentEntry = remember (this){
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}