package com.abbie.fast_tray

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abbie.fast_tray.ui.theme.FasttrayTheme
import com.abbie.fast_tray.viewmodels.MainViewModel
import com.abbie.fast_tray.views.RoleSelectionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = remember { MainViewModel() }

            FasttrayTheme {
                Box(modifier = Modifier.padding(0.dp)) {
                    RoleSelectionScreen(
                        viewModel = viewModel,
                        onNavigateToStudent = {},
                        onNavigateToOwner = {},
                        onNavigateToAdmin = {}
                    )
                }
            }
        }
    }
}