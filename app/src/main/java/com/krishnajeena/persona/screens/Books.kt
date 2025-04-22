package com.krishnajeena.persona.screens

import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.krishnajeena.persona.GetCustomContents
import com.krishnajeena.persona.R
import com.krishnajeena.persona.model.BooksViewModel
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import kotlinx.coroutines.launch
import java.io.File


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun BooksScreen() {

    val context = LocalContext.current
    val booksViewModel = BooksViewModel(context)

    var isWebOpen by remember { mutableStateOf(false) }


    val bookList by booksViewModel.pdfList.observeAsState(emptyList())
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = GetCustomContents(isMultiple = true),
        onResult = { uris ->
            uris.forEach{
                booksViewModel.savePdfToAppDirectory(context,  it)
            }

        }
    )

    Scaffold(modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

pdfPickerLauncher.launch(("application/pdf"))

                },
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                modifier = Modifier.padding(bottom = 86.dp)
            ) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = null)
            }
        }, floatingActionButtonPosition = FabPosition.Center
        ){ _ ->
Column(modifier = Modifier
    .fillMaxSize()
   ){

    val navController = rememberNavController()

    NavHost(navController, "listBook"){
        composable("listBook"){
isWebOpen = false


            Column(modifier = Modifier.fillMaxSize()) {


                            val myBooksDir = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                "Persona/MyBooks"
                            )

                            val downloadedBooks = remember {
                                myBooksDir.listFiles()?.filter {
                                    it.extension == "pdf" || it.extension == "epub"
                                } ?: emptyList()
                            }

// Combine both app-added books and downloaded ones
                            val allBooks = bookList + downloadedBooks

                            if (allBooks.isEmpty()) {
                                Image(
                                    painter = painterResource(R.drawable.undraw_reading_list_re_bk72),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    alignment = Alignment.Center
                                )
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    itemsIndexed(
                                        items = allBooks,
                                        key = { _, book -> book.absolutePath }
                                    ) { _, book ->
                                        BookItem(book, booksViewModel, navController)
                                    }
                                }
                            }}

                    }

        composable("bookOpen/{bookName}",
            arguments = listOf(navArgument("bookName"){
                type = NavType.StringType
            }))
        {
            backStackEntry ->
            val bookName = backStackEntry.arguments?.getString("bookName")
            if (bookName != null) {
                PersonaPdfViewer(url = bookName)
            }
        }

        composable(
            "webView/{url}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            WebViewItem(url = backStackEntry.arguments?.getString("url") ?: "https://www.google.com/")
            isWebOpen = true
        }
    }



}

    }

}

@Composable
fun BookItem(
    name: File,
    booksViewModel: BooksViewModel,
    navController: NavController
) {

    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                         booksViewModel.removePdfFromAppDirectory(context, name.canonicalFile.toUri())
                        }
                 else -> Unit
            }
            return@rememberSwipeToDismissBoxState true
        },
        // positional threshold of 25%
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = {DismissBackground(dismissState)},
    ) {

    Card(onClick = {
navController.navigate("bookOpen/${Uri.encode(name.toUri().toString())}")

                   }, modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        shape = RoundedCornerShape(20)
        ) {

        Row(modifier = Modifier.fillMaxWidth()){

        Image(painter = painterResource(R.drawable._282), contentDescription = null,
            modifier = Modifier.weight(1f))
        Text(text = name.name, fontSize = 20.sp,
            modifier = Modifier
                .padding(10.dp)
                .weight(3f),
            textAlign = TextAlign.Start,
            maxLines = 2)

    }
    }
    }

}

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color(0xFFFFFFFF)
        else  -> Color.Transparent
    }
Card(modifier = Modifier
    .fillMaxWidth()
    .padding(4.dp),
    elevation = CardDefaults.elevatedCardElevation((-10).dp)){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "delete"
        )

    }
}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PersonaPdfViewer(modifier: Modifier = Modifier, url: String="") {

    Column(modifier = Modifier.fillMaxSize()) {
    PdfRendererViewCompose(
        url = url,
        lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    )
    }

}