# Setup GitHub Repository - Panduan Lengkap

## 🎯 Tujuan

Mengatur repository GitHub agar:
- ✅ File sensitif tidak ter-commit
- ✅ Repository bersifat **PRIVATE** (hanya Anda yang bisa melihat)
- ✅ Orang lain harus minta persetujuan untuk melihat
- ✅ **Hanya Anda yang memiliki write access** - orang lain hanya read-only
- ✅ Akses terkontrol dengan baik

📖 **Lihat juga:** [ACCESS_CONTROL.md](./ACCESS_CONTROL.md) untuk panduan lengkap tentang mengatur akses repository.

## 📋 Langkah-langkah

### 1. Setup Git Repository Lokal

```bash
# Inisialisasi Git (jika belum)
git init

# Setup security check
./scripts/setup-git-secure.sh

# Tambahkan semua file (kecuali yang di .gitignore)
git add .

# Commit pertama
git commit -m "Initial commit: XploitSPY Monitoring System"
```

### 2. Buat Repository di GitHub

1. Buka [GitHub.com](https://github.com) dan login
2. Klik **+** (tanda plus) di kanan atas → **New repository**
3. Isi informasi:
   - **Repository name:** `xploitspy` (⭐ **Rekomendasi** - lihat [REPOSITORY_NAMING.md](./REPOSITORY_NAMING.md) untuk opsi lain)
   - **Description:** "XploitSPY - Android Monitoring & Logging System"
   - **Visibility:** ⚠️ **PILIH "PRIVATE"** (sangat penting!)
   - Jangan centang "Add a README file" (karena sudah ada)
   - Jangan centang "Add .gitignore" (karena sudah ada)
4. Klik **Create repository**

📖 **Lihat rekomendasi nama repository:** [REPOSITORY_NAMING.md](./REPOSITORY_NAMING.md)

### 3. Connect Local Repository ke GitHub

```bash
# Tambahkan remote (ganti USERNAME dengan username GitHub Anda, dan REPO_NAME dengan nama repository)
git remote add origin https://github.com/USERNAME/REPO_NAME.git

# Contoh jika menggunakan nama "xploitspy":
git remote add origin https://github.com/USERNAME/xploitspy.git

# Atau jika menggunakan SSH:
git remote add origin git@github.com:USERNAME/REPO_NAME.git

# Push ke GitHub
git branch -M main
git push -u origin main
```

💡 **Tip:** Ganti `REPO_NAME` dengan nama repository yang Anda pilih (disarankan: `xploitspy`)

### 4. Verifikasi Repository adalah PRIVATE

1. Buka repository di GitHub
2. Cek di bagian atas halaman:
   - Harus ada badge **"Private"** (bukan "Public")
   - Jika masih "Public", lanjut ke langkah 5

### 5. Ubah Repository ke PRIVATE (jika perlu)

1. Buka repository di GitHub
2. Klik **Settings** (di menu atas)
3. Scroll ke bawah ke bagian **Danger Zone**
4. Klik **Change visibility**
5. Pilih **Make private**
6. Ketik nama repository untuk konfirmasi
7. Klik **I understand, change repository visibility**

### 6. Setup Access Control

#### A. Mengatur Collaborator Access (READ-ONLY ONLY)

**⚠️ PENTING: Hanya berikan READ access, JANGAN berikan WRITE atau ADMIN access!**

**Cara memberikan akses READ-ONLY dengan persetujuan:**

1. Buka **Settings** → **Collaborators**
2. Klik **Add people** (atau **Manage access**)
3. Ketik username atau email orang yang ingin diberi akses
4. **PENTING:** Pilih permission level:
   - ✅ **Read**: Hanya bisa melihat code (INI YANG HARUS DIPILIH!)
   - ❌ **Write**: Bisa push code (JANGAN PILIH INI!)
   - ❌ **Admin**: Full access (JANGAN PILIH INI!)
5. Klik **Add [username] to this repository**
6. Orang tersebut akan menerima email invitation
7. Mereka harus **accept invitation** untuk mendapatkan akses
8. **Verifikasi:** Pastikan di daftar collaborator, permission mereka adalah **Read** (bukan Write atau Admin)

**Cara meminta persetujuan sebelum memberikan akses:**

1. Buka **Settings** → **Collaborators**
2. Nonaktifkan opsi **Allow people to request access** (jika ada)
3. Semua request akses akan masuk ke email Anda
4. Anda bisa approve atau reject secara manual dari:
   - Email notification
   - GitHub → Settings → Collaborators → Pending invitations
5. **Saat approve, pastikan hanya pilih "Read" permission!**

**Cara memastikan tidak ada Write Access yang diberikan:**

1. Buka **Settings** → **Collaborators**
2. Review semua collaborator di daftar
3. Pastikan semua collaborator memiliki permission **Read** (bukan Write atau Admin)
4. Jika ada yang memiliki Write atau Admin access:
   - Klik dropdown permission di sebelah nama mereka
   - Pilih **Read** untuk mengubah ke read-only
   - Klik **Change permission**
5. **Verifikasi berkala:** Cek daftar collaborator secara berkala untuk memastikan tidak ada yang memiliki write access

#### B. Mengatur Branch Protection

**Melindungi branch utama dari perubahan tidak sah:**

1. Buka **Settings** → **Branches**
2. Di bagian **Branch protection rules**, klik **Add rule**
3. Isi:
   - **Branch name pattern:** `main` (atau `master`)
   - Centang:
     - ✅ **Require pull request reviews before merging**
     - ✅ **Require approvals:** 1 (atau lebih sesuai kebutuhan)
     - ✅ **Dismiss stale pull request approvals when new commits are pushed**
     - ✅ **Require status checks to pass before merging** (opsional)
4. Klik **Create**

### 7. Setup File Template

**File template sudah disediakan untuk referensi:**

```bash
# Copy template dan isi dengan data Anda
cp server/firebase-service-account.json.example server/firebase-service-account.json
cp client/app/google-services.json.example client/app/google-services.json

# Edit file dan isi dengan kredensial Anda yang sebenarnya
# JANGAN commit file-file ini ke Git!
```

### 8. Verifikasi File Sensitif Tidak Ter-commit

```bash
# Cek apakah file sensitif ter-track
git ls-files | grep -E "(firebase-service-account|google-services|\.env)"

# Jika ada output, file tersebut ter-track (BAHAYA!)
# Hapus dari tracking:
git rm --cached server/firebase-service-account.json
git rm --cached client/app/google-services.json
git commit -m "Remove sensitive files from tracking"
git push
```

### 9. Setup GitHub Secrets (untuk CI/CD - Opsional)

Jika menggunakan GitHub Actions untuk CI/CD:

1. Buka **Settings** → **Secrets and variables** → **Actions**
2. Klik **New repository secret**
3. Tambahkan secrets:
   - Name: `FIREBASE_SERVICE_ACCOUNT`
   - Value: (paste isi file `firebase-service-account.json`)
4. Klik **Add secret**
5. Ulangi untuk `GOOGLE_SERVICES_JSON` jika diperlukan

## 🔒 Checklist Security

Sebelum push ke GitHub, pastikan:

- [ ] Repository sudah di-set ke **PRIVATE**
- [ ] File `firebase-service-account.json` **TIDAK** ter-track
- [ ] File `google-services.json` **TIDAK** ter-track
- [ ] File `.env` **TIDAK** ter-track
- [ ] File template (`.example`) sudah dibuat
- [ ] `.gitignore` sudah benar dan lengkap
- [ ] Branch protection rules sudah di-set
- [ ] **Semua collaborator hanya memiliki READ access (bukan Write atau Admin)**
- [ ] Collaborator access sudah di-review secara berkala
- [ ] Sudah menjalankan `./scripts/setup-git-secure.sh`

## 🚨 Jika File Sensitif Ter-commit

Jika file sensitif sudah ter-commit ke GitHub:

1. **SEGERA** ganti semua kredensial di Firebase Console
2. Hapus file dari Git history (lihat [SECURITY.md](../SECURITY.md))
3. Force push ke repository
4. Monitor aktivitas mencurigakan

## 📞 Bantuan

Jika ada masalah:
- Baca [SECURITY.md](../SECURITY.md) untuk panduan lengkap
- Jalankan `./scripts/setup-git-secure.sh` untuk verifikasi
- Cek GitHub documentation: https://docs.github.com/en/repositories

---

**Ingat:** Keamanan adalah prioritas utama. Jangan pernah commit file sensitif ke repository, bahkan yang private!

