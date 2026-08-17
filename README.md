# 🧘 Zen Launcher — Minimalist, Ultra-Lightweight Android TV Launcher

[![Build & Release](https://github.com/santhoshbs1987/zen_launcher/actions/workflows/release.yml/badge.svg)](https://github.com/santhoshbs1987/zen_launcher/actions/workflows/release.yml)
[![CI Status](https://github.com/santhoshbs1987/zen_launcher/actions/workflows/ci.yml/badge.svg)](https://github.com/santhoshbs1987/zen_launcher/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android_TV_%7C_Google_TV-green.svg)](https://developer.android.com/tv)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24_(Android_7.0+)-orange.svg)](app/build.gradle.kts)
[![RAM Footprint](https://img.shields.io/badge/RAM_Footprint-~29MB_PSS-brightgreen.svg)](#-highlights--features)
[![Wiki](https://img.shields.io/badge/Documentation-GitHub_Wiki-purple.svg)](https://github.com/santhoshbs1987/zen_launcher/wiki)

A blazing-fast, ultra-lightweight, and distraction-free Android TV launcher built with modern Jetpack Compose for TV (Material 3). Designed specifically for resource-constrained Smart TVs and TV sticks (e.g., Xiaomi Mi LED Smart TV 4A 32", Fire TV Stick, Chromecast with Google TV, and generic Android TV boxes).

---

## 📖 Complete Documentation & Wiki
Check out the full **[Zen Launcher GitHub Wiki](https://github.com/santhoshbs1987/zen_launcher/wiki)** for detailed guides:
- [🚀 Installation & ADB Setup Guide](https://github.com/santhoshbs1987/zen_launcher/wiki/Installation-and-ADB-Setup)
- [🎮 Remote Controls & Key Shortcuts](https://github.com/santhoshbs1987/zen_launcher/wiki/Remote-Controls-and-Shortcuts)
- [⚡ Architecture & Memory Optimization](https://github.com/santhoshbs1987/zen_launcher/wiki/Architecture-and-Memory-Optimization)
- [❓ Troubleshooting & FAQ](https://github.com/santhoshbs1987/zen_launcher/wiki/Troubleshooting-and-FAQ)

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
- **Language**: Kotlin 2.0+ (Jetpack Compose 1.7+)
- **UI Framework**: [Jetpack Compose for TV](https://developer.android.com/develop/ui/compose/tv) (Material 3)
- **Target SDK**: 35 (Android 15) | **Min SDK**: 24 (Android 7.0+)
- **Architecture**: Reactive MVVM with Kotlin Coroutines & `StateFlow`
- **Background Pipeline**: Auto-refresh app lists via `PackageChangeReceiver` (broadcast listener)
- **Persistence**: Synchronous `SharedPreferences` for ultra-low latency state restoration

---

## 🤝 Contribution & Engineering Guide

If you are a developer or using an AI coding agent to work on this project, please refer to **[AGENTS.md](AGENTS.md)**. It contains:
- Strict engineering rules for RAM management.
- Focus preservation guidelines.
- Vector rendering fallbacks for Android 9.
- Detailed remote key event capture data.

---

## 📐 Hardware Specifications & Target Profile

While Zen Launcher supports Android 7.0+, it is specifically optimized for low-spec Smart TVs and TV sticks:
- **Primary Testing Target**: Xiaomi Mi LED Smart TV 4A 32" (`magnolia`) / Android TV 9 (API 28).
- **RAM Footprint**: Idles at **≤ 35 MB PSS** to reserve maximum resources for heavy streaming apps (Netflix, Prime Video, etc.).
- **Resolution**: Native 720p HD (`1280x720`) at `213 dpi` (TVDPI).
- **Safe Viewport**: Built-in **44dp horizontal** and **16dp vertical** overscan padding on all root containers.

---

## 🎮 Remote Key Mapping & Interaction

Zen Launcher is 100% remote D-pad driven. It includes custom logic to handle peculiarities of Xiaomi and generic TV remotes:

| Button | Behaviour in Zen Launcher |
|---|---|
| **D-pad ↑↓←→** | Grid & TopBar focus traversal |
| **OK / Center** | Short press: launch app / confirm |
| **OK / Center (hold)** | Long press: opens Context Menu (Hide, Info, Uninstall) |
| **Back `<`** | Dismiss modal / restore focus |
| **Home `⊙`** | Returns to Zen Launcher |
| **Menu `☰` / Settings** | Opens Launcher Preferences (where supported) |

> [!IMPORTANT]
> **Xiaomi Remote Note**: The physical `☰` (Menu) button on some Xiaomi remotes is intercepted by system firmware and does not reach any app. Use long-press on **OK** to access app-specific options.

---

## 🏗️ Technical Architecture & "Secret Sauce"

Zen Launcher solves several common Jetpack Compose for TV issues on older Android versions:

- **No `androidx.compose.ui.window.Dialog`**: We avoid secondary windows which cause DPAD focus drops and synthetic click bugs on Android 9. All modals are **in-hierarchy overlays** with `BackHandler` support.
- **Long-Press Synthetic Click Fix**: Uses a custom `onPreviewKeyEvent` pipeline to ensure context menus open **after** key release, preventing unwanted accidental clicks.
- **Async 2-Stage Pipeline**: Apps are discovered instantly; high-quality icons are decoded in the background to 96x96 hardware-friendly bitmaps and cached in `ConcurrentHashMap`.
- **Focus Memory**: Uses `Modifier.focusRestorer()` to ensure the D-pad returns to your last selected app when navigating between rows.

---

## 🚀 Development & Build Commands

For developers and contributors, see [AGENTS.md](AGENTS.md) for the full engineering guide.

```bash
# Debug build, install, and launch on connected TV
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell am start -n com.ekshana.tv.launcher/.MainActivity

# Set as default Home launcher via ADB
adb shell cmd package set-home-activity com.ekshana.tv.launcher/.MainActivity

# Check memory footprint
adb shell dumpsys meminfo com.ekshana.tv.launcher
```

