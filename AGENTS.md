# AGENTS.md — Developer & AI Pair Programming Guide for Zen TV

This document provides architectural standards, device constraints, and engineering guidelines for developers and AI agents working on **Zen TV** (formerly FastLauncher).

---

## 🎯 Project Overview & Mission

**Zen TV** is an ultra-lightweight, zero-bloat, distraction-free Android TV launcher built with **Jetpack Compose for TV (Material 3)**. It is specifically engineered to deliver instant (<50ms) cold start, 60fps fluid DPAD navigation, and a tiny memory footprint (~29–33 MB RAM) on low-spec Smart TVs and TV sticks (e.g., Xiaomi Mi LED Smart TV 4A 32" with 1 GB RAM).

---

## 📐 Hardware Specifications & Constraints

- **Target Device Profile**: Xiaomi Mi TV 4A 32" (`amelie`), Android TV 9 (API 28 / Pie).
- **RAM**: 1 GB Total (Launcher must idle at **≤ 35 MB PSS** to reserve RAM for heavy streaming apps).
- **Display Framebuffer**: `1280x720` physical resolution (720p HD) at `213 dpi` (TVDPI).
- **Safe Viewport Margins**: Minimum **`44dp` horizontal** and **`16dp` vertical** overscan padding on all root containers.
- **Input Modality**: 100% remote D-pad navigation (DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT, DPAD_CENTER / ENTER, BACK, MENU).

---

## 🏗️ Architecture & Codebase Map

- **Application ID / Package**: `com.ekshana.tv.launcher`
- **Main Launcher Activity**: `MainActivity.kt` (`singleTask`, `CATEGORY_HOME`, `CATEGORY_DEFAULT`, `CATEGORY_LEANBACK_LAUNCHER`).
- **State Management**: Reactive MVVM pattern with Kotlin Coroutines & `StateFlow` (`HomeViewModel.kt`).
- **Data & Image Layer**:
  - `AppRepository.kt`: Asynchronous 2-stage app loading pipeline (fast package name discovery + background `loadIcon()` decoding to 96x96 `ImageBitmap` cached in `ConcurrentHashMap`).
  - `TvInputManagerHelper.kt`: Queries and switches hardware inputs (HDMI 1 ARC, HDMI 2, HDMI 3, AV, Antenna) via `android.media.tv.TvInputManager`.
  - `PackageChangeReceiver.kt`: Listens for broadcast package changes (`PACKAGE_ADDED`, `PACKAGE_REMOVED`, `PACKAGE_CHANGED`) to auto-refresh app lists without background polling.

### UI Structure (`app/src/main/java/com/ekshana/tv/launcher/ui/`)
- `home/HomeScreen.kt`: Root screen containing branded splash, glassmorphic TopBar, Favorites Row, All Apps Grid, and in-hierarchy overlays.
- `components/AppCard.kt`: DPAD-focusable TV card with custom focus border glow and remote Menu key shortcuts.
- `components/FavoritesRow.kt`: Horizontal row for pinned favorites.
- `components/AllAppsGrid.kt`: Adaptive vertical grid (`GridCells.Adaptive(minSize = 112.dp)`) matching Favorites dimensions.
- `components/AppContextMenu.kt`: In-hierarchy modal overlay for managing apps (Add/Remove Favorite, Reorder, Hide, App Info, Uninstall).
- `components/TvInputSwitcherDialog.kt`: Single-page modal for instant hardware input switching.
- `settings/SettingsScreen.kt`: Comprehensive glassmorphic launcher settings (Default Home status, Ram cleaner, Hide/Unhide apps, About).
- `theme/`: Next-Gen OLED Obsidian Dark theme (`Color.kt`, `Theme.kt`, `Type.kt`).

---

## 📜 Key Engineering Rules & Gotchas

1. **Avoid `androidx.compose.ui.window.Dialog` on Android 9 TV**:
   - Spawning secondary OS window dialogs drops DPAD focus and triggers synthetic click release bugs.
   - Always use **in-hierarchy modal overlays** (`Box` overlay inside the root composable tree guarded by `BackHandler`).
2. **Strict Memory Budget**:
   - Never load uncompressed raw drawables directly into Compose `AsyncImage` without downsampling.
   - Always decode icons into compact 96x96 `ImageBitmap` off the main thread.
3. **Focus Preservation**:
   - Restore focus to the last selected app when returning from external apps or closing modal overlays.
4. **Android TV Overscan**:
   - Maintain 44dp horizontal padding across TopBar, Favorites, and All Apps to prevent TV bezel clipping.

---

## 🛠️ Build & Development Commands

```bash
# Debug build and test on connected TV
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell am start -n com.ekshana.tv.launcher/.MainActivity

# Production release build (R8 minified & resource shrunk)
./gradlew assembleRelease

# Set Zen TV as the default Android TV Home launcher via ADB
adb shell cmd package set-home-activity com.ekshana.tv.launcher/.MainActivity
```
