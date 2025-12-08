# Panduan PM2 untuk XploitSPY

## Instalasi PM2

### Opsi 1: Install Global (Recommended untuk Production)

```bash
sudo npm install -g pm2
```

### Opsi 2: Menggunakan PM2 Lokal (Sudah Terinstall)

PM2 sudah terinstall sebagai dev dependency. Gunakan dengan `npx pm2` atau script npm.

## Menjalankan Server

### Menggunakan Script NPM (Recommended)

```bash
cd server

# Start dengan port default (80)
npm run pm2:start:80

# Atau start dengan port custom
PORT=3000 npm run pm2:start

# Atau menggunakan start-server.sh dari folder scripts
./scripts/start-server.sh 3000
```

### Menggunakan PM2 Langsung

```bash
cd server

# Jika PM2 global terinstall
PORT=80 pm2 start index.js --name xploitspy

# Jika menggunakan PM2 lokal
PORT=80 npx pm2 start index.js --name xploitspy
```

## Command PM2

### Status & Monitoring

```bash
# Lihat status
npm run pm2:status
# atau
npx pm2 status

# Lihat logs
npm run pm2:logs
# atau
npx pm2 logs xploitspy

# Monitor real-time
npx pm2 monit
```

### Management

```bash
# Stop server
npm run pm2:stop
# atau
npx pm2 stop xploitspy

# Restart server
npm run pm2:restart
# atau
npx pm2 restart xploitspy

# Delete dari PM2
npx pm2 delete xploitspy
```

### Auto-start pada Boot

```bash
# Generate startup script
npx pm2 startup

# Save current process list
npx pm2 save
```

## Script NPM yang Tersedia

- `npm run pm2:start` - Start server dengan port default
- `npm run pm2:start:80` - Start server di port 80
- `npm run pm2:stop` - Stop server
- `npm run pm2:restart` - Restart server
- `npm run pm2:logs` - Lihat logs
- `npm run pm2:status` - Lihat status

## Troubleshooting

### PM2 tidak ditemukan

Jika error "command not found: pm2":

1. Install global: `sudo npm install -g pm2`
2. Atau gunakan lokal: `npx pm2` atau `npm run pm2:*`

### Port 80 memerlukan root

Untuk menggunakan port 80, jalankan dengan sudo:

```bash
sudo PORT=80 npx pm2 start index.js --name xploitspy
```

Atau gunakan reverse proxy (nginx) untuk forward port 80 ke 3000.

### Server tidak start

1. Cek logs: `npm run pm2:logs`
2. Cek apakah port sudah digunakan: `lsof -i :3000`
3. Stop process yang conflict: `npx pm2 stop xploitspy`

## Production Setup

1. Install PM2 global:
   ```bash
   sudo npm install -g pm2
   ```

2. Start server:
   ```bash
   cd server
   PORT=80 pm2 start index.js --name xploitspy
   ```

3. Setup auto-start:
   ```bash
   pm2 startup
   pm2 save
   ```

4. Monitor:
   ```bash
   pm2 monit
   ```

## Catatan

- PM2 lokal sudah terinstall di `node_modules`
- Gunakan `npx pm2` untuk menjalankan PM2 lokal
- Script npm sudah dikonfigurasi untuk menggunakan PM2 lokal
- Untuk production, disarankan install PM2 global

