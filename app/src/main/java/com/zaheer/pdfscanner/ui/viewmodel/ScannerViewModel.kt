package com.zaheer.pdfscanner.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaheer.pdfscanner.billing.BillingManager
import com.zaheer.pdfscanner.domain.PdfGenerator
import com.zaheer.pdfscanner.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()
    
    private val preferencesManager = PreferencesManager(application)
    private val pdfGenerator = PdfGenerator(application)
    val billingManager = BillingManager(application)
    
    init {
        loadProStatus()
        loadScansRemaining()
        
        viewModelScope.launch {
            billingManager.isPro.collect { isPro ->
                _uiState.update { it.copy(isPro = isPro) }
            }
        }
    }
    
    private fun loadProStatus() {
        val isPro = preferencesManager.isPro()
        _uiState.update { it.copy(isPro = isPro) }
    }
    
    private fun loadScansRemaining() {
        if (!_uiState.value.isPro) {
            val remaining = preferencesManager.getScansRemainingToday()
            _uiState.update { it.copy(scansRemainingToday = remaining) }
        }
    }
    
    fun addScannedPage(bitmap: Bitmap) {
        if (!_uiState.value.isPro && _uiState.value.scansRemainingToday <= 0) {
            _uiState.update { it.copy(errorMessage = "Daily scan limit reached") }
            return
        }
        
        val page = ScannedPage(
            id = UUID.randomUUID().toString(),
            bitmap = bitmap
        )
        
        _uiState.update { state ->
            val newPages = state.capturedPages + page
            val newRemaining = if (!state.isPro) {
                val remaining = state.scansRemainingToday - 1
                preferencesManager.setScansRemainingToday(remaining)
                remaining
            } else {
                state.scansRemainingToday
            }
            state.copy(
                capturedPages = newPages,
                currentPage = page,
                scansRemainingToday = newRemaining
            )
        }
    }
    
    fun updatePageBrightness(pageId: String, brightness: Float) {
        _uiState.update { state ->
            val updatedPages = state.capturedPages.map { page ->
                if (page.id == pageId) page.copy(brightness = brightness) else page
            }
            state.copy(capturedPages = updatedPages)
        }
    }
    
    fun updatePageContrast(pageId: String, contrast: Float) {
        _uiState.update { state ->
            val updatedPages = state.capturedPages.map { page ->
                if (page.id == pageId) page.copy(contrast = contrast) else page
            }
            state.copy(capturedPages = updatedPages)
        }
    }
    
    fun updatePageCrop(pageId: String, cropRect: CropRect) {
        _uiState.update { state ->
            val updatedPages = state.capturedPages.map { page ->
                if (page.id == pageId) page.copy(cropRect = cropRect) else page
            }
            state.copy(capturedPages = updatedPages)
        }
    }
    
    fun reorderPages(fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val mutablePages = state.capturedPages.toMutableList()
            val page = mutablePages.removeAt(fromIndex)
            mutablePages.add(toIndex, page)
            state.copy(capturedPages = mutablePages)
        }
    }
    
    fun removePage(pageId: String) {
        _uiState.update { state ->
            state.copy(capturedPages = state.capturedPages.filter { it.id != pageId })
        }
    }
    
    fun generatePdf(context: Context, fileName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            
            try {
                val pages = _uiState.value.capturedPages
                val addWatermark = !_uiState.value.isPro
                val highQuality = _uiState.value.isPro
                
                val uri = pdfGenerator.generatePdf(
                    pages = pages,
                    fileName = fileName,
                    addWatermark = addWatermark,
                    highQuality = highQuality
                )
                
                _uiState.update { 
                    it.copy(
                        generatedPdfUri = uri,
                        isProcessing = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "Failed to generate PDF",
                        isProcessing = false
                    ) 
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun resetScanner() {
        _uiState.update { 
            ScannerUiState(
                isPro = it.isPro,
                scansRemainingToday = if (it.isPro) 5 else preferencesManager.getScansRemainingToday()
            ) 
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        billingManager.endConnection()
    }
}
