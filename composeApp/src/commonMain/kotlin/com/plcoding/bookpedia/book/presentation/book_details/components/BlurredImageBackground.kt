package com.plcoding.bookpedia.book.presentation.book_details.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.plcoding.bookpedia.core.presentation.DarkBlue
import com.plcoding.bookpedia.core.presentation.DesertWhite
import com.plcoding.bookpedia.core.presentation.SandYellow
import com.plcoding.bookpedia.resources.Res
import com.plcoding.bookpedia.resources.book_cover
import com.plcoding.bookpedia.resources.book_error_2
import com.plcoding.bookpedia.resources.go_back
import com.plcoding.bookpedia.resources.mark_as_favorite
import com.plcoding.bookpedia.resources.remove_from_favorites
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlurredImageBackground(

    imageUrl: String?,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackCLick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){

    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }

    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        onSuccess = { result ->
            imageLoadResult = if (result.painter.intrinsicSize.width > 1 && result.painter.intrinsicSize.height > 1) {
                Result.success(result.painter)
            } else {
                Result.failure(Exception("Invalid image size"))
            }
        },
        onError = {
            it.result.throwable.printStackTrace()
            imageLoadResult = Result.failure(it.result.throwable)
        }
    )

    Box(modifier = Modifier){
        Column (modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .background(DarkBlue)
            ) {
                imageLoadResult?.getOrNull()?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = stringResource(Res.string.book_cover),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().blur(20.dp)
                    )
                }

            }

            Box(
                modifier = Modifier.weight(0.7f).fillMaxWidth().background(DesertWhite)
            )
        }
        IconButton(
            onClick = onBackCLick,
            modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp, start = 16.dp).statusBarsPadding()
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.go_back),
                tint = Color.White
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
            Spacer(modifier = Modifier.fillMaxHeight(0.15F))
            ElevatedCard(
                modifier = Modifier.height(270.dp).aspectRatio(2 / 3f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.elevatedCardColors(contentColor = Color.Transparent),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 15.dp
                )
            ){
                AnimatedContent(
                    targetState = imageLoadResult
                ){result ->
                    when(result){
                        null -> CircularProgressIndicator()
                        else -> {
                            Box{
                                Image(
                                    painter = if(result.isSuccess == true) painter else{
                                        painterResource(Res.drawable.book_error_2)
                                    },
                                    contentDescription = stringResource(Res.string.book_cover),
                                    modifier = Modifier.fillMaxSize().background(Color.Transparent),
                                    contentScale = if(result.isSuccess){
                                        ContentScale.Crop
                                    }else{
                                        ContentScale.Fit
                                    }
                                )
                                IconButton(
                                    onClick = onFavoriteClick,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(brush = Brush.radialGradient(
                                            colors = listOf(
                                                SandYellow, Color.Transparent
                                            ),
                                            radius = 70f
                                        ))
                                ){
                                    Icon(
                                        imageVector = if(isFavorite){
                                            Icons.Filled.Favorite
                                        }else{
                                            Icons.Outlined.FavoriteBorder
                                        },
                                        tint = Color.Red,
                                        contentDescription = if(isFavorite){
                                            stringResource(Res.string.remove_from_favorites)
                                        }else{
                                            stringResource(Res.string.mark_as_favorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            content()
        }
    }
}