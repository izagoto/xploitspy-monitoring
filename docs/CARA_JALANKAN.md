# Cara Menjalankan XploitSPY

## 🚀 Quick Start

### 1. Menjalankan Server

```bash
cd server
npm run pm2:start
```

Atau dengan port 80:
```bash
npm run pm2:start:80
```

### 2. Akses Web Dashboard

Buka browser dan akses:
```
http://localhost:3000
```

atau jika menggunakan port 80:
```
http://localhost
```

**Login:**
- Username: `admin`
- Password: `password`

---

## 📋 Panduan Lengkap

### Step 1: Pastikan Dependencies Terinstall

```bash
# Install server dependencies
cd server
npm install

# Install web dependencies dan build
cd ../web
npm install
npm run build
```

### Step 2: Menjalankan Server

#### Opsi A: Menggunakan PM2 (Recommended)

```bash
cd server

# Start dengan port default (3000)
npm run pm2:start

# Atau start dengan port 80
npm run pm2:start:80
```

#### Opsi B: Menggunakan Script

```bash
./scripts/start-server.sh 3000    # Port 3000
./scripts/start-server.sh 80      # Port 80 (perlu sudo)
```

#### Opsi C: Langsung dengan Node (Development)

```bash
cd server
PORT=3000 node index.js
```

### Step 3: Verifikasi Server Berjalan

```bash
# Cek status PM2
cd server
npm run pm2:status

# Atau cek dengan curl
curl http://localhost:3000/api/devices
```

### Step 4: Akses Web Dashboard

1. Buka browser (Chrome, Firefox, Safari, dll)
2. Akses: `http://localhost:3000`
3. Login dengan:
   - Username: `admin`
   - Password: `password`

### Step 5: Monitor Server (Optional)

```bash
# Lihat logs real-time
cd server
npm run pm2:logs

# Atau monitor dengan PM2
npx pm2 monit
```

---

## 🔧 Management Server

### Stop Server

```bash
cd server
npm run pm2:stop
```

### Restart Server

```bash
cd server
npm run pm2:restart
```

### Lihat Status

```bash
cd server
npm run pm2:status
```

### Lihat Logs

```bash
cd server
npm run pm2:logs
```

---

## 📱 Build Android Client (Optional)

### 1. Setup Konfigurasi

```bash
cd builder
./build.sh
# Masukkan IP server Anda
```

### 2. Build APK

```bash
cd ../client
./gradlew assembleRelease
```

### 3. Install di Android

APK akan tersedia di:
```
client/app/build/outputs/apk/release/
```

---

## 🌐 Akses dari Device Lain

Jika ingin akses dari device lain di jaringan yang sama:

1. Cari IP address server:
   ```bash
   # Di Mac/Linux
   ifconfig | grep "inet "
   
   # Atau
   ipconfig getifaddr en0
   ```

2. Akses dari device lain:
   ```
   http://YOUR_SERVER_IP:3000
   ```

3. Pastikan firewall mengizinkan port 3000 (atau 80)

---

## ⚠️ Troubleshooting

### Port sudah digunakan

```bash
# Cek port yang digunakan
lsof -i :3000

# Stop process yang menggunakan port
kill -9 <PID>
```

### Server tidak start

```bash
# Cek logs
cd server
npm run pm2:logs

# Cek apakah database folder ada
ls -la server/data/
```

### Dashboard tidak muncul

```bash
# Pastikan web sudah di-build
cd web
npm run build

# Pastikan folder dist ada
ls -la web/dist/
```

### PM2 tidak ditemukan

PM2 sudah terinstall lokal, gunakan:
```bash
npx pm2 start index.js
# atau
npm run pm2:start
```

---

## 📝 Catatan Penting

1. **Port 80 memerlukan root access:**
   ```bash
   sudo PORT=80 npm run pm2:start:80
   ```

2. **Ubah password default** setelah login pertama kali

3. **Untuk production**, gunakan HTTPS dan reverse proxy (nginx)

4. **Database** otomatis dibuat di `server/data/xploitspy.db`

---

## ✅ Checklist

- [ ] Dependencies terinstall (`npm install` di server dan web)
- [ ] Web dashboard sudah di-build (`npm run build` di web)
- [ ] Server berjalan dengan PM2
- [ ] Bisa akses dashboard di browser
- [ ] Login berhasil dengan admin/password

---

## 🎯 Quick Commands

```bash
# Start server
cd server && npm run pm2:start

# Stop server
cd server && npm run pm2:stop

# Restart server
cd server && npm run pm2:restart

# Lihat logs
cd server && npm run pm2:logs

# Status
cd server && npm run pm2:status
```

