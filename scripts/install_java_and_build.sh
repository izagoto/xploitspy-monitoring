#!/bin/bash

# Script untuk Install Java dan Build APK

echo "========================================="
echo "   Install Java & Build APK"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Check if Java is installed
echo "☕ Checking Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo -e "${GREEN}✅ Java found: $JAVA_VERSION${NC}"
    
    # Check version
    VERSION_NUM=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
    if [ "$VERSION_NUM" -lt 11 ]; then
        echo -e "${YELLOW}⚠️  Java version is $VERSION_NUM. Java 11+ recommended${NC}"
        echo "Continuing anyway..."
    fi
else
    echo -e "${RED}❌ Java not found!${NC}"
    echo ""
    
    # Check if Homebrew is available
    if command -v brew &> /dev/null; then
        echo "🍺 Homebrew found. Installing Java via Homebrew..."
        echo ""
        read -p "Install Java 11 now? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo ""
            echo "Installing Java 11..."
            brew install openjdk@11
            
            if [ $? -eq 0 ]; then
                echo ""
                echo "Setting up Java..."
                
                # Try to link Java
                if [ -d "/opt/homebrew/opt/openjdk@11" ]; then
                    export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
                elif [ -d "/usr/local/opt/openjdk@11" ]; then
                    export JAVA_HOME="/usr/local/opt/openjdk@11"
                fi
                
                export PATH=$JAVA_HOME/bin:$PATH
                
                echo -e "${GREEN}✅ Java installed!${NC}"
                java -version
            else
                echo -e "${RED}❌ Installation failed!${NC}"
                echo ""
                echo "Please install Java manually:"
                echo "1. brew install openjdk@11"
                echo "2. Or download from: https://adoptium.net/"
                exit 1
            fi
        else
            echo ""
            echo "Please install Java manually:"
            echo "1. brew install openjdk@11"
            echo "2. Or download from: https://adoptium.net/"
            echo "3. Or install Android Studio (includes Java)"
            exit 1
        fi
    else
        echo ""
        echo "Homebrew not found. Please install Java manually:"
        echo ""
        echo "Option 1: Install Homebrew first"
        echo "  /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
        echo "  brew install openjdk@11"
        echo ""
        echo "Option 2: Download Java from"
        echo "  https://adoptium.net/"
        echo ""
        echo "Option 3: Install Android Studio (includes Java)"
        echo "  https://developer.android.com/studio"
        exit 1
    fi
fi

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/opt/homebrew/opt/openjdk@11" ]; then
        export JAVA_HOME="/opt/homebrew/opt/openjdk@11"
    elif [ -d "/usr/local/opt/openjdk@11" ]; then
        export JAVA_HOME="/usr/local/opt/openjdk@11"
    elif command -v /usr/libexec/java_home &> /dev/null; then
        export JAVA_HOME=$(/usr/libexec/java_home -v 11 2>/dev/null || /usr/libexec/java_home)
    fi
    
    if [ -n "$JAVA_HOME" ]; then
        export PATH=$JAVA_HOME/bin:$PATH
        echo ""
        echo "JAVA_HOME set to: $JAVA_HOME"
    fi
fi

# Verify Java
echo ""
echo "Verifying Java..."
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java still not found after installation!${NC}"
    exit 1
fi

java -version

# Build APK
echo ""
echo "========================================="
echo "   Building APK"
echo "========================================="
echo ""

cd client

if [ ! -f "gradlew" ]; then
    echo -e "${RED}❌ Gradle wrapper not found!${NC}"
    exit 1
fi

chmod +x gradlew

echo "🔨 Building APK (this may take a few minutes)..."
echo ""

./gradlew assembleRelease

if [ $? -ne 0 ]; then
    echo ""
    echo -e "${RED}❌ Build failed!${NC}"
    echo ""
    echo "Common issues:"
    echo "1. Android SDK not found - Install Android Studio"
    echo "2. Check build logs above for details"
    exit 1
fi

# Check if APK was created
APK_PATH="app/build/outputs/apk/release/app-release.apk"
if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}❌ APK not found after build!${NC}"
    exit 1
fi

APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
echo ""
echo -e "${GREEN}✅ APK built successfully!${NC}"
echo "   Location: $(pwd)/$APK_PATH"
echo "   Size: $APK_SIZE"
echo ""

# Check for connected device
echo "📱 Checking for connected device..."
if command -v adb &> /dev/null; then
    DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l | tr -d ' ')
    
    if [ "$DEVICES" -gt 0 ]; then
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
                echo "3. Check dashboard: http://10.254.103.119:3000"
            fi
        fi
    else
        echo -e "${YELLOW}⚠️  No devices connected${NC}"
        echo ""
        echo "APK ready at: $(pwd)/$APK_PATH"
        echo "Install manually: adb install -r $APK_PATH"
    fi
else
    echo -e "${YELLOW}⚠️  ADB not found${NC}"
    echo ""
    echo "APK ready at: $(pwd)/$APK_PATH"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   ✅ Build Complete!"
echo ""
echo "   APK Location:"
echo "   $(pwd)/$APK_PATH"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

