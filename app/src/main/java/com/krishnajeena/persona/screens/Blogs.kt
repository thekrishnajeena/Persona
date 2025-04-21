package com.krishnajeena.persona.screens

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.webkit.URLUtil
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.BlogUrl
import com.krishnajeena.persona.model.BlogUrlViewModel
import kotlinx.coroutines.launch

import androidx.core.content.ContextCompat
import com.krishnajeena.persona.other.DownloadCompleteReceiver
import java.io.File
import java.net.URLConnection


@RequiresApi(Build.VERSION_CODES.R)

@Composable
fun StudyScreen() {
    var showBottomSheet by remember { mutableStateOf(false) }
    var isWebOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { _ ->

        NavHost(
            navController = navController,
            startDestination = "blogs",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("blogs") {
                isWebOpen = false
                val tabTitles = listOf("My Blogs", "My Notes", "Books")
                val pagerState = rememberPagerState(initialPage = 0,
                    pageCount = { tabTitles.size })
                val coroutineScope = rememberCoroutineScope()

                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(title) }
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        when (page) {
                            0 -> MyBlogsSection(
                                setShowBottomSheet = { showBottomSheet = it },
                                isWebOpen = isWebOpen,
                                navController = navController,
                                context = context
                            )
                            1 -> NotesScreen()
                            2-> BooksScreen()
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBlogsSection(
    setShowBottomSheet: (Boolean) -> Unit,
    isWebOpen: Boolean,
    navController: NavHostController,
    context: Context
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    Scaffold(  floatingActionButton = {
        if (!isWebOpen) {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    },
        floatingActionButtonPosition = FabPosition.Center) { innerPadding ->

    val blogUrlViewModel = hiltViewModel<BlogUrlViewModel>()

    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { setShowBottomSheet(false) }
        ) {
            var blogName by remember { mutableStateOf("") }
            var blogUrl by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = blogName,
                    onValueChange = { blogName = it },
                    label = { Text("Blog Name") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = blogUrl,
                    onValueChange = { blogUrl = it },
                    label = { Text("Blog Url") }
                )
                Spacer(modifier = Modifier.height(15.dp))

                OutlinedButton(
                    onClick = {
                        if (blogName.isNotBlank() && blogUrl.isNotBlank()) {
                            val formattedUrl = formatUrl(blogUrl)
                            if (formattedUrl != null) {
                                try {
                                    blogUrlViewModel.addUrl(blogName, formattedUrl)
                                    Toast.makeText(context, "Blog is added!", Toast.LENGTH_SHORT).show()
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                                }
                            } else Toast.makeText(context, "Wrong Url!", Toast.LENGTH_SHORT).show()

                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) setShowBottomSheet(false)
                            }
                        } else {
                            Toast.makeText(context, "Fields can't be empty!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Add Blog", fontFamily = FontFamily.SansSerif, fontSize = 15.sp)
                }
            }
        }
    }

    if (blogUrlViewModel.isEmpty()) {
        Image(painter = painterResource(R.drawable.undraw_blog_post_re_fy5x), contentDescription = null)
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(blogUrlViewModel.urls) { item ->
                BlogsItem(
                    item = item,
                    blogUrlViewModel = blogUrlViewModel,
                    navController = navController
                )
            }
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

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun WebViewItem(url: String) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val webView = remember { WebView(context) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(
            factory = {
                webView.apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                            hasError = false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                        ) {
                            if (request?.isForMainFrame == true) {
                                isLoading = false
                                hasError = true
                            }
                        }
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        cacheMode = WebSettings.LOAD_NO_CACHE
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                    }

                    // Handle downloads to Downloads/Persona/MyBooks
                    setDownloadListener { downloadUrl, _, contentDisposition, mimeType, _ ->
                        val fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType)

                        // Ensure only necessary permission is requested (Scoped storage or WRITE_EXTERNAL_STORAGE)
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED) {

                            // Define your app-specific download folder
                            val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let {
                                File(it, "Persona/MyBooks")
                            }

                            // Make sure the directory exists
                            if (downloadDir?.exists() != true) {
                                val success = downloadDir?.mkdirs()
                                if (success == true) {
                                    Log.d("Download", "Folder created successfully.")
                                } else {
                                    Log.e("Download", "Failed to create folder.")
                                }
                            }

                            // Guess mime type and file name
                            val resolvedMimeType = mimeType ?: URLConnection.guessContentTypeFromName(downloadUrl) ?: "application/pdf"
                            val guessedFileName = URLUtil.guessFileName(downloadUrl, contentDisposition, resolvedMimeType)

                            val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                                setTitle(guessedFileName)
                                setDescription("Downloading book...")
                                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                                // Save to app-specific folder using Scoped Storage
                                downloadDir?.let { dir ->
                                    setDestinationInExternalFilesDir(
                                        context,
                                        Environment.DIRECTORY_DOWNLOADS,
                                        "Persona/MyBooks/$guessedFileName"
                                    )
                                }

                                setMimeType(resolvedMimeType)
                                setAllowedOverMetered(true)
                                setAllowedOverRoaming(true)
                            }

                            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            val downloadId = downloadManager.enqueue(request)

                            DownloadCompleteReceiver.downloadId = downloadId
                            DownloadCompleteReceiver.expectedFileName = guessedFileName
                            DownloadCompleteReceiver.expectedMimeType = resolvedMimeType

                            ContextCompat.registerReceiver(
                                context,
                                DownloadCompleteReceiver,
                                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                                ContextCompat.RECEIVER_NOT_EXPORTED
                            )

                            Toast.makeText(context, "Downloading $guessedFileName", Toast.LENGTH_SHORT).show()
                        } else {
                            // Request permission to write to storage only when needed
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1001 // Your request code here
                            )
                        }
                    }

                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator()
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
                        hasError = false
                        isLoading = true
                        webView.reload()
                    }
                ) {
                    Text("Retry")
                }
            }
        }
    }
}
