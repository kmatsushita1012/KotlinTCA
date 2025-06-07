package com.example.kotlin_tca

import HomeScreen
import com.example.kotlin_tca.core.tca.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.kotlin_tca.core.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = Store<HomeFeature.State, HomeFeature.Action>(
            initialState = HomeFeature.State(),
            reducer = HomeFeature,
        )
        setContent {
            AppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(store = store)
                }
            }
        }
    }
}
