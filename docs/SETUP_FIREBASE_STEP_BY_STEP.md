# Setup Firebase - Step by Step

## 🎯 Tujuan

Membuat device Android bisa di-monitoring dari jaringan manapun menggunakan Firebase sebagai perantara.

## 📋 Step-by-Step

### Step 1: Buat Firebase Project

1. Buka browser, akses: **https://console.firebase.google.com/**
2. Login dengan Google account
3. Klik **"Add project"** atau **"Create a project"**
4. Masukkan nama project: `xploitspy` (atau nama lain)
5. Klik **Continue**
6. (Opsional) Enable Google Analytics
7. Klik **Create project**
8. Tunggu beberapa detik, klik **Continue**

### Step 2: Setup Firestore Database

1. Di Firebase Console, klik **"Firestore Database"** di menu kiri
2. Klik **"Create database"**
3. Pilih **"Start in test mode"** (untuk development)
4. Klik **Next**
5. Pilih **Location** (pilih yang terdekat dengan Anda)
6. Klik **Enable**
7. Tunggu beberapa detik sampai database siap

### Step 3: Setup Android App di Firebase

1. Di Firebase Console, klik ikon **Android** (🟢)
2. Masukkan **Package name**: `com.xploitspy.client`
3. (Opsional) App nickname: `XploitSPY Client`
4. (Opsional) Debug signing certificate SHA-1 (bisa skip)
5. Klik **Register app**
6. Download **`google-services.json`**
7. **Copy file** `google-services.json` ke folder:
   ```
   client/app/google-services.json
   ```

### Step 4: Setup Server (Service Account)

1. Di Firebase Console, klik **⚙️ Settings** → **Project settings**
2. Pilih tab **"Service accounts"**
3. Klik **"Generate new private key"**
4. Klik **"Generate key"** di dialog
5. File JSON akan otomatis terdownload
6. **Rename file** menjadi: `firebase-service-account.json`
7. **Copy file** ke folder:
   ```
   server/firebase-service-account.json
   ```

### Step 5: Install Dependencies

#### Server

```bash
cd server
npm install firebase-admin
```

#### Android

Dependencies sudah ditambahkan di `build.gradle`. Tidak perlu install manual.

### Step 6: Rebuild APK

```bash
cd client
export JAVA_HOME=/opt/homebrew/opt/openjdk@11
export PATH=$JAVA_HOME/bin:$PATH
./gradlew clean assembleDebug
```

### Step 7: Install APK

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 8: Restart Server

```bash
cd server
npm run pm2:restart
```

## ✅ Testing

1. **Buka aplikasi** di Android device
2. **Berikan semua permissions**
3. **Cek Firebase Console**:
   - Firestore Database → Data
   - Harus muncul collection `devices`
   - Data akan muncul secara real-time
4. **Cek Dashboard**:
   - Buka: http://localhost:3000
   - Login: admin / password
   - Device harus muncul di daftar
   - Data akan sync dari Firebase

## 🔍 Verifikasi

### Cek Firebase

1. Firebase Console → Firestore Database
2. Harus ada collection `devices`
3. Klik collection, harus ada document dengan device ID
4. Klik document, harus ada sub-collections:
   - `gps_logs`
   - `sms_logs`
   - `call_logs`
   - dll

### Cek Server Logs

```bash
cd server
npm run pm2:logs
```

Harus muncul:
- `✅ Firebase initialized successfully`
- `✅ Firestore listeners active`
- `Device synced from Firestore: {deviceId}`

### Cek Android Logs

```bash
adb logcat | grep -i firebase
```

Harus muncul:
- `Device registered in Firebase`
- Tidak ada error connection

## 🎉 Selesai!

Sekarang device bisa di-monitoring dari jaringan manapun!

## 📝 Catatan Penting

1. **File Sensitif**: 
   - `google-services.json` dan `firebase-service-account.json` jangan di-commit ke Git!
   - Sudah ditambahkan ke `.gitignore`

2. **Firebase Free Tier**:
   - Cukup untuk development
   - Monitor usage di Firebase Console
   - Untuk production, pertimbangkan upgrade

3. **Security**:
   - Test mode = semua orang bisa read/write
   - Untuk production, setup Security Rules
   - Setup Authentication

4. **Dual Mode**:
   - Aplikasi akan kirim ke Firebase (cross-network)
   - Juga kirim ke Socket.IO jika dalam jaringan yang sama (faster)

## 🐛 Troubleshooting

### Firebase tidak initialize

- Pastikan `firebase-service-account.json` ada di `server/`
- Cek file JSON valid
- Cek server logs untuk error detail

### Data tidak muncul di Firebase

- Pastikan `google-services.json` ada di `client/app/`
- Pastikan aplikasi sudah diberikan permissions
- Cek log Android: `adb logcat | grep Firebase`

### Server tidak sync

- Pastikan Firebase Admin SDK terinstall
- Cek server logs
- Pastikan Firestore database sudah dibuat

## 📚 Dokumentasi Lengkap

- `FIREBASE_SETUP.md` - Dokumentasi lengkap
- `INTEGRATE_FIREBASE.md` - Detail integrasi
- `FIREBASE_QUICK_SETUP.md` - Quick reference

