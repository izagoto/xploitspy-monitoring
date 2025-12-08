# Fix Gradle Sync & Classpath Issues

## Masalah

Error: "DataManager.java is not on the classpath" dan "Build file has been changed"

## Solusi

### Opsi 1: Sync Gradle di Android Studio

1. Buka Android Studio
2. File → Open → Pilih folder `client/`
3. Tunggu Gradle sync otomatis
4. Atau klik "Sync Project with Gradle Files" (ikon Gradle di toolbar)

### Opsi 2: Sync via Command Line

```bash
cd client
export JAVA_HOME=/opt/homebrew/opt/openjdk@11
export PATH=$JAVA_HOME/bin:$PATH

# Clean dan sync
./gradlew clean
./gradlew build --refresh-dependencies
```

### Opsi 3: Invalidate Caches (Android Studio)

1. File → Invalidate Caches...
2. Pilih "Invalidate and Restart"
3. Tunggu Android Studio restart
4. Tunggu Gradle sync selesai

## Pastikan File Ada

1. `client/app/google-services.json` - Harus ada (download dari Firebase)
2. `client/build.gradle` - Harus ada plugin Google Services
3. `client/app/build.gradle` - Harus ada plugin dan dependencies Firebase

## Verifikasi

Setelah sync, cek:

```bash
cd client
./gradlew tasks
```

Tidak boleh ada error.

## Build APK

Setelah sync berhasil:

```bash
cd client
./gradlew assembleDebug
```

APK akan tersedia di: `app/build/outputs/apk/debug/app-debug.apk`

