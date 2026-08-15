# 🧘 Zen TV — Minimalist, Ultra-Lightweight Android TV Launcher

A blazing-fast, ultra-lightweight, and distraction-free Android TV launcher built with modern Jetpack Compose for TV (Material 3). Designed specifically for resource-constrained Smart TVs and TV sticks (e.g., Xiaomi Mi LED Smart TV 4A 32", Fire TV Stick, Chromecast with Google TV, and generic Android TV boxes).

---

## 🌟 Highlights & Features

- **🧘 Clean & Distraction-Free**: Zero ads, zero sponsored banners, zero recommendation bloat, and zero background telemetry.
- **⚡ Blazing Fast & Low Memory Footprint**: Idles around **~29 MB RAM**, keeping your TV snappy without UI lag or memory pressure.
- **🎮 DPAD & Remote Optimized**: Intuitive directional navigation with fluid focus transitions and focus-memory restoration.
- **⭐ Pinned Favorites Row**: Pin up to 8 of your go-to apps and reorder them with a single click.
- **🔌 TV Input Switcher**: Quick dialog to switch between HDMI 1 (ARC), HDMI 2, HDMI 3, AV (Composite), and Live TV / Antenna.
- **🧹 One-Tap RAM Cleaner**: Instantly clears background cached tasks before launching demanding apps.
- **📦 Sideloaded App Friendly**: Automatically discovers both native Android TV (`LEANBACK_LAUNCHER`) and standard mobile (`LAUNCHER`) sideloaded apps.
- **👁️ Hide / Unhide Apps**: Clean up system bloat and unused apps from your main launcher grid.
- **🎨 Glassmorphic & Modern Aesthetic**: Subtle glow effects, high-contrast focus rings, custom dark-mode gradients, and real-time clock header.
- **🔘 TV Remote Menu Key Support**: Press the `MENU` or `SETTINGS` key on your remote anytime to open launcher preferences.

---

## 🛠️ Tech Stack & Architecture

- **Application ID / Package**: `com.ekshana.tv.launcher`
- **Language**: Kotlin 2.0+
- **UI Framework**: [Jetpack Compose for TV](https://developer.android.com/develop/ui/compose/tv) (`androidx.tv:tv-material` & `androidx.tv:tv-foundation`)
- **Target SDK**: Android 14 / Android 15 (Target SDK 37, Min SDK 24 / Android 7.0+)
- **Architecture**: Single Activity (`MainActivity`) + StateFlow Reactive MVVM Pattern
- **Persistence**: Fast and synchronous `SharedPreferences` for favourites and hidden app configuration
- **Image Pipeline**: In-memory `ConcurrentHashMap` with hardware-friendly 96x96 bitmap decoding

---

## 🚀 Setup as Default Launcher (Mi TV / Android TV)

1. **Install via ADB**:
   ```bash
   adb install -r app/build/outputs/apk/release/app-release.apk
   ```

2. **Set as Default Home**:
   ```bash
   adb shell cmd package set-home-activity com.ekshana.tv.launcher/.MainActivity
   ```
