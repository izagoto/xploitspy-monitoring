#!/bin/bash

# XploitSPY Server Start Script
# Usage: ./scripts/start-server.sh [PORT]

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"
SERVER_DIR="$PROJECT_ROOT/server"

PORT=${1:-3000}

echo "========================================="
echo "   Starting XploitSPY Server"
echo "========================================="
echo ""
echo "Port: $PORT"
echo ""

# Change to server directory
cd "$SERVER_DIR" || exit 1

# Check if PM2 is available (local or global)
if command -v pm2 &> /dev/null; then
    echo "Using global PM2..."
    PORT=$PORT pm2 start index.js --name xploitspy
    pm2 save
    echo ""
    echo "✅ Server started with PM2"
    echo "   View logs: pm2 logs xploitspy"
    echo "   Stop: pm2 stop xploitspy"
elif [ -f "./node_modules/.bin/pm2" ]; then
    echo "Using local PM2..."
    PORT=$PORT npx pm2 start index.js --name xploitspy
    npx pm2 save
    echo ""
    echo "✅ Server started with PM2 (local)"
    echo "   View logs: npm run pm2:logs"
    echo "   Stop: npm run pm2:stop"
else
    echo "PM2 not found. Starting server directly..."
    echo "⚠️  For production, install PM2: sudo npm install -g pm2"
    echo ""
    PORT=$PORT node index.js
fi

