# Access Control Guide - Mengatur Akses Repository

## 🎯 Tujuan

Memastikan bahwa **hanya Anda yang memiliki write access** ke repository. Orang lain hanya bisa melihat (read-only) dan tidak bisa mengubah code.

## ⚠️ Prinsip Dasar

**JANGAN PERNAH memberikan Write atau Admin access kepada siapapun!**

- ✅ **Read Access**: Boleh diberikan untuk orang yang perlu melihat code
- ❌ **Write Access**: JANGAN diberikan kepada siapapun (kecuali Anda sendiri)
- ❌ **Admin Access**: JANGAN diberikan kepada siapapun (kecuali Anda sendiri)

## 📋 Langkah-langkah Mengatur Access Control

### 1. Cek Status Repository

1. Buka repository di GitHub
2. Klik **Settings** → **Collaborators**
3. Lihat daftar **People with access**
4. Pastikan hanya Anda yang memiliki **Owner** atau **Admin** access

### 2. Review Collaborator Access

**Cara melihat siapa saja yang punya akses:**

1. Buka **Settings** → **Collaborators**
2. Di bagian **People with access**, Anda akan melihat:
   - **Owner**: Anda (pemilik repository)
   - **Collaborators**: Orang lain yang punya akses

**Cek permission setiap collaborator:**
- Jika ada yang memiliki **Write** atau **Admin**: ⚠️ **BAHAYA!**
- Semua harus memiliki **Read** saja

### 3. Menghapus Write Access (jika ada)

**Jika ada collaborator yang memiliki Write atau Admin access:**

1. Buka **Settings** → **Collaborators**
2. Cari collaborator yang memiliki Write/Admin access
3. Klik dropdown permission di sebelah nama mereka
4. Pilih **Read** untuk mengubah ke read-only
5. Klik **Change permission**
6. Konfirmasi perubahan

**Atau hapus akses mereka sepenuhnya:**

1. Klik **Remove** di sebelah nama collaborator
2. Konfirmasi penghapusan
3. Mereka tidak akan bisa mengakses repository lagi

### 4. Menambahkan Collaborator dengan Read-Only Access

**Cara menambahkan orang dengan read-only access:**

1. Buka **Settings** → **Collaborators**
2. Klik **Add people**
3. Ketik username atau email GitHub mereka
4. **PENTING:** Di dropdown permission, pilih **Read** (bukan Write atau Admin!)
5. Klik **Add [username] to this repository**
6. Mereka akan menerima email invitation
7. Setelah mereka accept, mereka hanya bisa melihat code (read-only)

### 5. Mengatur Request Access

**Cara mengatur agar semua request akses memerlukan persetujuan:**

1. Buka **Settings** → **Collaborators**
2. Di bagian **Collaborator access**, pastikan:
   - ✅ **Allow people to request access** bisa diaktifkan (untuk kemudahan)
   - Atau nonaktifkan jika tidak ingin ada request otomatis
3. Semua request akan masuk ke email Anda
4. Saat approve request, **PASTIKAN hanya pilih "Read" permission!**

### 6. Branch Protection (Tambahan Keamanan)

**Melindungi branch utama dari perubahan tidak sah:**

1. Buka **Settings** → **Branches**
2. Klik **Add rule** untuk branch `main` atau `master`
3. Enable:
   - ✅ **Require pull request reviews before merging**
   - ✅ **Require approvals:** 1 (atau lebih)
   - ✅ **Restrict who can push to matching branches**
   - ✅ **Do not allow bypassing the above settings**
4. Di bagian **Restrict pushes and merges**, tambahkan hanya username Anda
5. Klik **Create**

**Dengan ini, bahkan jika ada yang punya write access, mereka tidak bisa push langsung ke branch utama tanpa approval.**

## 🔍 Verifikasi Access Control

### Checklist Verifikasi

- [ ] Repository adalah **PRIVATE**
- [ ] Hanya Anda yang memiliki **Owner** atau **Admin** access
- [ ] Semua collaborator lain hanya memiliki **Read** access
- [ ] Tidak ada collaborator dengan **Write** atau **Admin** access
- [ ] Branch protection rules sudah di-set
- [ ] Anda sudah review daftar collaborator secara berkala

### Cara Cek Cepat

```bash
# Buka di browser:
# https://github.com/USERNAME/xploitspy/settings/access

# Atau melalui GitHub CLI (jika terinstall):
gh api repos/USERNAME/xploitspy/collaborators
```

## 🚨 Jika Ada Yang Memiliki Write Access

**Jika Anda menemukan ada collaborator yang memiliki Write atau Admin access:**

1. **SEGERA** ubah permission mereka ke **Read**
2. Review semua commit yang mereka buat (jika ada)
3. Cek apakah ada perubahan yang tidak diinginkan
4. Pertimbangkan untuk menghapus akses mereka sepenuhnya
5. Ganti semua kredensial jika ada yang ter-expose

## 📝 Best Practices

### 1. Review Access Secara Berkala

- Cek daftar collaborator setiap bulan
- Hapus akses untuk user yang tidak lagi aktif
- Pastikan tidak ada yang memiliki write access

### 2. Gunakan Branch Protection

- Setup branch protection untuk branch utama
- Require pull request reviews
- Restrict who can push

### 3. Monitor Repository Activity

- Cek **Insights** → **Network** untuk melihat aktivitas
- Review commit history secara berkala
- Monitor pull requests dan issues

### 4. Dokumentasi Access

- Catat siapa saja yang memiliki akses
- Catat alasan mereka memiliki akses
- Review dan update dokumentasi secara berkala

## 🔐 Default Settings yang Disarankan

**Repository Settings:**
- ✅ Private repository
- ✅ Branch protection enabled
- ✅ Require pull request reviews
- ✅ Restrict pushes to owner only

**Collaborator Settings:**
- ✅ All collaborators: Read-only
- ✅ Owner only: Write/Admin access
- ✅ Request access: Require approval

## 📞 Troubleshooting

### Q: Bagaimana jika saya tidak sengaja memberikan Write access?

**A:** Segera ubah ke Read:
1. Settings → Collaborators
2. Ubah permission ke Read
3. Review commit history mereka

### Q: Bagaimana jika ada orang yang request access?

**A:** 
1. Review request di email atau GitHub
2. Jika perlu, approve dengan **Read-only** access
3. Jika tidak perlu, reject request

### Q: Bagaimana cara memastikan tidak ada yang bisa push code?

**A:**
1. Setup branch protection (lihat langkah 6)
2. Restrict pushes to owner only
3. Require pull request reviews

### Q: Apakah Read-only access aman?

**A:** Ya, dengan Read-only access:
- Mereka hanya bisa melihat code
- Tidak bisa push, commit, atau mengubah apapun
- Tidak bisa mengakses settings atau secrets
- Sangat aman untuk sharing code

---

**Ingat:** Keamanan repository adalah tanggung jawab Anda. Pastikan hanya Anda yang memiliki write access!

