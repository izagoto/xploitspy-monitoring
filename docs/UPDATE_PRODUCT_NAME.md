# Update Product Name untuk Device yang Sudah Terdaftar

Jika Product Name menampilkan "Unknown", device perlu re-register dengan product name baru.

## Cara 1: Restart Aplikasi di Device (Paling Mudah)

1. **Force stop aplikasi:**
   ```bash
   adb shell am force-stop com.xploitspy.client
   ```

2. **Start aplikasi lagi:**
   ```bash
   adb shell am start -n com.xploitspy.client/.MainActivity
   ```

3. **Atau restart service:**
   ```bash
   adb shell am startservice -n com.xploitspy.client/.service.SpyService
   ```

Device akan otomatis re-register dengan product name baru.

## Cara 2: Install APK Baru

1. **Build APK baru:**
   ```bash
   cd client
   ./gradlew assembleDebug
   ```

2. **Install APK:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Buka aplikasi** di device dan berikan permissions

Device akan register dengan product name baru.

## Cara 3: Update Manual di Database (Jika perlu)

Jika device tidak bisa re-register, bisa update manual:

```bash
cd server
sqlite3 data/xploitspy.db "UPDATE devices SET product_name = 'a05fxxx' WHERE device_id = '07d075e19cca9a34';"
```

Ganti `a05fxxx` dengan product name yang benar untuk device Anda.

## Verifikasi

Setelah restart, cek:
1. Refresh dashboard
2. Product Name harus muncul (bukan "Unknown")
3. Cek Firebase Console → Firestore → devices → {deviceId} → productName harus ada

