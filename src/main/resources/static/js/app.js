/* ========================================
   CabaiDetect - JavaScript Application
   ======================================== */

const API_BASE = '/api';

// ---- Utility Functions ----
function getUser() {
  const user = localStorage.getItem('cabaidetect_user');
  return user ? JSON.parse(user) : null;
}

function setUser(user) {
  localStorage.setItem('cabaidetect_user', JSON.stringify(user));
}

function clearUser() {
  localStorage.removeItem('cabaidetect_user');
  localStorage.removeItem('cabaidetect_token');
}

function requireAuth() {
  if (!getUser()) {
    window.location.href = '/login.html';
    return false;
  }
  return true;
}

function showToast(message, type = 'success') {
  let toast = document.getElementById('toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'toast';
    document.body.appendChild(toast);
  }
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  requestAnimationFrame(() => toast.classList.add('show'));
  setTimeout(() => toast.classList.remove('show'), 3000);
}

function showLoading(text = 'Memproses...') {
  let overlay = document.getElementById('loading-overlay');
  if (!overlay) {
    overlay = document.createElement('div');
    overlay.id = 'loading-overlay';
    overlay.className = 'loading-overlay';
    overlay.innerHTML = `<div class="spinner"></div><p>${text}</p>`;
    document.body.appendChild(overlay);
  } else {
    overlay.querySelector('p').textContent = text;
  }
  overlay.classList.add('show');
}

function hideLoading() {
  const overlay = document.getElementById('loading-overlay');
  if (overlay) overlay.classList.remove('show');
}

async function apiCall(url, options = {}) {
  try {
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    const token = localStorage.getItem('cabaidetect_token');
    if (token) {
      headers['Authorization'] = 'Bearer ' + token;
    }
    const response = await fetch(API_BASE + url, {
      ...options,
      headers
    });
    const data = await response.json();
    return data;
  } catch (error) {
    return { success: false, message: 'Gagal terhubung ke server' };
  }
}

// ---- Auth Functions ----
async function handleRegister(e) {
  e.preventDefault();
  const namaLengkap = document.getElementById('reg-nama').value.trim();
  const emailOrUsername = document.getElementById('reg-email').value.trim();
  const password = document.getElementById('reg-password').value;

  if (!namaLengkap || !emailOrUsername || !password) {
    showToast('Semua field harus diisi', 'error');
    return;
  }
  if (password.length < 6) {
    showToast('Password minimal 6 karakter', 'error');
    return;
  }

  showLoading('Mendaftarkan akun...');
  const result = await apiCall('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ namaLengkap, emailOrUsername, password })
  });
  hideLoading();

  if (result.success) {
    showToast('Registrasi berhasil! Silakan login');
    switchTab('login');
  } else {
    showToast(result.message, 'error');
  }
}

async function handleLogin(e) {
  e.preventDefault();
  const emailOrUsername = document.getElementById('login-email').value.trim();
  const password = document.getElementById('login-password').value;

  if (!emailOrUsername || !password) {
    showToast('Semua field harus diisi', 'error');
    return;
  }

  showLoading('Masuk ke akun...');
  const result = await apiCall('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ emailOrUsername, password })
  });
  hideLoading();

  if (result.success) {
    setUser(result.data);
    showToast('Login berhasil!');
    setTimeout(() => window.location.href = '/home.html', 500);
  } else {
    showToast(result.message, 'error');
  }
}

function switchTab(tab) {
  document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.auth-form').forEach(f => f.style.display = 'none');

  if (tab === 'login') {
    document.getElementById('tab-login').classList.add('active');
    document.getElementById('form-login').style.display = 'block';
  } else {
    document.getElementById('tab-register').classList.add('active');
    document.getElementById('form-register').style.display = 'block';
  }
}

// ---- Detection Functions ----
let selectedFile = null;

function initDetectPage() {
  if (!requireAuth()) return;

  const uploadArea = document.getElementById('photo-upload');
  const fileInput = document.getElementById('foto-input');

  uploadArea.addEventListener('click', () => fileInput.click());
  
  fileInput.addEventListener('change', (e) => {
    if (e.target.files.length > 0) {
      selectedFile = e.target.files[0];
      const preview = document.getElementById('photo-preview');
      const reader = new FileReader();
      reader.onload = (ev) => {
        preview.innerHTML = `<img src="${ev.target.result}" alt="Preview">`;
        preview.style.display = 'block';
        uploadArea.style.display = 'none';
      };
      reader.readAsDataURL(selectedFile);
    }
  });

  // Otomatis deteksi cuaca realtime saat halaman pertama kali dibuka
  fetchRealtimeWeather(true);
}

// ---- Realtime Weather & Environment Detection ----
async function fetchRealtimeWeather(silent = false) {
  if (!silent) showLoading('Mendeteksi lokasi & cuaca realtime...');

  const processCoordinates = async (lat, lon, locationLabel) => {
    try {
      const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,relative_humidity_2m,precipitation,cloud_cover,is_day,wind_speed_10m`;
      const res = await fetch(url);
      const data = await res.json();

      if (!data || !data.current) {
        throw new Error('Data cuaca tidak tersedia');
      }

      const current = data.current;
      const suhu = current.temperature_2m != null ? current.temperature_2m.toFixed(1) : '28.0';
      const kelembaban = current.relative_humidity_2m != null ? Math.round(current.relative_humidity_2m) : '70';
      const curahHujan = current.precipitation != null ? current.precipitation.toFixed(1) : '0.0';
      
      // Estimasi intensitas cahaya (lux) berdasarkan waktu siang/malam dan tutupan awan
      let lux;
      const cloud = current.cloud_cover ?? 50;
      if (current.is_day === 1) {
        // Siang hari: cerah ~75.000 lux, berawan mendung ~15.000 lux
        lux = Math.round(75000 - (cloud * 550));
        lux = Math.max(10000, Math.min(85000, lux));
      } else {
        // Malam hari
        lux = Math.round(1000 - (cloud * 5));
        lux = Math.max(300, lux);
      }

      // Isi input form
      const elSuhu = document.getElementById('suhuUdara');
      const elLembab = document.getElementById('kelembabanUdara');
      const elHujan = document.getElementById('curahHujan');
      const elCahaya = document.getElementById('intensitasCahaya');
      const elPh = document.getElementById('phTanah');
      const elTanahLembab = document.getElementById('kelembabanTanah');
      const elUmur = document.getElementById('umurTanaman');

      if (elSuhu) elSuhu.value = suhu;
      if (elLembab) elLembab.value = kelembaban;
      if (elHujan) elHujan.value = curahHujan;
      if (elCahaya) elCahaya.value = lux;
      
      // Nilai default agronomi tanah & tanaman cabai jika belum diisi
      if (elPh && !elPh.value) elPh.value = '6.5';
      if (elTanahLembab && !elTanahLembab.value) elTanahLembab.value = '65';
      if (elUmur && !elUmur.value) elUmur.value = '45';

      // Update badge cuaca
      const badge = document.getElementById('weather-badge');
      if (badge) {
        const kondisiHari = current.is_day === 1 ? '☀️ Siang' : '🌙 Malam';
        badge.style.display = 'block';
        badge.innerHTML = `
          <strong>📍 Lokasi:</strong> ${locationLabel} (${lat.toFixed(2)}°, ${lon.toFixed(2)}°)<br>
          <strong>🌤️ Cuaca Realtime:</strong> Suhu ${suhu}°C | Kelembaban ${kelembaban}% | Hujan ${curahHujan} mm/hari | Awan ${cloud}% (${kondisiHari}) | Est. Cahaya ${lux.toLocaleString()} lux
        `;
      }

      if (!silent) {
        hideLoading();
        showToast('Cuaca realtime lokasi Anda berhasil dimuat!', 'success');
      }
    } catch (err) {
      if (!silent) {
        hideLoading();
        showToast('Gagal memuat cuaca: ' + err.message, 'error');
      }
    }
  };

  // 1. Coba deteksi via GPS browser
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        processCoordinates(pos.coords.latitude, pos.coords.longitude, 'GPS Anda');
      },
      async (err) => {
        // 2. Jika GPS ditolak atau timeout, fallback ke IP Geolocation
        try {
          const ipRes = await fetch('https://get.geojs.io/v1/ip/geo.json');
          const ipData = await ipRes.json();
          const lat = parseFloat(ipData.latitude) || -6.2088;
          const lon = parseFloat(ipData.longitude) || 106.8456;
          const kota = ipData.city ? `${ipData.city}, ${ipData.country}` : 'Area Anda';
          processCoordinates(lat, lon, kota);
        } catch {
          // Fallback default koordinat Indonesia (Jakarta)
          processCoordinates(-6.2088, 106.8456, 'Jakarta (Default)');
        }
      },
      { timeout: 7000, enableHighAccuracy: true }
    );
  } else {
    processCoordinates(-6.2088, 106.8456, 'Jakarta (Default)');
  }
}

function fillPrototypeData() {
  if (document.getElementById('jenisCabai')) document.getElementById('jenisCabai').value = 'Cabai Rawit';
  if (document.getElementById('bagianTanaman')) document.getElementById('bagianTanaman').value = 'Buah';
  document.getElementById('suhuUdara').value = '28.5';
  document.getElementById('kelembabanUdara').value = '78';
  document.getElementById('phTanah').value = '6.2';
  document.getElementById('kelembabanTanah').value = '65';
  document.getElementById('curahHujan').value = '12';
  document.getElementById('intensitasCahaya').value = '35000';
  document.getElementById('umurTanaman').value = '70';
  document.getElementById('adaHama').value = 'true';

  const badge = document.getElementById('weather-badge');
  if (badge) {
    badge.style.display = 'block';
    badge.innerHTML = `<strong>🧪 Mode Prototype Aktif:</strong> Cabai Rawit (Buah) | Suhu 28.5°C, Lembab 78%, pH 6.2, Hama Ada → Output: Antraknosa Buah (87.5%).`;
  }
  showToast('Data prototype buah cabai diisi!', 'success');
}

function fetchSensorData() {
  fetchRealtimeWeather();
}

async function handleDetect(e) {
  e.preventDefault();
  const user = getUser();
  if (!user) return;

  const jenisCabai = document.getElementById('jenisCabai') ? document.getElementById('jenisCabai').value : 'Cabai Rawit';
  const bagianTanaman = document.getElementById('bagianTanaman') ? document.getElementById('bagianTanaman').value : 'Buah';
  const suhuUdara = document.getElementById('suhuUdara').value;
  const kelembabanUdara = document.getElementById('kelembabanUdara').value;
  const phTanah = document.getElementById('phTanah').value;
  const kelembabanTanah = document.getElementById('kelembabanTanah').value;
  const curahHujan = document.getElementById('curahHujan').value;
  const intensitasCahaya = document.getElementById('intensitasCahaya').value;
  const umurTanaman = document.getElementById('umurTanaman').value;
  const adaHama = document.getElementById('adaHama').value;

  if (!suhuUdara || !kelembabanUdara || !phTanah || !kelembabanTanah || !curahHujan || !intensitasCahaya || !umurTanaman) {
    showToast('Lengkapi semua data lingkungan', 'error');
    return;
  }

  showLoading('Menganalisis penyakit...');

  const formData = new FormData();
  formData.append('userId', user.id);
  formData.append('jenisCabai', jenisCabai);
  formData.append('bagianTanaman', bagianTanaman);
  formData.append('suhuUdara', suhuUdara);
  formData.append('kelembabanUdara', kelembabanUdara);
  formData.append('phTanah', phTanah);
  formData.append('kelembabanTanah', kelembabanTanah);
  formData.append('curahHujan', curahHujan);
  formData.append('intensitasCahaya', intensitasCahaya);
  formData.append('umurTanaman', umurTanaman);
  formData.append('adaHama', adaHama);
  if (selectedFile) formData.append('foto', selectedFile);

  try {
    const headers = {};
    const token = localStorage.getItem('cabaidetect_token');
    if (token) {
      headers['Authorization'] = 'Bearer ' + token;
    }
    const response = await fetch(API_BASE + '/prediction/detect', {
      method: 'POST',
      headers: headers,
      body: formData
    });
    const result = await response.json();
    hideLoading();

    if (result.success) {
      localStorage.setItem('cabaidetect_result', JSON.stringify(result.data));
      window.location.href = '/result.html';
    } else {
      showToast(result.message, 'error');
    }
  } catch {
    hideLoading();
    showToast('Gagal terhubung ke server', 'error');
  }
}

// ---- Result Page ----
function initResultPage() {
  if (!requireAuth()) return;
  const data = JSON.parse(localStorage.getItem('cabaidetect_result'));
  if (!data) {
    window.location.href = '/home.html';
    return;
  }

  document.getElementById('result-disease').textContent = data.penyakitTerdeteksi || '-';
  document.getElementById('result-confidence').textContent = (data.confidenceScore * 100).toFixed(1) + '%';
  document.getElementById('confidence-bar').style.width = (data.confidenceScore * 100) + '%';

  // Plant and Variety Info
  const plantInfo = document.getElementById('result-plant-info');
  if (plantInfo) {
    const jCabai = data.jenisCabai || 'Cabai Rawit';
    plantInfo.textContent = `🌶️ ${jCabai}  •  🎯 Buah Cabai`;
  }

  // Badge color
  const badge = document.getElementById('result-badge');
  if (data.penyakitTerdeteksi && data.penyakitTerdeteksi.toLowerCase() === 'sehat') {
    badge.className = 'badge badge-success';
    badge.textContent = '✓ Buah Sehat';
  } else {
    badge.className = 'badge badge-danger';
    badge.textContent = '⚠ Penyakit Terdeteksi';
  }

  // Environment details
  const details = document.getElementById('result-details');
  const items = [
    { label: 'Varietas Cabai', value: data.jenisCabai || 'Cabai Rawit' },
    { label: 'Objek Analisis', value: 'Buah Cabai' },
    { label: 'Suhu Udara', value: data.suhuUdara + ' °C' },
    { label: 'Kelembaban', value: data.kelembabanUdara + ' %' },
    { label: 'pH Tanah', value: data.phTanah },
    { label: 'K. Tanah', value: data.kelembabanTanah + ' %' },
    { label: 'Curah Hujan', value: data.curahHujan + ' mm' },
    { label: 'Cahaya', value: data.intensitasCahaya + ' lux' },
    { label: 'Umur', value: data.umurTanaman + ' hari' },
    { label: 'Hama', value: data.adaHama ? 'Ada' : 'Tidak' },
  ];
  details.innerHTML = items.map(i => `
    <div class="detail-item">
      <div class="label">${i.label}</div>
      <div class="value">${i.value}</div>
    </div>
  `).join('');

  // Saran
  const saranContainer = document.getElementById('result-saran');
  if (data.saranPenanganan) {
    const saranList = data.saranPenanganan.split('\n').filter(s => s.trim());
    saranContainer.innerHTML = saranList.map(s => `<li>${s.replace(/^[\d\.\-\s]+/, '')}</li>`).join('');
  }
}

// ---- History Page ----
async function initHistoryPage() {
  if (!requireAuth()) return;
  const user = getUser();

  showLoading('Memuat riwayat...');
  const result = await apiCall(`/prediction/history/${user.id}`);
  hideLoading();

  const container = document.getElementById('history-list');

  if (!result.success || !result.data || result.data.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="icon">📋</div>
        <h3>Belum Ada Riwayat</h3>
        <p>Mulai deteksi penyakit buah cabai pertama Anda</p>
        <br>
        <a href="/detect.html" class="btn btn-primary btn-sm">Mulai Deteksi</a>
      </div>
    `;
    return;
  }

  container.innerHTML = result.data.map(item => {
    const date = new Date(item.tanggalDeteksi).toLocaleDateString('id-ID', {
      day: 'numeric', month: 'short', year: 'numeric'
    });
    const score = item.confidenceScore ? (item.confidenceScore * 100).toFixed(0) : '0';
    const isHealthy = item.penyakitTerdeteksi?.toLowerCase() === 'sehat';
    const iconBg = isHealthy ? 'background:rgba(13,124,95,0.15)' : 'background:rgba(232,85,61,0.15)';
    const icon = isHealthy ? '✅' : '🔴';
    const subLabel = `${item.jenisCabai || 'Cabai Rawit'} • Buah Cabai`;

    return `
      <div class="history-item" onclick="viewDetail(${item.id})">
        <div class="history-icon" style="${iconBg}">${icon}</div>
        <div class="history-info">
          <h4>${item.penyakitTerdeteksi || 'Tidak diketahui'}</h4>
          <p>${subLabel}</p>
        </div>
        <div class="history-score">
          <div class="pct">${score}%</div>
          <div class="date">${date}</div>
        </div>
      </div>
    `;
  }).join('');
}

async function viewDetail(id) {
  showLoading('Memuat detail...');
  const result = await apiCall(`/prediction/detail/${id}`);
  hideLoading();

  if (result.success) {
    const data = result.data;
    const mapped = {
      jenisCabai: data.jenisCabai,
      bagianTanaman: data.bagianTanaman,
      penyakitTerdeteksi: data.penyakitTerdeteksi,
      confidenceScore: data.confidenceScore,
      suhuUdara: data.suhuUdara,
      kelembabanUdara: data.kelembabanUdara,
      phTanah: data.phTanah,
      kelembabanTanah: data.kelembabanTanah,
      curahHujan: data.curahHujan,
      intensitasCahaya: data.intensitasCahaya,
      umurTanaman: data.umurTanaman,
      adaHama: data.adaHama,
      saranPenanganan: data.saranPenanganan
    };
    localStorage.setItem('cabaidetect_result', JSON.stringify(mapped));
    window.location.href = '/result.html';
  }
}

// ---- Profile Page ----
function initProfilePage() {
  if (!requireAuth()) return;
  const user = getUser();
  document.getElementById('profile-name').textContent = user.namaLengkap || '-';
  document.getElementById('profile-username').textContent = user.emailOrUsername || '-';
  document.getElementById('profile-avatar-letter').textContent = (user.namaLengkap || 'U')[0].toUpperCase();
  document.getElementById('edit-nama').value = user.namaLengkap || '';
}

async function handleUpdateProfile(e) {
  e.preventDefault();
  const user = getUser();
  const namaLengkap = document.getElementById('edit-nama').value.trim();
  const password = document.getElementById('edit-password').value;

  const body = { namaLengkap };
  if (password) body.password = password;

  showLoading('Menyimpan...');
  const result = await apiCall(`/auth/profile/${user.id}`, {
    method: 'PUT',
    body: JSON.stringify(body)
  });
  hideLoading();

  if (result.success) {
    setUser(result.data);
    showToast('Profil berhasil diperbarui');
    setTimeout(() => location.reload(), 500);
  } else {
    showToast(result.message, 'error');
  }
}

function handleLogout() {
  clearUser();
  window.location.href = '/login.html';
}

// ---- Bottom Nav Highlight ----
function setActiveNav(page) {
  document.querySelectorAll('.nav-item').forEach(n => {
    n.classList.toggle('active', n.dataset.page === page);
  });
}
