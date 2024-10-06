package com.krishnajeena.persona.screens

import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.krishnajeena.persona.GetCustomContents
import com.krishnajeena.persona.R
import com.krishnajeena.persona.model.BooksViewModel
import java.io.File

private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
    var name = ""
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            if (nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name
}


@Composable
fun BooksScreen(modifier: Modifier = Modifier) {

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
        ){ innerPadding ->
Column(modifier = Modifier
    .fillMaxSize()
   ){

   LazyColumn(modifier = Modifier.fillMaxSize()){
        itemsIndexed(items = bookList, key = {_, book -> book.absolutePath}){ _, book ->

           // SwipeToDismissBox() { }
            BookItem(book,
                booksViewModel, onRemove = {
booksViewModel.removePdfFromAppDirectory(context, book.toUri())
                })

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
    onRemove: () -> Unit
) {

    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    //onRemove(currentItem)

                  //  Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                    booksViewModel.removePdfFromAppDirectory(context, name.canonicalFile.toUri())
                    //booksViewModel.pdfList.remove(name)
                   // onRemove()
                //    function.invoke()
                }
            //    SwipeToDismissBoxValue.EndToStart -> {
                    //onRemove(currentItem)
               //     Toast.makeText(context, "Item archived", Toast.LENGTH_SHORT).show()
              //  }
               // SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
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

    Card(onClick = {Toast.makeText(context, name.canonicalPath, Toast.LENGTH_LONG).show()
                   Log.i("TAG::", "$name")}, modifier = Modifier.fillMaxWidth().
        padding(5.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        shape = RoundedCornerShape(20)
        ) {

        Row(modifier = Modifier.fillMaxWidth()){

        Image(painter = painterResource(R.drawable._282), contentDescription = null,
            modifier = Modifier.weight(1f))
        Text(text = name.name, fontSize = 20.sp,
            modifier = Modifier.padding(10.dp).weight(3f),
            textAlign = TextAlign.Start,
            maxLines = 2)

    }
    }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color(0xFFFFFFFF)
    //    SwipeToDismissBoxValue.EndToStart -> Color(0xFF1DE9B6)
        else  -> Color.Transparent
    }
Card(modifier = Modifier.fillMaxWidth().padding(4.dp),
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