package com.krishnajeena.persona.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
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
import java.io.File


@Composable
fun BooksScreen() {

    val context = LocalContext.current
    val booksViewModel = BooksViewModel(context)

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
                modifier = Modifier.padding(bottom = 16.dp)
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

            if(booksViewModel.isEmpty()){
                Image(painter = painterResource(R.drawable.undraw_reading_list_re_bk72),
                    contentDescription = null, modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center)
            }else {

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = bookList,
                        key = { _, book -> book.absolutePath }) { _, book ->

                        // SwipeToDismissBox() { }
                        BookItem(
                            book,
                            booksViewModel, navController
                        )

                    }
                }
            }
        }
        composable("bookOpen/{bookName}",
            arguments = listOf(navArgument("bookName"){
                type = NavType.StringType
            })){
            backStackEntry ->
            val bookName = backStackEntry.arguments?.getString("bookName")
            if (bookName != null) {
                PersonaPdfViewer(url = bookName)
            }
        }
    }



}

    }

}

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showSystemUi = true, showBackground = true)
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
        Toast.makeText(context, name.canonicalPath, Toast.LENGTH_LONG).show()
                   Log.i("TAG::", "$name")
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
    //    SwipeToDismissBoxValue.EndToStart -> Color(0xFF1DE9B6)
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
            .padding(12.dp, 8.dp),
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