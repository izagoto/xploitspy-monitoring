# Panduan Instalasi XploitSPY

## Prerequisites

### 1. Install Java Runtime Environment 11 atau lebih tinggi

**Debian, Ubuntu, dll:**
```bash
sudo apt-get update
sudo apt-get install openjdk-11-jre
```

**Verifikasi instalasi:**
```bash
java -version
```

### 2. Install NodeJS

Kunjungi [NodeJS Official Website](https://nodejs.org/) dan ikuti instruksi instalasi untuk sistem operasi Anda.

**Verifikasi instalasi:**
```bash
node --version
npm --version
```

### 3. Install PM2 (Process Manager)

```bash
sudo npm install pm2 -g
```

**Verifikasi instalasi:**
```bash
pm2 --version
```

### 4. Server dengan Static IP Address

Pastikan server Anda memiliki IP address statis. Untuk mendapatkan IP publik Anda:

```bash
curl ifconfig.me
```

## Instalasi

### 1. Clone Repository

```bash
git clone https://github.com/XploitWizer/XploitSPY.git
cd XploitSPY
```

### 2. Setup Server Backend

```bash
cd server/
npm install
```

### 3. Build Web Dashboard

```bash
cd ../web/
npm install
npm run build
```

### 4. Start Server

**Opsi 1: Menggunakan PM2 (Recommended)**

```bash
cd ../server/
pm2 start index.js
pm2 startup  # Untuk menjalankan XploitSPY saat startup
pm2 save     # Simpan konfigurasi
```

**Opsi 2: Menggunakan PM2 dengan config file**

```bash
cd ../server/
pm2 start ecosystem.config.js
pm2 startup
pm2 save
```

**Opsi 3: Manual (untuk development)**

```bash
cd ../server/
node index.js
```

### 5. Akses Dashboard

Buka browser dan navigasi ke:

```
http://YOUR_SERVER_IP
```

atau jika menggunakan port default:

```
http://YOUR_SERVER_IP:80
```

**Login Default:**
- Username: `admin`
- Password: `password`

⚠️ **PENTING**: Ubah password default setelah login pertama kali!

## Konfigurasi

### Mengubah Port Server

Edit file `server/index.js` dan ubah:

```javascript
const PORT = process.env.PORT || 80;
```

Atau set environment variable:

```bash
export PORT=3000
pm2 restart xploitspy
```

### Mengubah Password Default

Setelah login pertama kali, Anda dapat mengubah password melalui database atau membuat user baru melalui API.

## Troubleshooting

### Port 80 sudah digunakan

Jika port 80 sudah digunakan, ubah ke port lain:

```bash
export PORT=3000
pm2 restart xploitspy
```

### PM2 tidak start saat boot

Jalankan:

```bash
pm2 startup
pm2 save
```

Ikuti instruksi yang ditampilkan.

### Database error

Pastikan folder `server/data/` memiliki permission yang benar:

```bash
chmod 755 server/data/
```

### Web dashboard tidak muncul

Pastikan Anda sudah build web dashboard:

```bash
cd web/
npm run build
```

## Monitoring

### Melihat log PM2

```bash
pm2 logs xploitspy
```

### Melihat status

```bash
pm2 status
```

### Restart server

```bash
pm2 restart xploitspy
```

### Stop server

```bash
pm2 stop xploitspy
```

## Build Android Client

Lihat dokumentasi di folder `builder/` untuk instruksi membangun APK Android client.

## Security Notes

⚠️ **PENTING**: 
- Ubah password default segera setelah instalasi
- Gunakan HTTPS di production
- Jangan expose server ke internet tanpa firewall yang tepat
- Backup database secara berkala
- Aplikasi ini hanya untuk tujuan edukasi dan pengujian keamanan

