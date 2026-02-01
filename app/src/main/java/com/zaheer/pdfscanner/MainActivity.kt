package com.zaheer.pdfscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.zaheer.pdfscanner.ui.navigation.NavigationGraph
import com.zaheer.pdfscanner.ui.theme.PDFScannerTheme
import com.zaheer.pdfscanner.ui.viewmodel.ScannerViewModel

class MainActivity : ComponentActivity() {
    
    private val viewModel: ScannerViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            PDFScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavigationGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
