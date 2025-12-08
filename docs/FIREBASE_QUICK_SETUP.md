# Firebase Quick Setup Guide

## 🚀 Setup Cepat (5 Menit)

### Step 1: Buat Firebase Project

1. Buka: https://console.firebase.google.com/
2. Klik "Add project"
3. Nama: `xploitspy`
4. Create project

### Step 2: Setup Firestore

1. Firestore Database → Create database
2. Start in **test mode**
3. Pilih location
4. Enable

### Step 3: Download Files

#### Android: `google-services.json`

1. Project Settings → Add app → Android
2. Package: `com.xploitspy.client`
3. Download `google-services.json`
4. Copy ke: `client/app/google-services.json`

#### Server: `firebase-service-account.json`

1. Project Settings → Service Accounts
2. Generate new private key
3. Download JSON
4. Simpan sebagai: `server/firebase-service-account.json`

### Step 4: Install Dependencies

```bash
# Server
cd server
npm install firebase-admin

# Android - sudah otomatis via Gradle
```

### Step 5: Rebuild & Install

```bash
# Build APK
cd client
./gradlew assembleDebug

# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 6: Restart Server

```bash
cd server
npm run pm2:restart
```

## ✅ Selesai!

Sekarang device bisa di-monitoring dari jaringan manapun!

## Testing

1. Install APK di device
2. Buka aplikasi
3. Cek Firebase Console → Firestore → Data akan muncul
4. Cek Dashboard → Device akan muncul

## Troubleshooting

### Firebase tidak terhubung

- Pastikan `google-services.json` ada di `client/app/`
- Pastikan `firebase-service-account.json` ada di `server/`
- Cek Firebase Console untuk errors

### Data tidak muncul

- Cek log Android: `adb logcat | grep Firebase`
- Cek server logs: `npm run pm2:logs`
- Cek Firebase Console → Firestore

