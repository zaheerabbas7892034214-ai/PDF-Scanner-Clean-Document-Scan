package com.zaheer.pdfscanner.domain

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.zaheer.pdfscanner.ui.viewmodel.ScannedPage
import com.zaheer.pdfscanner.utils.FileUtils
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {
    
    fun generatePdf(
        pages: List<ScannedPage>,
        fileName: String? = null,
        addWatermark: Boolean = false,
        highQuality: Boolean = false
    ): Uri? {
        if (pages.isEmpty()) return null
        
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val paint = Paint()
        
        pages.forEachIndexed { index, page ->
            // Apply transformations to bitmap
            val processedBitmap = processImage(page, highQuality)
            
            // Create page
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
                processedBitmap.width,
                processedBitmap.height,
                index + 1
            ).create()
            
            val pdfPage = pdfDocument.startPage(pageInfo)
            val canvas = pdfPage.canvas
            
            // Draw the bitmap
            canvas.drawBitmap(processedBitmap, 0f, 0f, paint)
            
            // Add watermark if needed
            if (addWatermark) {
                drawWatermark(canvas, processedBitmap.width, processedBitmap.height)
            }
            
            pdfDocument.finishPage(pdfPage)
            
            // Clean up
            if (processedBitmap != page.bitmap) {
                processedBitmap.recycle()
            }
        }
        
        // Convert to bytes
        val outputStream = ByteArrayOutputStream()
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        
        val pdfBytes = outputStream.toByteArray()
        
        // Generate filename
        val finalFileName = fileName ?: generateFileName()
        
        // Save to MediaStore
        return FileUtils.savePdfToMediaStore(context, pdfBytes, finalFileName)
    }
    
    private fun processImage(page: ScannedPage, highQuality: Boolean): Bitmap {
        var bitmap = page.bitmap
        
        // Apply crop if specified
        page.cropRect?.let { crop ->
            bitmap = cropBitmap(bitmap, crop)
        }
        
        // Apply brightness and contrast
        if (page.brightness != 0f || page.contrast != 1f) {
            bitmap = adjustBrightnessContrast(bitmap, page.brightness, page.contrast)
        }
        
        // Resize if not high quality (to reduce file size)
        if (!highQuality && (bitmap.width > 1920 || bitmap.height > 1920)) {
            val scale = 1920f / maxOf(bitmap.width, bitmap.height)
            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        }
        
        return bitmap
    }
    
    private fun cropBitmap(bitmap: Bitmap, crop: com.zaheer.pdfscanner.ui.viewmodel.CropRect): Bitmap {
        val left = (crop.left * bitmap.width).toInt().coerceIn(0, bitmap.width)
        val top = (crop.top * bitmap.height).toInt().coerceIn(0, bitmap.height)
        val right = (crop.right * bitmap.width).toInt().coerceIn(0, bitmap.width)
        val bottom = (crop.bottom * bitmap.height).toInt().coerceIn(0, bitmap.height)
        
        val width = (right - left).coerceAtLeast(1)
        val height = (bottom - top).coerceAtLeast(1)
        
        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }
    
    private fun adjustBrightnessContrast(bitmap: Bitmap, brightness: Float, contrast: Float): Bitmap {
        val cm = ColorMatrix()
        
        // Apply contrast
        val scale = contrast
        val translate = (1f - contrast) * 127.5f + brightness * 255f
        cm.set(floatArrayOf(
            scale, 0f, 0f, 0f, translate,
            0f, scale, 0f, 0f, translate,
            0f, 0f, scale, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        ))
        
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        
        return result
    }
    
    private fun drawWatermark(canvas: Canvas, width: Int, height: Int) {
        val paint = Paint().apply {
            color = Color.argb(80, 128, 128, 128)
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        
        canvas.save()
        canvas.rotate(-45f, width / 2f, height / 2f)
        canvas.drawText("PDF Scanner - Free Version", width / 2f, height / 2f, paint)
        canvas.restore()
    }
    
    private fun generateFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return "Scan_$timestamp.pdf"
    }
}
