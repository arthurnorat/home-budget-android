package com.example.homebudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.homebudget.ui.screens.ContentScreen
import com.example.homebudget.ui.theme.HomeBudgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeBudgetTheme {
                ContentScreen()
            }
        }
    }
}
