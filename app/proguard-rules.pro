# Zen TV ProGuard Rules for Production Release

# Keep Compose & Android TV runtime classes
-keep class androidx.compose.** { *; }
-keep class androidx.tv.** { *; }

# Keep Model / Data classes
-keep class com.ekshana.tv.launcher.data.** { *; }

# Keep ViewModel
-keep class com.ekshana.tv.launcher.ui.home.HomeViewModel { *; }

# Keep BroadcastReceiver
-keep class com.ekshana.tv.launcher.receiver.PackageChangeReceiver { *; }

# Optimize reflection & kotlin metadata
-dontwarn kotlin.**
-dontwarn kotlinx.**
-dontwarn androidx.**
