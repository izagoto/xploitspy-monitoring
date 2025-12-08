#!/bin/bash

# Quick Firebase Setup Checker

echo "========================================="
echo "   Firebase Setup Checker"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Check google-services.json
echo "📱 Checking Android Firebase config..."
GOOGLE_SERVICES="client/app/google-services.json"
if [ -f "$GOOGLE_SERVICES" ]; then
    # Check if it's dummy or real
    if grep -q "dummy\|000000000000" "$GOOGLE_SERVICES"; then
        echo -e "${YELLOW}⚠️  google-services.json is dummy file${NC}"
        echo "   Please download real file from Firebase Console"
    else
        echo -e "${GREEN}✅ google-services.json found${NC}"
    fi
else
    echo -e "${RED}❌ google-services.json not found${NC}"
    echo "   Download from: Firebase Console → Project Settings → Your apps → Android"
fi

echo ""

# Check firebase-service-account.json
echo "🖥️  Checking Server Firebase config..."
SERVICE_ACCOUNT="server/firebase-service-account.json"
if [ -f "$SERVICE_ACCOUNT" ]; then
    echo -e "${GREEN}✅ firebase-service-account.json found${NC}"
else
    echo -e "${RED}❌ firebase-service-account.json not found${NC}"
    echo "   Download from: Firebase Console → Project Settings → Service Accounts"
fi

echo ""

# Check Firebase Admin SDK
echo "📦 Checking dependencies..."
if [ -d "server/node_modules/firebase-admin" ]; then
    echo -e "${GREEN}✅ firebase-admin installed${NC}"
else
    echo -e "${YELLOW}⚠️  firebase-admin not installed${NC}"
    echo "   Run: cd server && npm install firebase-admin"
fi

echo ""

# Summary
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   Next Steps:"
echo ""

if [ ! -f "$GOOGLE_SERVICES" ] || grep -q "dummy\|000000000000" "$GOOGLE_SERVICES" 2>/dev/null; then
    echo "1. Download google-services.json from Firebase"
    echo "   → Project Settings → Your apps → Android"
    echo "   → Copy to: client/app/google-services.json"
    echo ""
fi

if [ ! -f "$SERVICE_ACCOUNT" ]; then
    echo "2. Download firebase-service-account.json from Firebase"
    echo "   → Project Settings → Service Accounts"
    echo "   → Generate new private key"
    echo "   → Copy to: server/firebase-service-account.json"
    echo ""
fi

echo "3. Rebuild APK:"
echo "   cd client && ./gradlew clean assembleDebug"
echo ""
echo "4. Install APK:"
echo "   adb install -r app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "5. Restart server:"
echo "   cd server && npm run pm2:restart"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

