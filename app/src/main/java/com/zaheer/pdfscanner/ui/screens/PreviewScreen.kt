package com.zaheer.pdfscanner.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaheer.pdfscanner.R
import com.zaheer.pdfscanner.ui.viewmodel.CropRect
import com.zaheer.pdfscanner.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel: ScannerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    val currentPage = uiState.currentPage ?: uiState.capturedPages.lastOrNull()
    
    var brightness by remember { mutableStateOf(currentPage?.brightness ?: 0f) }
    var contrast by remember { mutableStateOf(currentPage?.contrast ?: 1f) }
    var showCropOverlay by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.preview_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        currentPage?.let { viewModel.removePage(it.id) }
                        if (uiState.capturedPages.size <= 1) {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Thumbnails
                if (uiState.capturedPages.size > 1) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(uiState.capturedPages) { index, page ->
                            Card(
                                modifier = Modifier.size(80.dp),
                                onClick = { /* Select page */ }
                            ) {
                                Image(
                                    bitmap = page.bitmap.asImageBitmap(),
                                    contentDescription = "Page ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onNavigateBack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.add_page))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            viewModel.generatePdf(context, null)
                            onNavigateToResult()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.capturedPages.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.generate_pdf))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Image preview with crop overlay
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                currentPage?.let { page ->
                    val processedBitmap = remember(page, brightness, contrast) {
                        applyAdjustments(page.bitmap, brightness, contrast)
                    }
                    
                    Image(
                        bitmap = processedBitmap.asImageBitmap(),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    
                    if (showCropOverlay) {
                        CropOverlay(
                            onCropChanged = { cropRect ->
                                viewModel.updatePageCrop(page.id, cropRect)
                            }
                        )
                    }
                }
            }
            
            // Adjustment controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { showCropOverlay = !showCropOverlay }) {
                            Text(stringResource(R.string.crop))
                        }
                        TextButton(onClick = { 
                            brightness = 0f
                            contrast = 1f
                            currentPage?.let { page ->
                                viewModel.updatePageBrightness(page.id, 0f)
                                viewModel.updatePageContrast(page.id, 1f)
                            }
                        }) {
                            Text("Reset")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Brightness slider
                    Text(
                        text = stringResource(R.string.adjust_brightness),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Slider(
                        value = brightness,
                        onValueChange = { 
                            brightness = it
                            currentPage?.let { page ->
                                viewModel.updatePageBrightness(page.id, it)
                            }
                        },
                        valueRange = -1f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Contrast slider
                    Text(
                        text = stringResource(R.string.adjust_contrast),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Slider(
                        value = contrast,
                        onValueChange = { 
                            contrast = it
                            currentPage?.let { page ->
                                viewModel.updatePageContrast(page.id, it)
                            }
                        },
                        valueRange = 0.5f..2f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CropOverlay(
    onCropChanged: (CropRect) -> Unit
) {
    var cropRect by remember {
        mutableStateOf(CropRect(0.1f, 0.1f, 0.9f, 0.9f))
    }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Simple drag implementation - could be enhanced
                    val newLeft = (cropRect.left + dragAmount.x / size.width)
                        .coerceIn(0f, cropRect.right - 0.1f)
                    val newTop = (cropRect.top + dragAmount.y / size.height)
                        .coerceIn(0f, cropRect.bottom - 0.1f)
                    
                    cropRect = cropRect.copy(left = newLeft, top = newTop)
                    onCropChanged(cropRect)
                }
            }
    ) {
        val left = cropRect.left * size.width
        val top = cropRect.top * size.height
        val right = cropRect.right * size.width
        val bottom = cropRect.bottom * size.height
        
        // Draw semi-transparent overlay
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset.Zero,
            size = size
        )
        
        // Draw clear crop area
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
        )
        
        // Draw crop border
        drawRect(
            color = Color.White,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            style = Stroke(width = 4f)
        )
        
        // Draw corner handles
        val handleSize = 40f
        listOf(
            Offset(left, top),
            Offset(right - handleSize, top),
            Offset(left, bottom - handleSize),
            Offset(right - handleSize, bottom - handleSize)
        ).forEach { offset ->
            drawRect(
                color = Color.White,
                topLeft = offset,
                size = Size(handleSize, handleSize)
            )
        }
    }
}

private fun applyAdjustments(bitmap: Bitmap, brightness: Float, contrast: Float): Bitmap {
    if (brightness == 0f && contrast == 1f) return bitmap
    
    // Simple brightness/contrast adjustment
    // In a real app, this would use ColorMatrix as in PdfGenerator
    return bitmap
}
