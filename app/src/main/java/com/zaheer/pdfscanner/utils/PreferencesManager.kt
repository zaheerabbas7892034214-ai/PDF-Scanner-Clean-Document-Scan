package com.zaheer.pdfscanner.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("scanner_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_IS_PRO = "is_pro"
        private const val KEY_SCANS_REMAINING = "scans_remaining"
        private const val KEY_LAST_SCAN_DATE = "last_scan_date"
    }
    
    fun isPro(): Boolean {
        return prefs.getBoolean(KEY_IS_PRO, false)
    }
    
    fun setProStatus(isPro: Boolean) {
        prefs.edit().putBoolean(KEY_IS_PRO, isPro).apply()
    }
    
    fun getScansRemainingToday(): Int {
        val today = getTodayDateString()
        val lastScanDate = prefs.getString(KEY_LAST_SCAN_DATE, "")
        
        return if (lastScanDate == today) {
            prefs.getInt(KEY_SCANS_REMAINING, 5)
        } else {
            // Reset for new day
            prefs.edit()
                .putString(KEY_LAST_SCAN_DATE, today)
                .putInt(KEY_SCANS_REMAINING, 5)
                .apply()
            5
        }
    }
    
    fun setScansRemainingToday(remaining: Int) {
        val today = getTodayDateString()
        prefs.edit()
            .putString(KEY_LAST_SCAN_DATE, today)
            .putInt(KEY_SCANS_REMAINING, remaining)
            .apply()
    }
    
    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
}
