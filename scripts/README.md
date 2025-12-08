# Scripts Directory

Folder ini berisi semua script shell (.sh) untuk memudahkan pengelolaan dan eksekusi berbagai tugas dalam proyek XploitSPY.

## Daftar Scripts

### 1. `start-server.sh`
Menjalankan server XploitSPY menggunakan PM2.

**Usage:**
```bash
./scripts/start-server.sh [PORT]
```

**Contoh:**
```bash
./scripts/start-server.sh 3000    # Port 3000
./scripts/start-server.sh 80      # Port 80 (perlu sudo)
```

**Fitur:**
- Otomatis mendeteksi PM2 (global atau local)
- Menyimpan konfigurasi PM2
- Fallback ke Node.js langsung jika PM2 tidak tersedia

---

### 2. `START.sh`
Script untuk memulai aplikasi secara keseluruhan (server + web).

**Usage:**
```bash
./scripts/START.sh
```

---

### 3. `BUILD_AND_INSTALL_APK.sh`
Script untuk membangun APK Android dan menginstalnya ke device yang terhubung.

**Usage:**
```bash
./scripts/BUILD_AND_INSTALL_APK.sh
```

**Fitur:**
- Build APK debug
- Install otomatis ke device via ADB
- Verifikasi instalasi

---

### 4. `FIX_DEVICE_AND_INSTALL.sh`
Script untuk memperbaiki masalah device dan menginstal APK.

**Usage:**
```bash
./scripts/FIX_DEVICE_AND_INSTALL.sh
```

**Fitur:**
- Memperbaiki masalah koneksi ADB
- Menginstal APK ke device
- Verifikasi instalasi

---

### 5. `install_java_and_build.sh`
Script untuk menginstal Java dan membangun APK.

**Usage:**
```bash
./scripts/install_java_and_build.sh
```

**Fitur:**
- Menginstal Java 11+ (jika belum ada)
- Setup environment variables
- Build APK

---

### 6. `QUICK_FIREBASE_SETUP.sh`
Script untuk setup Firebase dengan cepat.

**Usage:**
```bash
./scripts/QUICK_FIREBASE_SETUP.sh
```

**Fitur:**
- Setup Firebase Admin SDK
- Konfigurasi Firestore
- Verifikasi koneksi

---

### 7. `setup-git-secure.sh`
Script untuk setup Git repository dengan security yang baik.

**Usage:**
```bash
./scripts/setup-git-secure.sh
```

**Fitur:**
- Memeriksa file sensitif yang ter-track
- Menghapus file sensitif dari Git tracking
- Verifikasi .gitignore
- Checklist security

**PENTING:** Jalankan script ini sebelum push ke GitHub!

---

## Catatan

- Semua script harus memiliki permission execute: `chmod +x scripts/*.sh`
- Script di folder `builder/` dan `server/node_modules/` adalah script internal dan tidak perlu dipindah
- Pastikan Anda berada di root directory project saat menjalankan script

## Troubleshooting

Jika script tidak bisa dijalankan:
1. Pastikan permission execute sudah diberikan: `chmod +x scripts/*.sh`
2. Pastikan Anda berada di root directory project
3. Cek path yang digunakan dalam script sesuai dengan struktur folder project

