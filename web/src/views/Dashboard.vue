<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div class="header-content">
        <h1>XploitSPY Monitoring System</h1>
        <div class="header-actions">
          <span class="username">{{ username }}</span>
          <button @click="handleLogout" class="btn btn-danger">Logout</button>
        </div>
      </div>
    </header>
    
    <main class="dashboard-main">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📱</div>
          <div class="stat-info">
            <div class="stat-value">{{ devices.length }}</div>
            <div class="stat-label">Connected Devices</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">📍</div>
          <div class="stat-info">
            <div class="stat-value">{{ totalGps }}</div>
            <div class="stat-label">GPS Logs</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">💬</div>
          <div class="stat-info">
            <div class="stat-value">{{ totalSms }}</div>
            <div class="stat-label">SMS Logs</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">📞</div>
          <div class="stat-info">
            <div class="stat-value">{{ totalCalls }}</div>
            <div class="stat-label">Call Logs</div>
          </div>
        </div>
      </div>
      
      <div class="devices-section">
        <div class="card">
          <div class="card-header">
            <h2 class="card-title">Connected Devices</h2>
            <button @click="refreshDevices" class="btn btn-primary">Refresh</button>
          </div>
          
          <div v-if="loading" class="spinner"></div>
          
          <div v-else-if="devices.length === 0" class="empty-state">
            <p>Tidak ada perangkat yang terhubung</p>
          </div>
          
          <table v-else class="table">
            <thead>
              <tr>
                <th>Device ID</th>
                <th>Device Name</th>
                <th>Product Name</th>
                <th>Android Version</th>
                <th>Status</th>
                <th>Last Seen</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="device in devices" :key="device.id">
                <td>{{ device.device_id }}</td>
                <td>{{ device.device_name || 'Unknown' }}</td>
                <td>{{ device.product_name || 'Unknown' }}</td>
                <td>{{ device.android_version || 'Unknown' }}</td>
                <td>
                  <div class="status-cell">
                    <span :class="['status-dot', getDeviceStatus(device) === 'online' ? 'online' : 'offline']"></span>
                    <span class="status-text">{{ getDeviceStatus(device) === 'online' ? 'Online' : 'Offline' }}</span>
                    <span v-if="device.battery_level !== null" class="battery-info">
                      🔋 {{ device.battery_level }}%
                      <span v-if="device.is_charging">⚡</span>
                    </span>
                  </div>
                </td>
                <td>{{ formatDate(device.last_seen) }}</td>
                <td>
                  <button
                    @click="viewDevice(device.device_id)"
                    class="btn btn-primary"
                    style="padding: 6px 12px; font-size: 12px;"
                  >
                    View
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import axios from 'axios';
import io from 'socket.io-client';

export default {
  name: 'Dashboard',
  data() {
    return {
      username: localStorage.getItem('username') || 'admin',
      devices: [],
      loading: true,
      totalGps: 0,
      totalSms: 0,
      totalCalls: 0,
      socket: null
    };
  },
  mounted() {
    this.loadDevices();
    this.loadStats();
    this.connectSocket();
  },
  beforeUnmount() {
    if (this.socket) {
      this.socket.disconnect();
    }
  },
  methods: {
    async loadDevices() {
      this.loading = true;
      try {
        const response = await axios.get('/api/devices');
        this.devices = response.data;
      } catch (error) {
        console.error('Error loading devices:', error);
      } finally {
        this.loading = false;
      }
    },
    async loadStats() {
      try {
        const response = await axios.get('/api/stats');
        this.totalGps = response.data.totalGps || 0;
        this.totalSms = response.data.totalSms || 0;
        this.totalCalls = response.data.totalCalls || 0;
      } catch (error) {
        console.error('Error loading stats:', error);
      }
    },
    connectSocket() {
      this.socket = io();
      
      this.socket.on('connect', () => {
        console.log('Connected to server');
      });
      
      this.socket.on('gps_update', () => {
        this.totalGps++;
      });
      
      this.socket.on('sms_update', () => {
        this.totalSms++;
      });
      
      this.socket.on('call_update', () => {
        this.totalCalls++;
      });
      
      // Listen for device status updates
      this.socket.on('device_update', (data) => {
        // Update device status in realtime
        const device = this.devices.find(d => d.device_id === data.deviceId);
        if (device) {
          device.status = data.status;
          device.battery_level = data.batteryLevel;
          device.is_charging = data.isCharging;
          device.last_seen = new Date().toISOString();
        } else {
          // Reload devices if new device
          this.loadDevices();
        }
      });
    },
    getDeviceStatus(device) {
      // Check if device is online based on status or last_seen
      if (device.status === 'online') {
        return 'online';
      }
      
      // Fallback: check last_seen timestamp
      if (device.last_seen) {
        const lastSeen = new Date(device.last_seen);
        const now = new Date();
        const diffMinutes = (now - lastSeen) / (1000 * 60);
        return diffMinutes < 2 ? 'online' : 'offline';
      }
      
      return 'offline';
    },
    refreshDevices() {
      this.loadDevices();
      this.loadStats();
    },
    viewDevice(deviceId) {
      this.$router.push(`/device/${deviceId}`);
    },
    formatDate(dateString) {
      if (!dateString) return 'Never';
      const date = new Date(dateString);
      return date.toLocaleString('id-ID');
    },
    async handleLogout() {
      try {
        await axios.post('/api/logout');
      } catch (error) {
        console.error('Logout error:', error);
      }
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  position: relative;
  background: #000000;
  overflow-x: hidden;
}

/* Animated Grid Background */
.dashboard::before {
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
.dashboard::after {
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
.dashboard {
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

.dashboard-header {
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

.dashboard-header h1 {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #888;
  font-size: 14px;
}

.dashboard-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 20px;
  position: relative;
  z-index: 10;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(26, 26, 26, 0.9);
  border: 1px solid rgba(74, 158, 255, 0.2);
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(5px);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(74, 158, 255, 0.1), transparent);
  transition: left 0.5s;
}

.stat-card:hover::before {
  left: 100%;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 
    0 10px 30px rgba(0, 0, 0, 0.5),
    0 0 20px rgba(74, 158, 255, 0.3);
  border-color: rgba(74, 158, 255, 0.5);
}

.stat-icon {
  font-size: 40px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #4a9eff 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 20px rgba(74, 158, 255, 0.3);
}

.stat-label {
  font-size: 14px;
  color: #888;
  margin-top: 5px;
}

.devices-section {
  position: relative;
  z-index: 10;
}

.devices-section .card {
  background: rgba(26, 26, 26, 0.9);
  border: 1px solid rgba(74, 158, 255, 0.2);
  backdrop-filter: blur(5px);
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.3),
    0 0 10px rgba(74, 158, 255, 0.1);
  transition: all 0.3s;
}

.devices-section .card:hover {
  border-color: rgba(74, 158, 255, 0.4);
  box-shadow: 
    0 6px 30px rgba(0, 0, 0, 0.4),
    0 0 20px rgba(74, 158, 255, 0.2);
}

.devices-section .card-header {
  border-bottom-color: rgba(74, 158, 255, 0.2);
}

.devices-section .card-title {
  background: linear-gradient(135deg, #4a9eff 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.devices-section .table th {
  background: rgba(42, 42, 42, 0.8);
  border-bottom: 1px solid rgba(74, 158, 255, 0.2);
  color: #fff;
}

.devices-section .table td {
  border-bottom: 1px solid rgba(42, 42, 42, 0.5);
}

.devices-section .table tr:hover {
  background: rgba(74, 158, 255, 0.05);
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #888;
  opacity: 0.8;
}

.status-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}

.status-dot.online {
  background-color: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);
  animation: pulse 2s infinite;
}

.status-dot.offline {
  background-color: #ef4444;
  box-shadow: 0 0 8px rgba(239, 68, 68, 0.5);
}

.status-text {
  font-weight: 500;
  text-transform: capitalize;
  color: #ccc;
}

.battery-info {
  font-size: 11px;
  color: #888;
  margin-left: 4px;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
}
</style>

