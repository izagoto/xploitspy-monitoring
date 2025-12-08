<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">XploitSPY</h1>
      <p class="login-subtitle">Dashboard Login</p>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label>Username</label>
          <input
            v-model="username"
            type="text"
            class="input"
            placeholder="Masukkan username"
            required
          />
        </div>
        
        <div class="form-group">
          <label>Password</label>
          <input
            v-model="password"
            type="password"
            class="input"
            placeholder="Masukkan password"
            required
          />
        </div>
        
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
        
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Masuk...' : 'Masuk' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Login',
  data() {
    return {
      username: 'admin',
      password: 'password',
      error: '',
      loading: false
    };
  },
  methods: {
    async handleLogin() {
      this.error = '';
      this.loading = true;
      
      try {
        const response = await axios.post('/api/login', {
          username: this.username,
          password: this.password
        });
        
        if (response.data.success) {
          localStorage.setItem('isLoggedIn', 'true');
          localStorage.setItem('username', this.username);
          this.$router.push('/');
        }
      } catch (error) {
        this.error = error.response?.data?.error || 'Login gagal. Periksa kredensial Anda.';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  background: #000000;
  overflow: hidden;
}

/* Animated Grid Background */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(74, 158, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(74, 158, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
  z-index: 0;
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
.login-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(74, 158, 255, 0.5) 50%, 
    transparent 100%);
  animation: scanLine 3s linear infinite;
  z-index: 1;
  box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
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
.login-container {
  background: 
    radial-gradient(circle at 20% 50%, rgba(74, 158, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(139, 92, 246, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 20%, rgba(16, 185, 129, 0.05) 0%, transparent 50%),
    linear-gradient(135deg, #000000 0%, #0a0a0a 25%, #000000 50%, #0a0a0a 75%, #000000 100%);
  background-size: 100% 100%, 100% 100%, 100% 100%, 200% 200%;
  animation: backgroundShift 15s ease infinite;
}

@keyframes backgroundShift {
  0%, 100% {
    background-position: 0% 0%, 0% 0%, 0% 0%, 0% 0%;
  }
  50% {
    background-position: 100% 100%, 100% 100%, 100% 100%, 100% 100%;
  }
}

.login-box {
  background: rgba(26, 26, 26, 0.95);
  border: 1px solid rgba(74, 158, 255, 0.3);
  border-radius: 12px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 
    0 10px 40px rgba(0, 0, 0, 0.8),
    0 0 20px rgba(74, 158, 255, 0.2),
    inset 0 0 20px rgba(74, 158, 255, 0.05);
  position: relative;
  z-index: 10;
  backdrop-filter: blur(10px);
}

/* Glowing border effect */
.login-box::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, 
    rgba(74, 158, 255, 0.5), 
    rgba(139, 92, 246, 0.5), 
    rgba(74, 158, 255, 0.5));
  border-radius: 12px;
  z-index: -1;
  opacity: 0.5;
  animation: borderGlow 3s ease infinite;
}

@keyframes borderGlow {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.7;
  }
}

.login-title {
  font-size: 36px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #4a9eff 0%, #8b5cf6 50%, #4a9eff 100%);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: titleShine 3s ease infinite;
  text-shadow: 0 0 30px rgba(74, 158, 255, 0.5);
  letter-spacing: 2px;
  position: relative;
}

@keyframes titleShine {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.login-title::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #4a9eff, transparent);
  animation: underlineGlow 2s ease infinite;
}

@keyframes underlineGlow {
  0%, 100% {
    opacity: 0.5;
    width: 60px;
  }
  50% {
    opacity: 1;
    width: 100px;
  }
}

.login-subtitle {
  text-align: center;
  color: #888;
  margin-bottom: 30px;
  font-size: 14px;
  letter-spacing: 1px;
  text-transform: uppercase;
  opacity: 0.8;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #ccc;
}

.error-message {
  padding: 12px;
  background: #ff4444;
  color: white;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
}

.input {
  width: 100%;
  padding: 10px;
  border: 1px solid #2a2a2a;
  border-radius: 6px;
  background: #1a1a1a;
  color: #e0e0e0;
  font-size: 14px;
  transition: all 0.3s;
}

.input:focus {
  outline: none;
  border-color: #4a9eff;
  box-shadow: 
    0 0 10px rgba(74, 158, 255, 0.3),
    inset 0 0 10px rgba(74, 158, 255, 0.1);
  background: rgba(26, 26, 26, 0.8);
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  background: #2a2a2a;
  color: #e0e0e0;
}

.btn-primary {
  background: linear-gradient(135deg, #4a9eff 0%, #8b5cf6 100%);
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(74, 158, 255, 0.4);
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #5aaeff 0%, #9b6cf6 100%);
  box-shadow: 0 6px 20px rgba(74, 158, 255, 0.6);
  transform: translateY(-2px);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

