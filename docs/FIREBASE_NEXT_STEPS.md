# Langkah Selanjutnya - Setup Firebase

## ✅ Status Saat Ini

- Firebase Project sudah dibuat: `xploitspy-e4297` ✅
- Code sudah siap dengan Firebase integration ✅
- Build APK berhasil ✅

## 📋 Langkah Selanjutnya

### Step 1: Setup Firestore Database

1. Di Firebase Console, klik **"Firestore Database"** di menu kiri
2. Klik **"Create database"**
3. Pilih **"Start in test mode"** (untuk development)
4. Klik **Next**
5. Pilih **Location** (pilih yang terdekat, contoh: `asia-southeast2` untuk Indonesia)
6. Klik **Enable**
7. Tunggu beberapa detik sampai database siap

### Step 2: Download google-services.json (Android)

1. Di Firebase Console, klik **⚙️ Settings** (ikon gear) → **Project settings**
2. Scroll ke bawah, bagian **"Your apps"**
3. Klik ikon **Android** (🟢) atau **"Add app"** → **Android**
4. Masukkan:
   - **Package name**: `com.xploitspy.client`
   - (Opsional) App nickname: `XploitSPY Client`
5. Klik **Register app**
6. Download **`google-services.json`**
7. **Copy file** ke folder:
   ```
   client/app/google-services.json
   ```
   (Replace file dummy yang sudah ada)

### Step 3: Download Service Account (Server)

1. Masih di **Project settings**
2. Pilih tab **"Service accounts"**
3. Klik **"Generate new private key"**
4. Klik **"Generate key"** di dialog konfirmasi
5. File JSON akan otomatis terdownload (nama: `xploitspy-e4297-xxxxx.json`)
6. **Rename file** menjadi: `firebase-service-account.json`
7. **Copy file** ke folder:
   ```
   server/firebase-service-account.json
   ```

### Step 4: Rebuild APK dengan Firebase

```bash
cd client
export JAVA_HOME=/opt/homebrew/opt/openjdk@11
export PATH=$JAVA_HOME/bin:$PATH

# Clean build
./gradlew clean

# Build dengan Firebase
./gradlew assembleDebug
```

### Step 5: Install APK ke Device

```bash
# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Atau jika ada multiple devices
adb -s R9CX100A5NF install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 6: Restart Server

```bash
cd server
npm run pm2:restart
```

Server akan otomatis detect Firebase dan mulai sync data.

### Step 7: Test

1. **Buka aplikasi** di Android device
2. **Berikan semua permissions**
3. **Cek Firebase Console**:
   - Firestore Database → Data
   - Harus muncul collection `devices`
   - Klik collection, harus ada document dengan device ID
   - Data akan muncul secara real-time
4. **Cek Dashboard**:
   - Buka: http://localhost:3000
   - Login: admin / password
   - Device harus muncul di daftar
   - Data akan sync dari Firebase

## 🔍 Verifikasi

### Cek Firebase Console

1. Firestore Database → Data
2. Harus ada collection: `devices`
3. Klik collection → harus ada document dengan device ID
4. Klik document → harus ada sub-collections:
   - `gps_logs`
   - `sms_logs`
   - `call_logs`
   - `contacts`
   - `apps`
   - dll

### Cek Server Logs

```bash
cd server
npm run pm2:logs
```

Harus muncul:
```
✅ Firebase initialized successfully
✅ Firestore listeners active
Device synced from Firestore: {deviceId}
```

### Cek Android Logs

```bash
adb logcat | grep -i firebase
```

Harus muncul:
```
Device registered in Firebase: {deviceId}
```

## 🎉 Selesai!

Setelah semua langkah selesai, device bisa di-monitoring dari **jaringan manapun**!

## 📝 Checklist

- [ ] Firestore Database dibuat (test mode)
- [ ] `google-services.json` didownload dan di-copy ke `client/app/`
- [ ] `firebase-service-account.json` didownload dan di-copy ke `server/`
- [ ] APK di-rebuild dengan file Firebase asli
- [ ] APK diinstall ke device
- [ ] Server di-restart
- [ ] Device muncul di Firebase Console
- [ ] Device muncul di Dashboard
- [ ] Data mulai muncul

## 🐛 Troubleshooting

### Data tidak muncul di Firebase

1. Cek `google-services.json` sudah di-copy dengan benar
2. Cek aplikasi sudah diberikan permissions
3. Cek log Android: `adb logcat | grep Firebase`
4. Pastikan device punya koneksi internet

### Server tidak sync

1. Cek `firebase-service-account.json` ada di `server/`
2. Cek server logs: `npm run pm2:logs`
3. Pastikan Firebase Admin SDK terinstall: `npm install firebase-admin`

### Build error

1. Pastikan `google-services.json` valid (dari Firebase, bukan dummy)
2. Clean build: `./gradlew clean`
3. Rebuild: `./gradlew assembleDebug`

