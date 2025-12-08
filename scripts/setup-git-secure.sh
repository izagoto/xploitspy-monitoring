#!/bin/bash

# Script untuk setup Git repository dengan security yang baik
# Usage: ./scripts/setup-git-secure.sh

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

echo "========================================="
echo "   XploitSPY Git Security Setup"
echo "========================================="
echo ""

cd "$PROJECT_ROOT"

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "⚠️  Git repository belum diinisialisasi"
    read -p "Apakah Anda ingin menginisialisasi Git repository? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git init
        echo "✅ Git repository diinisialisasi"
    else
        echo "❌ Setup dibatalkan"
        exit 1
    fi
fi

echo ""
echo "🔍 Memeriksa file sensitif yang ter-track..."

# Check for sensitive files in git
SENSITIVE_FILES=$(git ls-files 2>/dev/null | grep -E "(firebase-service-account\.json|google-services\.json|\.env$|local\.properties)" || true)

if [ -n "$SENSITIVE_FILES" ]; then
    echo "⚠️  PERINGATAN: File sensitif terdeteksi di Git tracking!"
    echo ""
    echo "File yang ter-track:"
    echo "$SENSITIVE_FILES"
    echo ""
    read -p "Apakah Anda ingin menghapus file-file ini dari Git tracking? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "$SENSITIVE_FILES" | while read -r file; do
            if [ -f "$file" ]; then
                git rm --cached "$file" 2>/dev/null || true
                echo "✅ Removed $file from Git tracking"
            fi
        done
        echo ""
        echo "✅ File sensitif dihapus dari Git tracking"
        echo "⚠️  Jangan lupa commit perubahan ini!"
    fi
else
    echo "✅ Tidak ada file sensitif yang ter-track"
fi

echo ""
echo "🔍 Memeriksa .gitignore..."

# Check if sensitive files are in .gitignore
if grep -q "firebase-service-account.json" .gitignore && \
   grep -q "google-services.json" .gitignore; then
    echo "✅ File sensitif sudah ada di .gitignore"
else
    echo "⚠️  File sensitif belum lengkap di .gitignore"
    echo "   Pastikan .gitignore sudah benar"
fi

echo ""
echo "🔍 Memeriksa file template..."

# Check for example files
if [ -f "server/firebase-service-account.json.example" ] && \
   [ -f "client/app/google-services.json.example" ]; then
    echo "✅ File template sudah ada"
else
    echo "⚠️  File template belum lengkap"
    echo "   Pastikan file .example sudah dibuat"
fi

echo ""
echo "📋 Checklist Security:"
echo ""

# Check if actual sensitive files exist
if [ -f "server/firebase-service-account.json" ]; then
    if git ls-files | grep -q "server/firebase-service-account.json"; then
        echo "❌ server/firebase-service-account.json TER-TRACK (BAHAYA!)"
    else
        echo "✅ server/firebase-service-account.json tidak ter-track"
    fi
else
    echo "ℹ️  server/firebase-service-account.json belum dibuat"
fi

if [ -f "client/app/google-services.json" ]; then
    if git ls-files | grep -q "client/app/google-services.json"; then
        echo "❌ client/app/google-services.json TER-TRACK (BAHAYA!)"
    else
        echo "✅ client/app/google-services.json tidak ter-track"
    fi
else
    echo "ℹ️  client/app/google-services.json belum dibuat"
fi

echo ""
echo "========================================="
echo "   Setup Selesai"
echo "========================================="
echo ""
echo "📝 Langkah selanjutnya:"
echo ""
echo "1. Pastikan repository GitHub Anda adalah PRIVATE"
echo "2. Review file SECURITY.md untuk instruksi lengkap"
echo "3. Jika ada file sensitif yang ter-track, commit perubahan:"
echo "   git add .gitignore"
echo "   git commit -m 'Remove sensitive files from tracking'"
echo ""
echo "4. Setup GitHub repository settings:"
echo "   - Set repository ke PRIVATE"
echo "   - Setup branch protection rules"
echo "   - Review collaborator access"
echo ""

