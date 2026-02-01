package com.zaheer.pdfscanner.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri

data class ScannerUiState(
    val capturedPages: List<ScannedPage> = emptyList(),
    val currentPage: ScannedPage? = null,
    val isPro: Boolean = false,
    val scansRemainingToday: Int = 5,
    val generatedPdfUri: Uri? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

data class ScannedPage(
    val id: String,
    val bitmap: Bitmap,
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val cropRect: CropRect? = null
)

data class CropRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)
