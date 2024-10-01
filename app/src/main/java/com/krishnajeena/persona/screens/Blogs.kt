package com.krishnajeena.persona.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krishnajeena.persona.ui_layer.BlogUrlViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogsScreen(modifier: Modifier = Modifier,

) {

    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                  showBottomSheet = true
                },
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = null)
            }
        }, floatingActionButtonPosition = FabPosition.Center
    ){innerPadding ->

        val blogUrlViewModel = hiltViewModel<BlogUrlViewModel>()
        //val state by blogUrlViewModel.state.collectAsState()

        var selectedBlog by remember{mutableStateOf("https://www.google.com")}
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "blogUrls"){

        composable("blogUrls"){
            if(showBottomSheet){
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val scope = rememberCoroutineScope()

                ModalBottomSheet(sheetState = sheetState,
                    onDismissRequest = {
                        showBottomSheet = false
                    }  ) {

                    var blogName by remember{ mutableStateOf("") }
                    var blogUrl by remember{ mutableStateOf("")}

                    Column(modifier = Modifier.padding(5.dp).fillMaxWidth(),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally){
                        OutlinedTextField(value = blogName, onValueChange =
                        {blogName = it}, label = { Text("Blog Name") },
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = blogUrl, onValueChange =
                        {blogUrl = it}, label = { Text("Blog Url") },
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.height(15.dp))

                        OutlinedButton(onClick = {
                            if(blogName.isNotBlank() && blogUrl.isNotBlank()) {
                             blogUrlViewModel.addUrl(blogName, blogUrl)
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion { if(!sheetState.isVisible)
                                showBottomSheet = false}
                        }
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Add Blog", modifier = Modifier,
                                fontFamily = FontFamily.SansSerif, fontSize = 15.sp)
                        }
                    }
                }

            }

        LazyColumn(modifier = Modifier.fillMaxSize()){

            items(blogUrlViewModel.urls){
                item ->

                Card(modifier = Modifier.padding(5.dp),
                    onClick = {
                    selectedBlog = item.url
                    navController.navigate("webView") },
                    elevation = CardDefaults.elevatedCardElevation(10.dp),
                    shape = RoundedCornerShape(10.dp)
                ){
                Column(modifier = Modifier.fillMaxWidth().padding(5.dp)
                , horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = item.name, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = item.url, fontSize = 12.sp)
                }
            }
            }


        }
        }

            composable("webView"){

                WebViewItem(url = selectedBlog)
            }

        }
    }

}

@Composable
fun WebViewItem(modifier: Modifier = Modifier, url: String) {

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())){
        AndroidView(
            factory = {
                    context ->
                WebView(context).apply{
                    clearCache(true)
                    clearHistory()
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            }, modifier = Modifier.fillMaxSize()
        )
    }
}
