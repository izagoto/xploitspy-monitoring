# Security Guide - XploitSPY

## ⚠️ PENTING: Perlindungan File Sensitif

File-file berikut mengandung informasi sensitif dan **TIDAK BOLEH** di-commit ke repository Git:

### File Sensitif yang Harus Dilindungi

1. **`server/firebase-service-account.json`**
   - Berisi kredensial Firebase Admin SDK
   - Akses penuh ke Firestore database
   - **JANGAN PERNAH** commit file ini!

2. **`client/app/google-services.json`**
   - Berisi konfigurasi Firebase untuk Android
   - Berisi API keys dan project information
   - **JANGAN PERNAH** commit file ini!

3. **`client/local.properties`**
   - Berisi path Android SDK (bisa berisi informasi sistem)
   - Sudah di-ignore oleh .gitignore

4. **File Environment (`.env`, `.env.local`)**
   - Berisi environment variables sensitif
   - Sudah di-ignore oleh .gitignore

## 🔒 Setup Repository yang Aman

### 1. Inisialisasi Git Repository

```bash
# Inisialisasi repository
git init

# Tambahkan remote (gunakan PRIVATE repository)
git remote add origin https://github.com/USERNAME/xploitspy.git

# Pastikan .gitignore sudah benar
git add .gitignore
git commit -m "Add .gitignore for sensitive files"
```

### 2. Setup File Template

File template (`.example`) sudah disediakan untuk referensi:

- `server/firebase-service-account.json.example`
- `client/app/google-services.json.example`

**Cara menggunakan:**
```bash
# Copy template dan isi dengan data Anda
cp server/firebase-service-account.json.example server/firebase-service-account.json
# Edit file dan isi dengan kredensial Anda yang sebenarnya
```

### 3. Verifikasi File Sensitif Tidak Ter-track

```bash
# Cek apakah file sensitif ter-track oleh Git
git ls-files | grep -E "(firebase-service-account|google-services|\.env)"

# Jika ada file yang ter-track, HAPUS dari Git (tapi tetap di filesystem)
git rm --cached server/firebase-service-account.json
git rm --cached client/app/google-services.json
git commit -m "Remove sensitive files from Git tracking"
```

### 4. Jika File Sensitif Sudah Ter-commit

**⚠️ PERINGATAN:** Jika file sensitif sudah ter-commit ke Git, Anda harus:

1. **Ganti semua kredensial yang sudah ter-expose:**
   - Buat Firebase service account baru
   - Generate API keys baru
   - Update semua kredensial di aplikasi

2. **Hapus dari Git History:**
   ```bash
   # Install git-filter-repo (lebih aman dari BFG)
   pip install git-filter-repo
   
   # Hapus file dari seluruh history
   git filter-repo --path server/firebase-service-account.json --invert-paths
   git filter-repo --path client/app/google-services.json --invert-paths
   
   # Force push (HATI-HATI! Ini akan mengubah history)
   git push origin --force --all
   ```

## 🔐 GitHub Repository Settings

### 1. Buat Repository PRIVATE

**Cara membuat repository private di GitHub:**

1. Buka repository di GitHub
2. Klik **Settings** → **General**
3. Scroll ke bagian **Danger Zone**
4. Klik **Change visibility** → Pilih **Make private**
5. Konfirmasi dengan mengetik nama repository

### 2. Repository Access Control

**⚠️ PENTING: Hanya berikan READ access, JANGAN berikan WRITE atau ADMIN access!**

**Mengatur siapa yang bisa melihat repository:**

1. Buka **Settings** → **Collaborators**
2. Klik **Add people** untuk menambahkan collaborator
3. **PENTING:** Pilih permission level:
   - ✅ **Read**: Hanya bisa melihat (INI YANG HARUS DIPILIH!)
   - ❌ **Write**: Bisa push code (JANGAN PILIH INI!)
   - ❌ **Admin**: Full access (JANGAN PILIH INI!)

**Untuk meminta persetujuan sebelum memberikan akses:**

1. Buka **Settings** → **Collaborators**
2. Nonaktifkan **Allow people to request access** (jika ingin manual approval)
3. Semua request akses akan masuk ke email Anda
4. Anda bisa approve atau reject secara manual
5. **Saat approve, pastikan hanya pilih "Read" permission!**

**Verifikasi Access Control:**

1. Buka **Settings** → **Collaborators**
2. Review semua collaborator di daftar
3. Pastikan semua hanya memiliki **Read** access
4. Jika ada yang memiliki Write/Admin, segera ubah ke Read

📖 **Lihat panduan lengkap:** [docs/ACCESS_CONTROL.md](./docs/ACCESS_CONTROL.md)

### 3. Branch Protection Rules

**Melindungi branch utama dari perubahan tidak sah:**

1. Buka **Settings** → **Branches**
2. Klik **Add rule** untuk branch `main` atau `master`
3. Enable:
   - ✅ Require pull request reviews before merging
   - ✅ Require approvals: 1 (atau lebih)
   - ✅ Dismiss stale pull request approvals when new commits are pushed
   - ✅ Require status checks to pass before merging

### 4. GitHub Secrets (untuk CI/CD)

Jika menggunakan GitHub Actions, gunakan **Secrets** untuk menyimpan kredensial:

1. Buka **Settings** → **Secrets and variables** → **Actions**
2. Klik **New repository secret**
3. Tambahkan secrets:
   - `FIREBASE_SERVICE_ACCOUNT` (isi dengan JSON content)
   - `GOOGLE_SERVICES_JSON` (isi dengan JSON content)

## 🛡️ Best Practices

### 1. Jangan Commit File Sensitif

- ✅ Gunakan `.gitignore` untuk semua file sensitif
- ✅ Gunakan file `.example` sebagai template
- ✅ Verifikasi sebelum commit: `git status`

### 2. Gunakan Environment Variables

Untuk konfigurasi yang tidak terlalu sensitif:

```bash
# server/.env
FIREBASE_PROJECT_ID=your-project-id
PORT=3000
```

### 3. Rotate Credentials Secara Berkala

- Ganti API keys setiap 3-6 bulan
- Ganti service account keys jika ada yang ter-expose
- Monitor penggunaan API keys di Firebase Console

### 4. Monitor Repository Access

- Review collaborator list secara berkala
- Hapus akses untuk user yang tidak lagi aktif
- Monitor audit logs di GitHub (jika menggunakan GitHub Enterprise)

## 📋 Checklist Sebelum Push ke GitHub

- [ ] File `firebase-service-account.json` tidak ter-track
- [ ] File `google-services.json` tidak ter-track
- [ ] File `.env` tidak ter-track
- [ ] Repository sudah di-set ke **PRIVATE**
- [ ] Branch protection rules sudah di-set
- [ ] Collaborator access sudah di-review
- [ ] File template (`.example`) sudah dibuat
- [ ] README sudah di-update dengan instruksi setup

## 🚨 Jika Kredensial Ter-expose

Jika kredensial Anda ter-expose di GitHub:

1. **SEGERA** ganti semua kredensial di Firebase Console
2. Hapus file dari Git history (lihat instruksi di atas)
3. Force push ke repository
4. Monitor aktivitas mencurigakan di Firebase Console
5. Pertimbangkan untuk membuat project Firebase baru

## 📞 Kontak

Jika Anda menemukan file sensitif yang ter-commit, segera:
1. Hapus file dari repository
2. Ganti semua kredensial
3. Review akses repository

---

**Ingat:** Keamanan adalah tanggung jawab bersama. Jangan pernah commit file sensitif ke repository publik!

