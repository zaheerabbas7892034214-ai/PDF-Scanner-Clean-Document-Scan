# Add project specific ProGuard rules here.

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Billing
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# Keep generic signature of Compose classes
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
