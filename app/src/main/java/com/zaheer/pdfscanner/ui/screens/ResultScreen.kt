package com.zaheer.pdfscanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaheer.pdfscanner.R
import com.zaheer.pdfscanner.ui.viewmodel.ScannerViewModel
import com.zaheer.pdfscanner.utils.FileUtils

@Composable
fun ResultScreen(
    viewModel: ScannerViewModel,
    onNavigateToCamera: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(stringResource(R.string.result_screen_title)) }
            )
        }
    ) { paddingValues ->
        if (uiState.isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = stringResource(R.string.save_success),
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!uiState.isPro) {
                    Text(
                        text = "Watermark applied to free version",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    text = "${uiState.capturedPages.size} page(s) saved",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            uiState.generatedPdfUri?.let { uri ->
                                FileUtils.sharePdf(context, uri)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.generatedPdfUri != null
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.share_pdf))
                    }
                    
                    Button(
                        onClick = {
                            viewModel.resetScanner()
                            onNavigateToCamera()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("New Scan")
                    }
                }
            }
        }
    }
}
