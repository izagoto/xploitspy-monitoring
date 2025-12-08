# APK Builder

Script untuk membangun APK Android client dengan konfigurasi server Anda.

## Penggunaan

1. Pastikan Java 11+ terinstall
2. Pastikan Android SDK terinstall dan `ANDROID_HOME` di-set
3. Jalankan script:

```bash
chmod +x build.sh
./build.sh
```

4. Masukkan Server IP dan Port
5. Script akan membuat file konfigurasi
6. Build APK dengan Gradle:

```bash
cd ../client
./gradlew assembleRelease
```

APK akan tersedia di `client/app/build/outputs/apk/release/`

## Catatan

- Pastikan Android project sudah di-setup dengan benar
- File konfigurasi akan dibuat di `client/app/src/main/java/com/xploitspy/client/Config.java`
- Anda perlu mengubah konfigurasi di Android project sesuai kebutuhan

