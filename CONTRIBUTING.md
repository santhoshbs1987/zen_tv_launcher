# Contributing to Zen TV

Thank you for your interest in contributing to **Zen TV**!

Zen TV is built with a singular focus: **delivering an ultra-lightweight, zero-bloat, distraction-free Android TV launcher** that stays lightning fast on low-spec hardware (1 GB RAM TV devices like Xiaomi Mi TV 4A 32", Fire TV Stick, etc.).

---

## 📐 Core Engineering Principles

1. **Memory Budget First**: 
   - Zen TV must idle at **≤ 35 MB RAM**.
   - Do not add heavy dependencies, uncompressed image loaders, or background telemetry services.
2. **100% D-pad Accessibility**:
   - Every interactive element must be fully navigable with standard remote buttons (`DPAD_UP`, `DPAD_DOWN`, `DPAD_LEFT`, `DPAD_RIGHT`, `DPAD_CENTER`, `BACK`, `MENU`).
3. **No Secondary Window Dialogs**:
   - Avoid `androidx.compose.ui.window.Dialog` because it causes focus loss and synthetic click glitches on Android 9 TV. Use in-hierarchy modal overlays guarded by `BackHandler`.
4. **Overscan Margins**:
   - Maintain minimum `44dp` horizontal padding on all screens to prevent TV bezel clipping.

---

## 🛠️ Local Development & Testing

### Prerequisites
- JDK 17+
- Android SDK (API 24 to 34+)
- Android TV Emulator (720p or 1080p) or a physical Android TV / TV Stick connected via ADB.

### Building & Running

```bash
# Clone the repository
git clone https://github.com/santhoshbs1987/zen_tv_launcher.git
cd zen_tv_launcher

# Build debug APK and install onto connected TV device
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch Zen TV via ADB
adb shell am start -n com.ekshana.tv.launcher/.MainActivity
```

---

## 📬 Submitting a Pull Request

1. Fork the repo and create a feature branch (`git checkout -b feature/amazing-feature`).
2. Verify that `./gradlew test lintDebug` passes cleanly.
3. Verify D-pad navigation on a real TV device or TV emulator.
4. Open a Pull Request filling out the PR template checklist.
