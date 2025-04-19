package com.krishnajeena.persona.screens

import android.net.Uri
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import com.krishnajeena.persona.data_layer.BlogItem
import com.krishnajeena.persona.model.ArticlesViewModel
import com.krishnajeena.persona.model.ExploreViewModel

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (List<BlogItem>) -> Unit,
    navController: NavHostController
) {
    val viewModel: ExploreViewModel = viewModel()

    val articlesViewModel: ArticlesViewModel = viewModel()

    val categories = viewModel.categories

    val loading = viewModel.isLoading

    val categoriesArticles = listOf("entrepreneurship", "AI", "careers", "android", "react", "ai", "webdev", "space")
    val selected = articlesViewModel.selectedCategory
    val articles = articlesViewModel.articles
    val isLoading = viewModel.isLoading


    Scaffold(modifier = modifier) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize().padding(start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding())) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item(0){
                Text("Blogs", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.SemiBold)

                if (loading) {
                    Row(modifier = Modifier.fillMaxWidth()){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterVertically))
                    }
                } else {
                    LazyRow(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(categories) { category ->
                            BlogsCategoryItem(
                                title = category.name,
                                image = category.image,
                                onClick = { onCategoryClick(category.blogs) }
                            )
                        }
                    }
                }
                }


                item(1){
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
if(isLoading){
                item(2){
                    Row(modifier = Modifier.fillMaxWidth()){
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterVertically))
                    }

            }}
else {
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
                // Show image if available
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
