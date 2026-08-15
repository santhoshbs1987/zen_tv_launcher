# ❓ Troubleshooting & Frequently Asked Questions

### Q: Zen TV does not open when I press the HOME button on my remote.
**A:** On some OEM TVs (like Xiaomi PatchWall or Sony BRAVIA), the manufacturer hardcodes the remote HOME button to launch their proprietary launcher.  
To fix this:
1. Connect via ADB: `adb connect <TV_IP>:5555`
2. Run: `adb shell cmd package set-home-activity com.ekshana.tv.launcher/.MainActivity`
3. If still overridden, disable the default OEM launcher: `adb shell pm disable-user --user 0 com.mitv.tvhome`

---

### Q: Why do some sideloaded mobile apps not show icons or open?
**A:** Standard Android mobile apps often lack Leanback TV launcher banners. Zen TV automatically discovers standard `android.intent.action.MAIN` + `android.intent.category.LAUNCHER` activities in addition to TV-specific leanback launchers, ensuring all sideloaded apps show up.

---

### Q: How do I unhide an app that I previously hid?
**A:** Press the **MENU** key on your remote (or click the Settings cog icon in the TopBar) -> Navigate to **Settings** -> **Hidden Apps** -> Uncheck the app you wish to restore to the main grid.
