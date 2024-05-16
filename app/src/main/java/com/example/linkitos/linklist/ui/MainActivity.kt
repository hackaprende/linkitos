package com.example.linkitos.linklist.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.linkitos.models.Link
import com.example.linkitos.ui.theme.LinkitosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinkitosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LinkNavHost(
                        onLinkClicked = ::openLink
                    )
                }
            }
        }
    }

    private fun openLink(link: Link) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.short))
        startActivity(intent)
    }
}
