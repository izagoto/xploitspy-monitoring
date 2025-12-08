# Setup Android SDK

## Masalah

Build APK memerlukan Android SDK, tapi SDK tidak ditemukan.

## Solusi

### Opsi 1: Install Android Studio (Recommended)

1. Download Android Studio: https://developer.android.com/studio
2. Install Android Studio
3. Buka Android Studio, akan otomatis download Android SDK
4. SDK akan terinstall di:
   - Mac: `~/Library/Android/sdk`
   - Linux: `~/Android/Sdk`
   - Windows: `C:\Users\YourName\AppData\Local\Android\Sdk`

### Opsi 2: Set ANDROID_HOME

Jika Android SDK sudah terinstall:

```bash
# Mac
export ANDROID_HOME=$HOME/Library/Android/sdk

# Linux
export ANDROID_HOME=$HOME/Android/Sdk

# Add to PATH
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### Opsi 3: Buat local.properties

Buat file `client/local.properties`:

```properties
sdk.dir=/path/to/your/android/sdk
```

Contoh:
```properties
# Mac
sdk.dir=/Users/yourname/Library/Android/sdk

# Linux
sdk.dir=/home/yourname/Android/Sdk
```

## Setelah Setup

Coba build lagi:

```bash
cd client
export JAVA_HOME=/opt/homebrew/opt/openjdk@11
export PATH=$JAVA_HOME/bin:$PATH
./gradlew assembleRelease
```

