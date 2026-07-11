package com.hopkins.fitlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hopkins.fitlink.core.ble.FitBLE
import com.hopkins.fitlink.nav.Nav
import com.hopkins.fitlink.ui.theme.FitLinkTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var fitBLE: FitBLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitLinkTheme {
                Nav()
            }
        }
    }
}
