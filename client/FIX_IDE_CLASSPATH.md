# Fix IDE Classpath Warning

Jika Anda melihat warning "is not on the classpath of project app" di IDE, ini adalah warning biasa yang tidak mempengaruhi build. Berikut cara memperbaikinya:

## ✅ Build Sudah Berhasil

Dari output build terlihat:
```
BUILD SUCCESSFUL in 49s
75 actionable tasks: 75 executed
```

Ini berarti kode sudah benar dan bisa di-build.

## 🔧 Cara Memperbaiki Warning IDE

### Untuk VS Code:

1. **Reload Window:**
   - Tekan `Cmd+Shift+P` (Mac) atau `Ctrl+Shift+P` (Windows/Linux)
   - Ketik: `Java: Clean Java Language Server Workspace`
   - Pilih dan restart

2. **Sync Gradle:**
   - Tekan `Cmd+Shift+P`
   - Ketik: `Java: Rebuild Projects`
   - Tunggu sampai selesai

3. **Install Extension (jika belum):**
   - Extension Pack for Java
   - Gradle for Java

### Untuk IntelliJ IDEA / Android Studio:

1. **Sync Gradle:**
   - Klik kanan pada `build.gradle`
   - Pilih "Sync Gradle Files"
   - Atau: File → Sync Project with Gradle Files

2. **Invalidate Caches:**
   - File → Invalidate Caches / Restart
   - Pilih "Invalidate and Restart"

3. **Refresh Project:**
   - File → Reload Gradle Project

### Manual Fix:

```bash
cd client
./gradlew clean build
```

Kemudian reload window/restart IDE.

## 📝 Catatan

- Warning ini **tidak mempengaruhi build**
- APK tetap bisa di-build dengan sukses
- Hanya masalah konfigurasi IDE untuk autocomplete/error checking
- Build dari command line tetap bekerja normal

## ✅ Verifikasi

Setelah fix, coba build lagi:

```bash
cd client
./gradlew assembleDebug
```

Jika build berhasil, berarti semuanya OK meskipun warning masih muncul di IDE.

