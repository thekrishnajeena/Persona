package com.krishnajeena.persona.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.krishnajeena.persona.data_layer.BlogItem
import com.krishnajeena.persona.model.ArticlesViewModel
import com.krishnajeena.persona.model.ExploreViewModel

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (List<BlogItem>, String) -> Unit,
    navController: NavHostController
) {
    val viewModel: ExploreViewModel = hiltViewModel()
    val articlesViewModel: ArticlesViewModel = viewModel()

    val categories = viewModel.categories
    val isConnected by viewModel.isConnected.collectAsState()
    val categoriesArticles = viewModel.articlesCategories

    val loading = viewModel.isLoading
    val selected = articlesViewModel.selectedCategory
    val articles = articlesViewModel.articles
    val isLoading = viewModel.isLoading

    val context = LocalContext.current



    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Storage permission is required to access media files", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_AUDIO
            }
            else -> {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {

            if (!isConnected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You're offline. Please connect to the internet.",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            else {

                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    item(0) {
                        Text(
                            "Blogs",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp),
                            fontWeight = FontWeight.SemiBold
                        )

                        if (loading) {

                            Row{
                                repeat(5) {

                                    ShimmerEffect(
                                        modifier = Modifier.padding(16.dp)
                                            .size(120.dp)
                                            .background(
                                                Color.LightGray,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    )
                                    }
                                }



                        } else {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(categories) { category ->
                                    BlogsCategoryItem(
                                        title = category.name,
                                        image = category.image,
                                        onClick = { onCategoryClick(category.blogs, category.name) }
                                    )
                                }
                            }
                        }
                    }

                    item(1) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categoriesArticles) { category ->
                                val isSelected = category == selected
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) Color.Blue else Color.LightGray)
                                        .clickable { articlesViewModel.onCategoryClick(category) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(category.capitalize(), color = Color.White)
                                }
                            }
                        }
                    }

                    if (isLoading) {
                        item(2) {

                            repeat(5) {
                                Column(modifier = Modifier) {

                                    ShimmerEffect(
                                        modifier = Modifier.padding(
                                            vertical = 8.dp,
                                            horizontal = 6.dp
                                        )
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .background(
                                                Color.LightGray,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    )
                                }
                            }
                        }
                    } else {
                        items(articles) { article ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 6.dp)
                                    .clickable {
                                        val encodedUrl = Uri.encode(article.url)
                                        navController.navigate("webview/$encodedUrl")
                                    }
                            ) {
                                Column {
                                    article.cover_image?.let { imageUrl ->
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(article.title, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(4.dp))
                                        Text(article.description ?: "", maxLines = 2)
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {


    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 1.0f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
    }


}

@Composable
fun BlogsCategoryItem(
    modifier: Modifier = Modifier,
    title: String,
    image: String,
    onClick: () -> Unit
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Card(modifier = Modifier.size(120.dp)) {
            if (image.isNotBlank()) {
                AsyncImage(model = image, contentDescription = title, contentScale = ContentScale.Crop)
            }
        }
        Text(title, modifier = Modifier.padding(2.dp), style = MaterialTheme.typography.bodyMedium)
    }
}
