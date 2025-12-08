# Install Java untuk Build APK

## 🔍 Status

Java tidak ditemukan di sistem. Untuk build APK Android, diperlukan Java 11 atau lebih tinggi.

## 🚀 Cara Install Java

### Opsi 1: Install via Homebrew (Paling Mudah - Mac)

```bash
# Install Homebrew jika belum ada
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 11
brew install openjdk@11

# Link Java
sudo ln -sfn /opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-11.jdk

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
export PATH=$JAVA_HOME/bin:$PATH

# Verify
java -version
```

### Opsi 2: Download dari Adoptium (Cross-platform)

1. Download Java 11+ dari: https://adoptium.net/
2. Install sesuai OS Anda
3. Set JAVA_HOME:
   ```bash
   # Mac
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-11.jdk/Contents/Home
   
   # Linux
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
   
   # Windows
   set JAVA_HOME=C:\Program Files\Java\jdk-11
   ```

### Opsi 3: Install via Android Studio (Recommended)

Android Studio sudah include Java, jadi jika install Android Studio, Java akan otomatis terinstall.

1. Download Android Studio: https://developer.android.com/studio
2. Install Android Studio
3. Java akan otomatis tersedia

## ✅ Setelah Install Java

### Verify Installation

```bash
java -version
# Should show: openjdk version "11.x.x" or higher
```

### Build APK

```bash
cd client
./gradlew assembleRelease
```

### Install APK

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 🎯 Quick Install (Mac dengan Homebrew)

Jalankan command berikut:

```bash
# Install Java
brew install openjdk@11

# Set JAVA_HOME untuk session ini
export JAVA_HOME=$(/usr/libexec/java_home -v 11 2>/dev/null || echo "/opt/homebrew/opt/openjdk@11")
export PATH=$JAVA_HOME/bin:$PATH

# Verify
java -version

# Build APK
cd client
./gradlew assembleRelease

# Install
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 📝 Permanent Setup (Tambahkan ke ~/.zshrc)

```bash
# Add to ~/.zshrc
export JAVA_HOME=$(/usr/libexec/java_home -v 11 2>/dev/null || echo "/opt/homebrew/opt/openjdk@11")
export PATH=$JAVA_HOME/bin:$PATH
```

Reload:
```bash
source ~/.zshrc
```

## ⚠️ Catatan

- Java 11 atau lebih tinggi diperlukan untuk build Android APK
- Android Studio sudah include Java, jadi install Android Studio = install Java
- Setelah install Java, build APK akan berjalan dengan baik

