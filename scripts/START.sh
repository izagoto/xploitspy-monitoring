#!/bin/bash

# XploitSPY Quick Start Script

echo "========================================="
echo "   XploitSPY - Quick Start"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if we're in the right directory
if [ ! -d "server" ] || [ ! -d "web" ]; then
    echo "❌ Error: Jalankan script ini dari root directory proyek"
    exit 1
fi

# Step 1: Check dependencies
echo "📦 Checking dependencies..."
if [ ! -d "server/node_modules" ]; then
    echo "   Installing server dependencies..."
    cd server && npm install && cd ..
fi

if [ ! -d "web/node_modules" ]; then
    echo "   Installing web dependencies..."
    cd web && npm install && cd ..
fi

# Step 2: Build web dashboard
echo ""
echo "🏗️  Building web dashboard..."
if [ ! -d "web/dist" ]; then
    cd web && npm run build && cd ..
    echo "   ✅ Web dashboard built"
else
    echo "   ✅ Web dashboard already built"
fi

# Step 3: Start server
echo ""
echo "🚀 Starting server..."
cd server

# Check if server is already running
if npx pm2 list | grep -q "xploitspy"; then
    echo "   ⚠️  Server already running. Restarting..."
    npx pm2 restart xploitspy
else
    PORT=3000 npx pm2 start index.js --name xploitspy
fi

cd ..

# Step 4: Wait a bit for server to start
sleep 2

# Step 5: Check server status
echo ""
echo "📊 Server Status:"
npx pm2 list | grep xploitspy || echo "   Server not found"

# Step 6: Get server info
echo ""
echo "✅ Server started!"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   🌐 Access Dashboard:"
echo "   http://localhost:3000"
echo ""
echo "   👤 Login Credentials:"
echo "   Username: admin"
echo "   Password: password"
echo ""
echo "   📝 Useful Commands:"
echo "   - View logs:    cd server && npm run pm2:logs"
echo "   - Stop server:  cd server && npm run pm2:stop"
echo "   - Restart:      cd server && npm run pm2:restart"
echo "   - Status:       cd server && npm run pm2:status"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Try to open browser (Mac)
if [[ "$OSTYPE" == "darwin"* ]]; then
    read -p "Open browser now? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        open http://localhost:3000
    fi
fi

