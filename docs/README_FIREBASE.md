# Firebase Integration - Monitoring Cross-Network

## 🎯 Solusi Masalah

Dengan Firebase, device Android bisa di-monitoring **dari jaringan manapun**, tidak perlu dalam WiFi yang sama!

## 📋 Cara Kerja

```
Android Device (Jaringan A - WiFi/Cellular)
    ↓
Firebase Firestore (Cloud)
    ↓
Server (Jaringan B - Internet)
    ↓
Dashboard Web
```

## 🚀 Quick Setup

### 1. Buat Firebase Project (5 menit)

1. Buka: https://console.firebase.google.com/
2. Add project → Nama: `xploitspy`
3. Create project

### 2. Setup Firestore

1. Firestore Database → Create database
2. Start in **test mode**
3. Pilih location
4. Enable

### 3. Download Files

#### Android: `google-services.json`
1. Project Settings → Add app → Android
2. Package: `com.xploitspy.client`
3. Download `google-services.json`
4. Copy ke: `client/app/google-services.json`

#### Server: `firebase-service-account.json`
1. Project Settings → Service Accounts
2. Generate new private key
3. Download JSON
4. Simpan: `server/firebase-service-account.json`

### 4. Install & Build

```bash
# Install Firebase Admin SDK
cd server
npm install firebase-admin

# Rebuild APK (Firebase sudah ditambahkan di dependencies)
cd ../client
./gradlew assembleDebug

# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 5. Restart Server

```bash
cd server
npm run pm2:restart
```

## ✅ Selesai!

Sekarang device bisa di-monitoring dari jaringan manapun!

## 🔍 Testing

1. Install APK di device
2. Buka aplikasi (berikan permissions)
3. Device akan otomatis kirim data ke Firebase
4. Cek Firebase Console → Firestore → Data akan muncul
5. Cek Dashboard → Server akan sync data dari Firebase

## 📊 Struktur Data Firestore

```
/devices/{deviceId}/
  ├── deviceInfo: { deviceName, androidVersion, lastSeen }
  ├── gps_logs/{timestamp}
  ├── sms_logs/{timestamp}
  ├── call_logs/{timestamp}
  ├── contacts/{contactId}
  ├── apps/{packageName}
  ├── clipboard/{timestamp}
  ├── notifications/{timestamp}
  ├── wifi/{timestamp}
  └── commands/{commandId}
```

## 💡 Keuntungan

✅ **Cross-Network**: Device bisa di-monitoring dari jaringan manapun
✅ **Real-time**: Firestore real-time sync
✅ **Reliable**: Firebase handle connection issues
✅ **Offline Support**: Data di-cache saat offline, sync saat online
✅ **Scalable**: Bisa handle banyak device sekaligus

## 🔒 Security (Production)

Setup Firestore Security Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /devices/{deviceId}/{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 📝 Catatan

- Firebase free tier cukup untuk development
- Monitor usage di Firebase Console
- Setup authentication untuk production
- File `firebase-service-account.json` dan `google-services.json` jangan di-commit ke Git!

## 🐛 Troubleshooting

### Data tidak muncul di Firebase

1. Cek `google-services.json` ada di `client/app/`
2. Cek log Android: `adb logcat | grep Firebase`
3. Pastikan aplikasi sudah diberikan permissions

### Server tidak sync dari Firebase

1. Cek `firebase-service-account.json` ada di `server/`
2. Cek server logs: `npm run pm2:logs`
3. Pastikan Firebase Admin SDK terinstall: `npm install firebase-admin`

### Build error

1. Pastikan Google Services plugin sudah ditambahkan
2. Pastikan `google-services.json` ada
3. Clean build: `./gradlew clean assembleDebug`

