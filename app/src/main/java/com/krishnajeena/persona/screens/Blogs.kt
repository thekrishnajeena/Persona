package com.krishnajeena.persona.screens

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.BlogUrl
import com.krishnajeena.persona.model.BlogUrlViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogsScreen(
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var isWebOpen by remember {mutableStateOf(false)}

    Scaffold(modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (!isWebOpen) {
                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }, floatingActionButtonPosition = FabPosition.Center
    ){_ ->

        val blogUrlViewModel = hiltViewModel<BlogUrlViewModel>()

        val context = LocalContext.current
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "blogUrls", modifier = Modifier.fillMaxSize()){

        composable("blogUrls"){
            isWebOpen = false
            if(showBottomSheet){
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val scope = rememberCoroutineScope()

                ModalBottomSheet(sheetState = sheetState,
                    onDismissRequest = {
                        showBottomSheet = false
                    }  ) {

                    var blogName by remember{ mutableStateOf("") }
                    var blogUrl by remember{ mutableStateOf("")}

                    Column(modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
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
                                val formattedUrl = formatUrl(blogUrl)
                                if (formattedUrl != null){
                                    try {
                                        blogUrlViewModel.addUrl(blogName, blogUrl)
                                        Toast.makeText(
                                            context,
                                            "Blog is added!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (_: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Something went wrong!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else Toast.makeText(context, "Wrong Url!", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion { if(!sheetState.isVisible)
                                showBottomSheet = false}
                        } else Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Add Blog", modifier = Modifier,
                                fontFamily = FontFamily.SansSerif, fontSize = 15.sp)
                        }
                    }
                }

            }

            if(blogUrlViewModel.isEmpty()){
                Image(painter = painterResource(R.drawable.undraw_blog_post_re_fy5x), contentDescription = null)
            }
else{
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            items(blogUrlViewModel.urls) { item ->

                BlogsItem(
                    item = item,
                    blogUrlViewModel = blogUrlViewModel,
                    navController = navController,
                )
            }

        }
        }
        }

            composable("webView/{url}",
                arguments = listOf(navArgument("url") { type = NavType.StringType
                nullable = true})){
                        backStackEntry ->
                WebViewItem(url =  backStackEntry.arguments?.getString("url") ?: "https://www.google.com/")
                isWebOpen = true

            }

        }
    }

}

fun formatUrl(url: String): String? {
    // Regex to validate URL
    val regex = "^(http://|https://|)(www\\.)?([a-zA-Z0-9]+\\.[a-zA-Z]{2,})(/.*)?$".toRegex()

    return if (url.isNotEmpty()) {
        // Check if URL already contains http or https
        val formattedUrl = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("www.") -> "https://$url"
            else -> "https://$url"
        }

        // Check if the formatted URL matches the regex
        if (regex.matches(formattedUrl)) {
            formattedUrl
        } else {
            null // Invalid URL
        }
    } else {
        null // Empty URL
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogsItem(
    blogUrlViewModel: BlogUrlViewModel,
    item: BlogUrl,
    navController: NavController
) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    blogUrlViewModel.removeUrl(item)
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
    enableDismissFromStartToEnd = true,
    enableDismissFromEndToStart = false,
    modifier = Modifier,
    backgroundContent = { DismissBackground(dismissState) }
) {
    Card(modifier = Modifier.padding(5.dp),
        onClick = {

            navController.navigate("webView/${Uri.encode(item.url)}")

                  },
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        shape = RoundedCornerShape(10.dp)
    )   {
        Column(modifier = Modifier.fillMaxWidth().padding(5.dp)
            , horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = item.name, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = item.url, fontSize = 12.sp)
        }
    }
}
}
@Composable
fun WebViewItem(url: String) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            hasError = false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

                        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                            super.onReceivedError(view, request, error)
                            isLoading = false
                            hasError = true
                        }
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        cacheMode = WebSettings.LOAD_NO_CACHE
                        domStorageEnabled = true
                    }

                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (hasError) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Failed to load the page")
                Button(
                    onClick = {
                        isLoading = true
                        hasError = false
                        context.findComponentActivity()?.recreate()
                    }
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

fun Context.findComponentActivity(): ComponentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    return null
}
