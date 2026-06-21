# LAPORAN TUGAS BESAR
## MOBILE PROGRAMMING

### **Tugasku**

---

**DISUSUN OLEH:**
* **Abu Harris M** (2350081144)
* **Randy Malik** (2350081125)
* **Safa Salsabila** (2350081134)
* **Veliana Alifah** (2350081127)

**PROGRAM STUDI INFORMATIKA**
**FAKULTAS SAINS DAN INFORMATIKA**
**UNIVERSITAS JENDERAL ACHMAD YANI**
**KOTA CIMAHI**
**TAHUN 2026**

---

## **DAFTAR ISI**
1. [BAB I: PENDAHULUAN](#bab-i-pendahuluan)
   - [I.1 Latar Belakang](#i1-latar-belakang)
   - [I.2 Rumusan Masalah](#i2-rumusan-masalah)
   - [I.3 Tujuan](#i3-tujuan)
   - [I.4 Manfaat](#i4-manfaat)
   - [I.5 Ruang Lingkup](#i5-ruang-lingkup)
2. [BAB II: IMPLEMENTASI](#bab-ii-implementasi)
   - [2.1 Implementasi Aplikasi](#21-implementasi-aplikasi)
     - [2.1.1 Fitur Autentikasi & Login](#211-fitur-autentikasi--login)
     - [2.1.2 Dashboard Utama (Home)](#212-dashboard-utama-home)
     - [2.1.3 Tambah & Edit Tugas](#213-tambah--edit-tugas)
     - [2.1.4 Detail Tugas & Checklist Sub-Tugas](#214-detail-tugas--checklist-sub-tugas)
     - [2.1.5 Sistem Pengingat & Notifikasi (Reminders)](#215-sistem-pengingat--notifikasi-reminders)
     - [2.1.6 Analisis Produktivitas & Statistik](#216-analisis-produktivitas--statistik)
     - [2.1.7 Ekspor Cadangan Data (JSON Backup)](#217-ekspor-cadangan-data-json-backup)
     - [2.1.8 Halaman Profil Pengguna](#218-halaman-profil-pengguna)
   - [2.2 Link Github](#22-link-github)
3. [BAB III: PENUTUP](#bab-iii-penutup)
   - [3.1 Kesimpulan](#31-kesimpulan)

---

## **BAB I: PENDAHULUAN**

### **I.1 Latar Belakang**
Di era digital dan akademik yang serba cepat ini, mahasiswa maupun profesional dituntut untuk dapat mengelola berbagai tugas dan proyek secara efektif. Tugas-tugas dengan deadline yang menumpuk, tingkat prioritas yang berbeda, serta detail instruksi yang kompleks sering kali memicu kecemasan dan ketidakefektifan jika tidak dikelola dengan baik. Sebagian besar orang masih mengandalkan pencatatan tugas secara manual di kertas atau catatan konvensional yang rentan hilang, sulit diurutkan berdasarkan prioritas, dan tidak memiliki sistem pengingat otomatis.

Untuk memecahkan masalah ini, teknologi perangkat bergerak (*mobile device*) khususnya Android menjadi solusi paling tepat karena sifatnya yang portabel dan selalu mendampingi pengguna. Aplikasi manajemen tugas (*task management*) berbasis mobile memungkinkan pengguna mengelompokkan, mengurutkan, melacak, serta mendapatkan pengingat waktu secara dinamis.

Berdasarkan hal tersebut, tim pengembang membangun aplikasi Android bernama **Tugasku**. Aplikasi ini dirancang sebagai alat manajemen tugas pribadi terstruktur yang menggunakan arsitektur MVVM modern dengan penyimpanan lokal Room Database (SQLite), sistem notifikasi alarm otomatis, pembagian sub-tugas (checklist), pencarian & pengurutan lanjutan, grafik produktivitas, serta kemampuan ekspor cadangan data dalam format JSON.

### **I.2 Rumusan Masalah**
1. Bagaimana merancang dan membangun aplikasi Android yang dapat menyimpan dan mengelola data tugas secara lokal dan aman?
2. Bagaimana mengimplementasikan sistem pengingat (notifikasi alarm) yang akurat tepat waktu sebelum batas deadline tugas berakhir?
3. Bagaimana menyajikan fitur sub-tugas (*checklist*) di dalam tugas utama agar kemajuan tugas dapat dipantau lebih mendetail?
4. Bagaimana menampilkan visualisasi analisis tingkat produktivitas pengguna dalam menyelesaikan tugas secara informatif?

### **I.3 Tujuan**
1. Membangun aplikasi Android berbasis Java dengan penyimpanan lokal terstruktur menggunakan Room Database.
2. Menyediakan fitur pengingat deadline menggunakan `AlarmManager` untuk memicu notifikasi lokal secara terjadwal.
3. Mengimplementasikan fitur pencarian, filter prioritas, dan pengurutan (*sorting*) dinamis pada dashboard utama.
4. Menyediakan fitur grafik analisis dan ekspor cadangan data berbasis teks JSON untuk keperluan pencadangan manual pengguna.

### **I.4 Manfaat**
* **Bagi Pengguna:** Membantu mengorganisasi tugas kuliah atau kerja, mengurangi risiko terlewatnya deadline penting, dan memotivasi peningkatan produktivitas harian.
* **Bagi Akademik:** Sebagai implementasi nyata dari konsep pemrograman berorientasi objek, arsitektur perangkat lunak Android modern (MVVM), dan pengelolaan basis data relasional lokal (SQLite/Room).

### **I.5 Ruang Lingkup**
* Platform minimum Android 5.0 (Lollipop) ke atas.
* Bahasa pemrograman Java dengan IDE Android Studio.
* Basis data lokal menggunakan Room Database (SQLite).
* Autentikasi sesi pengguna lokal berbasis `SharedPreferences`.
* Fitur Utama: Manajemen Tugas (CRUD), Sub-Tugas Checklist (CRUD), Alarm Notifikasi Lokal, Pencarian & Sorting, Dashboard Statistik Visual, dan Ekspor Cadangan Data (JSON).

---

## **BAB II: IMPLEMENTASI**

### **2.1 Implementasi Aplikasi**

#### **2.1.1 Fitur Autentikasi & Login**
Fitur login membatasi akses aplikasi agar data tugas setiap pengguna tetap bersifat personal. Pengguna memasukkan username dan password untuk divalidasi ke basis data lokal Room. Setelah login berhasil, detail pengguna disimpan ke dalam `SharedPreferences` sebagai sesi aktif.

#### **2.1.2 Dashboard Utama (Home)**
Dashboard utama menyajikan sapaan dinamis berdasarkan waktu (*Pagi/Siang/Sore/Malam*) serta tanggal hari ini. Dashboard memuat kartu ringkasan jumlah tugas berdasarkan tingkat prioritas (*High, Medium, Low*), bar pencarian teks real-time, tombol *sorting* (berdasarkan tanggal, deadline, atau prioritas), tombol filter cepat, dan daftar tugas aktif dalam bentuk kartu interaktif.

#### **2.1.3 Tambah & Edit Tugas**
Halaman ini memfasilitasi pembuatan tugas baru atau memperbarui tugas yang ada. Pengguna dapat mengisi:
* Judul Tugas dan Catatan (*Note*).
* Pemilih Tanggal & Waktu (*DatePicker & TimePicker*) untuk menentukan deadline.
* Dropdown Spinner untuk tingkat prioritas (*High, Medium, Low*).
* Dropdown Spinner untuk status pengerjaan (*Pending, In Progress, Done*).
* Dropdown Spinner untuk kategori tugas (*Kuliah, Kerja, Pribadi, Belanja, Lainnya*).

#### **2.1.4 Detail Tugas & Checklist Sub-Tugas**
Halaman detail menyajikan semua informasi komprehensif dari tugas terpilih. Pada halaman ini, pengguna dapat mengelola sub-tugas (*checklist*). Pengguna dapat mengetikkan sub-tugas baru dan menambahkannya ke daftar. Setiap sub-tugas memiliki checkbox yang bisa dicentang ketika selesai (memberi efek coret teks secara visual) atau dihapus secara langsung.

#### **2.1.5 Sistem Pengingat & Notifikasi (Reminders)**
Sistem notifikasi menggunakan `AlarmManager` untuk menjadwalkan notifikasi lokal yang dikirimkan oleh `AlarmReceiver`. Ketika waktu sistem saat ini mencapai waktu deadline tugas, sistem akan memicu notifikasi berprioritas tinggi dengan suara dan getaran di status bar ponsel. Mengklik notifikasi tersebut akan mengarahkan pengguna kembali ke detail tugas terkait.

#### **2.1.6 Analisis Produktivitas & Statistik**
Halaman analisis menyajikan metrik kinerja produktivitas pengguna:
* **Tingkat Penyelesaian:** Ditampilkan dengan persentase dan ProgressBar sirkular visual yang membandingkan tugas selesai dengan total tugas.
* **Status Penyelesaian:** Jumlah tugas Selesai vs Tertunda.
* **Distribusi Prioritas:** Grafik batang horizontal yang menampilkan proporsi sebaran tugas berdasarkan prioritas *High, Medium, dan Low*.

#### **2.1.7 Ekspor Cadangan Data (JSON Backup)**
Untuk mencegah kehilangan data, pengguna dapat menekan tombol cadangkan data di halaman profil. Aplikasi akan mengekspor seluruh data tugas dari Room database menjadi struktur teks JSON terformat, lalu memicu *Share Intent* bawaan Android agar pengguna dapat menyimpan teks cadangan tersebut ke email, catatan cloud, atau membagikannya ke aplikasi pesan.

#### **2.1.8 Halaman Profil Pengguna**
Menampilkan nama pengguna, email, badge status akun, dan statistik ringkasan tugas. Halaman ini juga berfungsi sebagai pintu masuk ke menu Analisis Produktivitas, menu Ekspor Cadangan Data JSON, serta tombol Keluar untuk mengakhiri sesi pengguna.

---

### **2.2 Link Github**
* Repositori Proyek: `https://github.com/lucky/Tugasku` *(Contoh)*

---

## **BAB III: PENUTUP**

### **3.1 Kesimpulan**
Aplikasi **Tugasku** merupakan solusi praktis dan efisien untuk mendigitalisasi serta mempermudah manajemen tugas sehari-hari. Dengan menggunakan basis data lokal Room dan sesi SharedPreferences, aplikasi ini mampu bekerja secara offline dengan performa tinggi. Keberadaan fitur-fitur mutakhir seperti notifikasi pengingat otomatis, sub-tugas terperinci, grafik analitik produktivitas, serta kemudahan ekspor cadangan data menjadikan aplikasi **Tugasku** sebagai alat bantu yang sangat andal, aman, dan memikat secara estetika guna meningkatkan efisiensi dan fokus pengguna dalam menyelesaikan tanggung jawab akademis maupun profesional.
