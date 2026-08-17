# AGENTS.md — Developer & AI Pair Programming Guide for Zen Launcher

This document provides architectural standards, device constraints, and engineering guidelines for developers and AI agents working on **Zen Launcher**.

---

## 🎯 Project Overview & Mission

**Zen Launcher** is an ultra-lightweight, zero-bloat, distraction-free Android TV launcher built with **Jetpack Compose for TV (Material 3)**. It is specifically engineered to deliver instant (<50ms) cold start, 60fps fluid DPAD navigation, and a tiny memory footprint (~29–33 MB RAM) on low-spec Smart TVs and TV sticks (e.g., Xiaomi Mi LED Smart TV 4A 32" with 1 GB RAM).

---

## 📐 Hardware Specifications & Constraints

- **Target Device Profile**: Xiaomi Mi TV 4A / 4C 32" (`MiTV4I` / `magnolia` / `amelie`), Android TV 9 (API 28 / Pie). ADB target: `192.168.68.112:5555`.
- **RAM**: 1 GB Total (Launcher must idle at **≤ 35 MB PSS** to reserve RAM for heavy streaming apps).
- **Display Framebuffer**: `1280x720` physical resolution (720p HD) at `213 dpi` (TVDPI).
- **Safe Viewport Margins**: Minimum **`44dp` horizontal** and **`16dp` vertical** overscan padding on all root containers.
- **Input Modality**: 100% remote D-pad navigation. See Remote Key Mapping section below.

---

## 🎮 Xiaomi Mi TV Remote Key Mapping

The target device uses the Xiaomi IR remote (XMRM-00A / classic design). Key behaviour confirmed via `getevent` kernel capture:

| Button | Linux Keycode | Android KeyCode | Reaches App? | Behaviour in Zen Launcher |
|---|---|---|---|---|
| D-pad ↑↓←→ | `KEY_UP/DOWN/LEFT/RIGHT` | `KEYCODE_DPAD_*` | ✅ | Grid & TopBar focus traversal |
| OK / Center | `KEY_ENTER` | `KEYCODE_DPAD_CENTER` | ✅ | Short press: launch app |
| OK / Center (hold) | `KEY_ENTER` repeat | `KEYCODE_DPAD_CENTER` (repeatCount ≥ 1) | ✅ | Long press: opens Context Menu |
| Back `<` | `KEY_BACK` | `KEYCODE_BACK` | ✅ | Dismiss modal / restore focus |
| Volume +/- | `KEY_VOLUMEUP/DOWN` | `KEYCODE_VOLUME_UP/DOWN` | ❌ (OS AudioManager HAL) | System volume HUD |
| Home `⊙` | `KEY_HOMEPAGE` | `KEYCODE_HOME` | ❌ (ActivityManager intent) | Returns to Zen Launcher |
| Menu `☰` (3 lines) | _none_ | _none_ | ❌ (Xiaomi firmware intercept) | Opens Xiaomi system panel — **cannot be overridden** |

> **Critical Finding**: The physical `☰` (Menu) button on this remote generates **zero Linux kernel input events** at the `/dev/input/event1` (`aml_keypad`) level. It is processed entirely by Xiaomi's IR decoder firmware. No app at any privilege level can intercept it.

---

## 🏗️ Architecture & Codebase Map

- **Application ID / Package**: `com.ekshana.tv.launcher`
- **Main Launcher Activity**: `MainActivity.kt` (`singleTask`, `CATEGORY_HOME`, `CATEGORY_DEFAULT`, `CATEGORY_LEANBACK_LAUNCHER`).
- **State Management**: Reactive MVVM pattern with Kotlin Coroutines & `StateFlow` (`HomeViewModel.kt`).
- **Data & Image Layer**:
  - `AppRepository.kt`: Asynchronous 2-stage app loading pipeline (fast package name discovery + background `loadIcon()` decoding to 96x96 `ImageBitmap` cached in `ConcurrentHashMap`). Exposes `isInitialized` flag guarding `PackageChangeReceiver` from process-death races.
  - `TvInputManagerHelper.kt`: Queries and switches hardware inputs (HDMI 1 ARC, HDMI 2, HDMI 3, AV, Antenna) via `android.media.tv.TvInputManager`.
  - `PackageChangeReceiver.kt`: Listens for broadcast package changes (`PACKAGE_ADDED`, `PACKAGE_REMOVED`, `PACKAGE_CHANGED`) to auto-refresh app lists without background polling.

### UI Structure (`app/src/main/java/com/ekshana/tv/launcher/ui/`)
- `home/HomeScreen.kt`: Root screen with glassmorphic TopBar (clock, Wi-Fi, Settings, Inputs, Hidden Apps pill), All Apps Grid, and in-hierarchy modal overlays.
- `components/AppCard.kt`: DPAD-focusable TV card. Uses `onPreviewKeyEvent` to intercept long press via key repeat (`repeatCount ≥ 1`) and fires context menu on `ACTION_UP` (key released). Long-press glow feedback via `longPressArmed` state.
- `components/AllAppsGrid.kt`: Fixed 6-column grid (`GridCells.Fixed(6)`) with `Modifier.focusRestorer()`.
- `components/AppContextMenu.kt`: In-hierarchy modal overlay (Hide, App Info, Uninstall). 100ms settle delay before accepting clicks.
- `components/HiddenAppsModal.kt`: In-hierarchy modal for viewing and restoring hidden apps. Includes 300ms click guard.
- `components/RecommendationsRow.kt`: Watch Next / OS TV recommendations row via `TvContractCompat`.
- `theme/`: OLED Obsidian Dark theme (`Color.kt`, `Theme.kt`, `Type.kt`).

---

## 📜 Key Engineering Rules & Gotchas

### 1. Never Use `androidx.compose.ui.window.Dialog` on Android 9 TV
Spawning secondary OS window dialogs drops DPAD focus and triggers synthetic click release bugs.
Always use **in-hierarchy modal overlays** (`Box` overlay inside the root composable tree guarded by `BackHandler`).

### 2. Long-Press Synthetic Click Release Bug (CRITICAL)
**Problem**: Compose TV's `Card.onLongClick` fires while the key is **still physically held down**. When the user releases, `ACTION_UP` is delivered to whatever view now has focus (e.g., the first button of a newly-opened modal), triggering an unwanted auto-click.

**Wrong Fix (band-aid)**: Adding a delay in the modal and swallowing `ACTION_UP` there. Fails if user releases faster than the delay.

**Correct Fix**: In `AppCard.kt`, use `onPreviewKeyEvent` to intercept the key repeat before it reaches `Card`:
- `ACTION_DOWN` with `repeatCount == 0`: pass through (Card tracks initial press).
- `ACTION_DOWN` with `repeatCount ≥ 1`: set `longPressArmed = true`, **consume** (Card never fires `onLongClick`).
- `ACTION_UP` with `longPressArmed == true`: reset flag, call `onLongClick()`, **consume** (menu opens **after** key release — no lingering `ACTION_UP`).
- Set `onLongClick = { /* handled via key events */ }` on `Card` to disable built-in long click.

### 3. Strict Memory Budget
- Never load uncompressed raw drawables directly into Compose `AsyncImage` without downsampling.
- Always decode icons into compact 96x96 `ImageBitmap` off the main thread.

### 4. Focus Preservation
- Use `Modifier.focusRestorer()` on all `LazyVerticalGrid` and `TvLazyRow` containers.
- Restore focus to `lastFocusedPackage` (tracked in `HomeViewModel`) when returning from overlays.

### 5. Android TV Overscan
- Maintain **44dp horizontal padding** across TopBar, Favorites, and All Apps to prevent TV bezel clipping.

### 6. VectorDrawable Rendering on Android 9 TV
- Android TV 9 (API 28) uses software rendering fallbacks for VectorDrawables which causes blurriness at 213 TVDPI.
- Use **pre-rendered PNG bitmaps** in `mipmap-tvdpi`, `mipmap-hdpi`, `mipmap-xhdpi`, etc. for app icons and launcher banner.

### 7. Key Event Interception Priority on Xiaomi Android TV
- `Volume`, `Home`: Consumed at HAL/ActivityManager level — never reach apps.
- `Menu ☰`: Consumed by Xiaomi firmware IR decoder — zero kernel events generated.
- Only `DPAD_*`, `ENTER`, `BACK`, and `TV_INPUT_*` keycodes reliably reach the launcher.
- Use `onKeyDown()` (not `dispatchKeyEvent`) for TV input source key interception; `dispatchKeyEvent` is unnecessary overhead.

---

## 🛠️ Build & Development Commands

```bash
# Debug build, install, and launch on connected TV (over ADB WiFi)
./gradlew assembleDebug && adb -s 192.168.68.112:5555 install -r app/build/outputs/apk/debug/app-debug.apk && adb -s 192.168.68.112:5555 shell am start -n com.ekshana.tv.launcher/.MainActivity

# Run unit tests before building
./gradlew testDebugUnitTest

# Production release build (R8 minified & resource shrunk, ARM-only: armeabi-v7a + arm64-v8a)
./gradlew assembleRelease

# Set Zen Launcher as the default Android TV Home launcher via ADB
adb shell cmd package set-home-activity com.ekshana.tv.launcher/.MainActivity

# Live remote key diagnostics (run, then press remote buttons)
adb -s 192.168.68.112:5555 shell getevent -l /dev/input/event1

# Check launcher memory footprint
adb -s 192.168.68.112:5555 shell dumpsys meminfo com.ekshana.tv.launcher
```

---

## 📦 Build Configuration Notes

- **ABI Filters**: `armeabi-v7a` + `arm64-v8a` only (no x86/x86_64). Release APK ~3.3 MB.
- **Min SDK**: 28 (Android 9 / Pie). **Target SDK**: 35.
- **Theme**: `android:Theme.Material.NoActionBar` (no `androidx.appcompat` dependency).
- **R8**: Full minification + resource shrinking enabled in release builds.
