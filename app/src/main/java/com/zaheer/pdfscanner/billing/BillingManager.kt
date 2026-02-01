package com.zaheer.pdfscanner.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.zaheer.pdfscanner.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(private val context: Context) : PurchasesUpdatedListener {
    
    private val preferencesManager = PreferencesManager(context)
    
    private val _isPro = MutableStateFlow(preferencesManager.isPro())
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()
    
    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()
    
    private var billingClient: BillingClient? = null
    
    companion object {
        private const val PRODUCT_ID_PRO = "scanner_pro_unlock"
    }
    
    init {
        startConnection()
    }
    
    private fun startConnection() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    queryPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection
                startConnection()
            }
        })
    }
    
    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_PRO)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = productDetailsList.firstOrNull()
            }
        }
    }
    
    private fun queryPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            }
        }
    }
    
    private fun handlePurchases(purchases: List<Purchase>) {
        var hasPro = false
        
        for (purchase in purchases) {
            if (purchase.products.contains(PRODUCT_ID_PRO)) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    hasPro = true
                    
                    // Acknowledge purchase if not already acknowledged
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                }
            }
        }
        
        // Update Pro status
        preferencesManager.setProStatus(hasPro)
        _isPro.value = hasPro
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient?.acknowledgePurchase(params) { billingResult ->
            // Purchase acknowledged
        }
    }
    
    fun launchPurchaseFlow(activity: Activity) {
        val productDetails = _productDetails.value ?: return
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }
    
    fun restorePurchases() {
        queryPurchases()
    }
    
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        }
    }
    
    fun endConnection() {
        billingClient?.endConnection()
    }
}
