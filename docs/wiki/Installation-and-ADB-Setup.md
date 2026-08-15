# 🚀 Installation & ADB Setup Guide

This guide details how to install Zen TV and set it as the permanent default launcher across various Android TV devices.

---

## 1. Sideloading via ADB

Connect your TV to the same Wi-Fi network and enable **Developer Options** and **USB / Network Debugging** in your TV Settings.

```bash
# Connect to your TV IP address
adb connect <TV_IP_ADDRESS>:5555

# Verify connection
adb devices

# Install the latest Zen TV Release APK
adb install -r app-release-unsigned.apk
```

---

## 2. Setting Zen TV as Default Launcher

On Android TV 9+ (Pie and newer), you can set the default Home activity directly via ADB:

```bash
adb shell cmd package set-home-activity com.ekshana.tv.launcher/.MainActivity
```

To verify it is active:
```bash
adb shell cmd package resolve-activity -a android.intent.action.MAIN -c android.intent.category.HOME
```

---

## 3. Disabling OEM Bloat Launchers (Optional)

If your TV insists on opening its default OEM launcher (e.g. PatchWall on Mi TV, or stock Google TV launcher), you can disable the default launcher via ADB:

### Xiaomi Mi TV (PatchWall / Android TV Home)
```bash
# Disable Xiaomi PatchWall
adb shell pm disable-user --user 0 com.mitv.tvhome

# Disable Android TV stock launcher (if desired)
adb shell pm disable-user --user 0 com.google.android.tvlauncher
```

> **Note:** To re-enable any disabled launcher in the future, replace `disable-user --user 0` with `enable`.
