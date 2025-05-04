package br.com.coisasdecasa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun Ofertas(url: String) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0) }

    val webView = remember {
        createWebView(context, url, onProgressChanged = { newProgress ->
            progress = newProgress
            isLoading = newProgress < 100
        })
    }

    WebViewContent(webView, isLoading, progress)

    HandleBackButton(webView)
}

@Composable
private fun WebViewContent(webView: WebView, isLoading: Boolean, progress: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray
                )
            }

            AndroidView(
                factory = { webView },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HandleBackButton(webView: WebView) {
    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
}

private fun createWebView(
    context: Context,
    url: String,
    onProgressChanged: (Int) -> Unit
): WebView {
    return WebView(context).apply {
        configureSettings(context)
        configureClients(onProgressChanged)
        loadUrl(url)
    }
}

private fun WebView.configureSettings(context: Context) {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        setDatabaseEnabled(true)
        cacheMode = WebSettings.LOAD_DEFAULT
        mediaPlaybackRequiresUserGesture = false
        userAgentString = "Mozilla/5.0 (Linux; Android 11; Mobile; rv:96.0) Gecko/96.0 Firefox/96.0"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }
    setLayerType(View.LAYER_TYPE_HARDWARE, null)
}

private fun WebView.configureClients(onProgressChanged: (Int) -> Unit) {
    webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            onProgressChanged(newProgress)
        }
    }

    webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest
        ): Boolean {
            val url = request.url.toString()

            val externalSchemes = listOf("whatsapp:", "mailto:", "tel:", "intent:", "market:", "tg:", "geo:")

            if (externalSchemes.any { url.startsWith(it) }) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                    return true
                } catch (e: Exception) {
                    return true
                }
            }

            if (url.contains("instagram.com") || url.contains("youtube.com") || url.contains("shopee")) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                    return true
                } catch (e: Exception) {
                    return false
                }
            }

            return false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}