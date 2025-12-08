# Struktur Proyek XploitSPY

## Overview

```
ExploSpy/
├── server/          # Backend Node.js Server
├── web/             # Web Dashboard Frontend (Vue 3)
├── client/          # Android Client Application
├── builder/          # APK Builder Scripts
└── docs/            # Dokumentasi
```

## Server (`/server`)

Backend server menggunakan Node.js dengan Express dan Socket.IO.

### File Utama

- `index.js` - Server utama dengan semua API endpoints dan WebSocket handlers
- `package.json` - Dependencies dan scripts
- `ecosystem.config.js` - PM2 configuration

### Database

SQLite database dengan tabel:
- `users` - User authentication
- `devices` - Registered devices
- `gps_logs` - GPS location data
- `sms_logs` - SMS messages
- `call_logs` - Call history
- `contacts` - Device contacts
- `installed_apps` - Installed applications
- `clipboard_logs` - Clipboard content
- `notifications` - Notification logs
- `wifi_networks` - WiFi network information
- `commands` - Command queue
- `audio_recordings` - Audio recording metadata
- `files` - File metadata

### API Endpoints

- `POST /api/login` - User authentication
- `POST /api/logout` - User logout
- `GET /api/devices` - List all devices
- `GET /api/devices/:deviceId/gps` - Get GPS logs
- `GET /api/devices/:deviceId/sms` - Get SMS logs
- `GET /api/devices/:deviceId/calls` - Get call logs
- `GET /api/devices/:deviceId/contacts` - Get contacts
- `GET /api/devices/:deviceId/apps` - Get installed apps
- `GET /api/devices/:deviceId/clipboard` - Get clipboard logs
- `GET /api/devices/:deviceId/notifications` - Get notifications
- `GET /api/devices/:deviceId/wifi` - Get WiFi networks
- `POST /api/devices/:deviceId/commands` - Send command
- `GET /api/devices/:deviceId/commands` - Get command history

### WebSocket Events

**Client → Server:**
- `register` - Register device
- `gps` - Send GPS data
- `sms` - Send SMS data
- `call` - Send call data
- `contacts` - Send contacts
- `apps` - Send apps list
- `clipboard` - Send clipboard content
- `notification` - Send notification
- `wifi` - Send WiFi networks
- `command_result` - Send command execution result

**Server → Client:**
- `command` - Receive command from server
- `gps_update` - GPS data update
- `sms_update` - SMS data update
- `call_update` - Call data update
- `contacts_update` - Contacts update
- `apps_update` - Apps update
- `clipboard_update` - Clipboard update
- `notification_update` - Notification update
- `wifi_update` - WiFi update
- `command_result_update` - Command result update

## Web Dashboard (`/web`)

Frontend menggunakan Vue 3 dengan Vite.

### Struktur

```
web/
├── src/
│   ├── views/
│   │   ├── Login.vue        # Login page
│   │   ├── Dashboard.vue    # Main dashboard
│   │   └── Device.vue       # Device detail page
│   ├── router/
│   │   └── index.js         # Vue Router configuration
│   ├── App.vue              # Root component
│   ├── main.js              # Entry point
│   └── style.css            # Global styles
├── index.html
├── vite.config.js
└── package.json
```

### Features

- Login/Logout
- Device list dengan real-time updates
- Device detail dengan tabs:
  - GPS dengan peta interaktif (Leaflet)
  - SMS dengan kemampuan kirim SMS
  - Call logs
  - Contacts
  - Installed apps
  - Clipboard logs
  - Notifications
  - WiFi networks
  - Command queue

## Android Client (`/client`)

Android application menggunakan Java.

### Struktur

```
client/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/xploitspy/client/
│           │   ├── MainActivity.java
│           │   ├── Config.java
│           │   ├── service/
│           │   │   └── SpyService.java
│           │   ├── manager/
│           │   │   ├── SocketManager.java
│           │   │   └── DataManager.java
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
│           │       └── BootReceiver.java
│           ├── res/
│           │   └── values/
│           │       └── strings.xml
│           └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── gradle.properties
```

### Components

1. **MainActivity**: Entry point, meminta permissions
2. **SpyService**: Background service yang berjalan terus
3. **SocketManager**: WebSocket connection manager
4. **DataManager**: Mengatur data collection dengan timer
5. **Collectors**: Mengumpulkan data dari berbagai sumber
6. **BootReceiver**: Auto-start service saat boot

## Builder (`/builder`)

Script untuk build APK dengan konfigurasi server.

- `build.sh` - Script untuk generate Config.java dengan IP server
- `README.md` - Dokumentasi builder

## Data Flow

```
Android Device (Client)
    ↓
Collect Data (Collectors)
    ↓
DataManager
    ↓
SocketManager (WebSocket)
    ↓
Server (Node.js)
    ↓
Database (SQLite)
    ↓
WebSocket Broadcast
    ↓
Web Dashboard (Vue)
```

## Technology Stack

### Backend
- Node.js
- Express.js
- Socket.IO
- SQLite3
- bcrypt (password hashing)
- express-session (session management)

### Frontend
- Vue 3
- Vite
- Vue Router
- Axios
- Socket.IO Client
- Leaflet (maps)

### Android
- Java
- Android SDK
- Socket.IO Client for Android
- OkHttp
- Gson

## Security Considerations

⚠️ **PENTING**: 
- Default password harus diubah setelah instalasi
- Gunakan HTTPS di production
- Implement rate limiting
- Validasi input di server
- Encrypt sensitive data
- Secure WebSocket connection (WSS)

## Deployment

### Server
- Deploy dengan PM2 untuk process management
- Setup reverse proxy (nginx) untuk HTTPS
- Configure firewall
- Setup SSL certificate

### Web Dashboard
- Build dengan `npm run build`
- Serve static files dari server Express
- Atau deploy ke CDN/static hosting

### Android Client
- Build APK dengan Gradle
- Sign APK untuk release
- Distribute melalui internal channel

