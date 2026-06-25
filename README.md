# SEAPEDIA Backend Documentation

## Tech Stack

- Java 21, Spring Boot 3
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA + Hibernate (PostgreSQL)
- Spring Validation (spring-boot-starter-validation)
- Gradle (Kotlin DSL)

---

## Getting Started

---

### Environment Variables
#### .env file
```dotenv
JWT_SECRET=<ISI_SECRET_KAMU>
JWT_EXPIRATION=900000
APP_PORT=8080
```
| Variable            | Keterangan                                      | Contoh                        |
|---------------------|-------------------------------------------------|-------------------------------|
| `APP_PORT`          | Port server (default: `8080`)                   | `8080`                        |
| `JWT_SECRET`        | Secret key JWT, Base64-encoded, min 256-bit     | `(base64 string)`             |
| `JWT_EXPIRATION`    | Durasi token dalam milliseconds (default: 900000 = 15 menit) | `900000`   |
| `DATABASE_URL`      | JDBC URL PostgreSQL                             | `jdbc:postgresql://...`       |
| `DATABASE_USERNAME` | Username database                               | `postgres`                    |
| `DATABASE_PASSWORD` | Password database                               | `secret`                      |

#### .env.development / .env.production file
```dotenv
DATABASE_URL=<ISI_DENGAN_DATABASE_URL_KAMU>
DATABASE_USERNAME=<ISI_USERNAME_DATABASE_KAMU>
DATABASE_PASSWORD=<ISI_PASSWORD_DATABASE_KAMU>
JWT_SECRET=<ISI_SECRET_KAMU>
JWT_EXPIRATION=900000
```
| Variable            | Keterangan                                      | Contoh                        |
|---------------------|-------------------------------------------------|-------------------------------|
| `DATABASE_URL`      | JDBC URL PostgreSQL                             | `jdbc:postgresql://...`       |
| `DATABASE_USERNAME` | Username database                               | `postgres`                    |
| `DATABASE_PASSWORD` | Password database                               | `secret`                      |
| `JWT_SECRET`        | Secret key JWT, Base64-encoded, min 256-bit     | `(base64 string)`             |
| `JWT_EXPIRATION`    | Durasi token dalam milliseconds (default: 900000 = 15 menit) | `900000`   |

### Menjalankan Server

Aplikasi ini membutuhkan **environment variables** (khususnya konfigurasi database) agar bisa berjalan. Ada dua cara menjalankannya:
Terminal tidak otomatis membaca file .env. Pastikan sudah memuat environment variables terlebih dahulu sebelum menjalankan perintah. Sebelum menjalankan perintah berikut, pastikan sudah ada .env.development di root project.

#### Opsi 1: Menggunakan Terminal (Git Bash / WSL / Linux / Mac)

```bash
#1. Muat semua variabel dari file .env.development
set -a && source .env.development && set +a

# 2. Jalankan aplikasi dengan profile dev
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

#### Opsi 2: PowerShell (Windows)
```bash

# 1. Muat semua variabel dari file .env.development
Get-Content .env.development | Foreach-Object { if ($_ -match '^([^#=]+)=(.*)$') { Set-Item -Path "env:$($matches[1])" -Value $matches[2] } }

# 2. Set profile dev
$env:SPRING_PROFILES_ACTIVE="dev"

# 3. Jalankan aplikasi
.\gradlew.bat bootRun
```

#### Opsi 3: Cmd (Windows)
```bash

# 1. Muat semua variabel dari file .env.development
for /f "tokens=1* delims==" %a in (.env.development) do if not "%a"=="" set %a=%b

# 2. Set profile dev
set SPRING_PROFILES_ACTIVE=dev

# 3. Jalankan aplikasi
.\gradlew.bat bootRun
```

### Seed Data (Demo Accounts)

Seed data sudah disertakan di src/main/resources/data.sql dan berjalan otomatis saat server start. Tidak perlu setup manual tambahan.

Pastikan konfigurasi berikut ada di application.properties (sudah ada secara default):
```properties
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

Setiap INSERT memakai ON CONFLICT (id) DO NOTHING, sehingga aman dijalankan berulang kali.

**Demo Accounts**

| Role     | Username   | Email                | Password     | Keterangan                                              |
|----------|------------|----------------------|---------------|---------------------------------------------------------|
| Admin    | `admin`    | admin@example.com    | `admin123`    | `is_admin = true`, akses penuh platform seapedia        |
| Seller   | `seller01` | seller01@example.com | `seller123`   | Pemilik toko Toko Jaya Abadi beserta 2 produk           |
| Buyer    | `buyer01`  | buyer01@example.com  | `buyer123`    | Sudah punya saldo wallet Rp1.000.000 & 1 alamat default |
| Driver   | `driver01` | driver01@example.com | `driver123`   | Role DRIVER yang siap menerima delivery job             |

Login menggunakan endpoint `POST /api/auth/login` dengan body:
```json
{
  "username": "buyer01",
  "password": "buyer123"
}
```


---

## Admin Setup

---

Admin adalah user dengan atribut **isAdmin = true** pada tabel users. Admin tidak memiliki role BUYER, SELLER, atau DRIVER.

Karena tidak ada endpoint register admin dari UI, admin dibuat langsung melalui database. Admin sudah di seed data sebelumnya.
Jika password ada kesalahan, password diganti dengen generate BCrypt hash baru dan update langsung di database.

Untuk generate BCrypt hash, jalankan snippet berikut (ditaruh di main method):

```java
System.out.println(new BCryptPasswordEncoder().encode("admin123"));
```

Setelah user admin dibuat, login menggunakan endpoint `POST /api/auth/login` dengan kredensial tersebut. Token yang dihasilkan akan memiliki activeRole: "ADMIN" secara otomatis.

---

## Business Rules

---

### 1. Single-Store Checkout

Cart hanya boleh berisi produk dari **satu toko** dalam satu waktu.

Enforcement pada codebase ini berlapis dua:

- CartServiceImpl, saat menambah produk ke cart, jika cart sudah berisi produk dari toko lain, throw BadRequestException.
- OrderServiceImpl (computeCheckout), saat checkout, setiap item di-validasi ulang bahwa `product.store.id == cart.storeId`. Jika tidak cocok, throw BadRequestException("Cart contains product from another store").

Untuk membeli dari toko berbeda, user harus mengosongkan cart terlebih dahulu (baik dengan menghapus item cart ataupun dengan checkout.

---

### 2. Discount dan PPN

Urutan kalkulasi yang dilakukan adalah sebagai berikut:

```
subtotal        = Σ (harga × kuantitas) semua item
discountAmount  = diskon yang dikenakan terhadap subtotal
taxBase         = subtotal - discountAmount
taxAmount       = round(taxBase × 12 / 100)
totalAmount     = taxBase + deliveryFee + taxAmount
```

**Keterangan:**
- Diskon dihitung sebelum PPN. Jadi, PPN dikenakan pada harga setelah diskon, bukan harga full.
- Diskon bisa berupa voucher (penggunaan terbatas, bisa expired) atau promo (tidak ada batas penggunaan per kode, tapi bisa expired).
- Hanya satu kode diskon yang bisa digunakan per transaksi. Tidak bisa menggunakan voucher dan promo sekaligus.
- Kode bersifat case-insensitive (DISKON10 sama dengan diskon10).
- Diskon tipe PERCENTAGE memiliki field maxDiscountAmount sebagai batas atas potongan.
- Diskon tidak bisa membuat taxBase negatif (discountAmount dibatasi maksimal sebesar subtotal).

---

### 3. Driver Earning

Driver mendapatkan penghasilan dari delivery fee setiap order yang berhasil diselesaikan.

| Metode Pengiriman | Delivery Fee |
|-------------------|-------------|
| INSTANT           | Rp 25.000   |
| NEXT DAY          | Rp 15.000   |
| REGULAR           | Rp 10.000   |

Saat driver menyelesaikan job (completeJob), sistem otomatis:
1. Mengkreditkan deliveryFee ke wallet driver.
2. Mengkreditkan totalAmount order ke wallet seller.

Tidak ada pembagian fee antara platform seapedia dan mitra aplikasi. Semua delivery fee masuk ke driver, semua totalAmount masuk ke seller.

---

### 4. Overdue SLA dan Auto Return

Sistem mendeteksi order yang melewati SLA berdasarkan metode pengiriman:

| Metode Pengiriman | SLA (sejak order dibuat) |
|-------------------|--------------------------|
| INSTANT           | 120 menit (2 jam)        |
| NEXT DAY          | 2.880 menit (2 hari)     |
| REGULAR           | 10.080 menit (7 hari)    |

Order dianggap overdue jika statusnya **bukan** SELESAI atau DIKEMBALIKAN dan sudah melewati SLA-nya.

Proses otomatis yang terjadi saat overdue:
- Status order diubah menjadi DIKEMBALIKAN.
- Stok produk dikembalikan ke masing-masing item.
- totalAmount dikembalikan ke wallet buyer sebagai refund.
- Perubahan riwayat status tersimpan dengan timestamp dan catatan penyebab.

**Scheduling:** OverdueServiceImpl menjalankan `@Scheduled(fixedRate = 1800000)` yang otomatis memproses overdue setiap 30 menit.

#### Simulasi Waktu

Untuk keperluan demo, terdapat endpoint simulasi waktu. Semua perhitungan SLA menggunakan `SimulationService.now()` bukan `LocalDateTime.now()`.

| Endpoint                              | Method | Deskripsi                                                      |
|---------------------------------------|--------|----------------------------------------------------------------|
| `/api/admin/simulate/advance?minutes=` | POST   | Memajukan waktu sejumlah menit sehingga trigger proses overdue |
| `/api/admin/simulate/reset`           | POST   | Reset offset waktu ke 0                                        |
| `/api/admin/simulate/status`          | GET    | Lihat waktu simulasi saat ini                                  |

Contoh: untuk mensimulasikan order INSTANT yang overdue, majukan waktu sebanyak 121 menit:

```
POST /api/admin/simulate/advance?minutes=121
```

---

## Security

--- 

### SQL Injection

Seluruh query database menggunakan Spring Data JPA dengan method derivation (findBy...) atau @Query dengan parameter binding. Tidak ada query string yang dibangun secara manual, ORM Hibernate menangani parameterisasi secara otomatis.

### XSS (Cross-Site Scripting)

XSS dicegah di frontend (React) melalui auto-escaping bawaan. `{variable}` tidak akan merender HTML. SanitizerUtil.clean() di backend hanya melakukan trim() untuk menghapus whitespace. Tag HTML sengaja tidak di-strip agar konten seperti `<suka suka aja>` tidak rusak.

### Input Validation

Semua request DTO menggunakan anotasi Bean Validation dari spring-boot-starter-validation:

| Anotasi       | Contoh penggunaan                              |
|---------------|------------------------------------------------|
| `@NotBlank`   | Username, email, password wajib diisi          |
| `@Email`      | Format email harus valid                       |
| `@Size`       | Username maks 50 karakter, password min 6      |
| `@Valid`      | Di-trigger di semua `@RequestBody` controller  |

Error validasi dikembalikan sebagai **400 Bad Request** dengan daftar field yang gagal melalui GlobalExceptionHandler.

### Session & Token

| Aspek                   | Behavior                                                                     |
|-------------------------|------------------------------------------------------------------------------|
| Tipe session            | Stateless                                                                    |
| Default expiry          | 15 menit (`JWT_EXPIRATION=900000`)                                           |
| Override expiry         | Set env var `JWT_EXPIRATION` dalam milliseconds                              |
| Refresh token           | Token di-refresh otomatis oleh frontend jika sisa waktu kurang 2 menit       |
| Logout aktif            | Token di-blacklist via `jti` (JWT ID) di in-memory store                     |
| Blacklist persistence   | In-memory, hilang saat server restart                                        |
| Token setelah refresh   | Token lama langsung di-blacklist. Lalu, satu token hanya bisa refresh sekali |

### Role-Based Access Control

Verifikasi role dilakukan server-side melalui dua lapisan:

1. Path-level (`SecurityConfig`):

| Path prefix      | Role yang dibutuhkan |
|------------------|----------------------|
| `/api/admin/**`  | `ROLE_ADMIN`         |
| `/api/seller/**` | `ROLE_SELLER`        |
| `/api/buyer/**`  | `ROLE_BUYER`         |
| `/api/driver/**` | `ROLE_DRIVER`        |

2. Method-level — `@PreAuthorize("hasRole('...')")` di setiap class controller sebagai lapisan kedua.

Role diambil dari field `activeRole` di JWT, bukan semua role yang dimiliki user. User multi-role harus memilih role aktif saat login jika memiliki lebih dari satu role.

### Ownership Protection

| Resource        | Mekanisme validasi                                                                                                                                                             |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Seller products | findByIdAndStoreId(productId, store.id) dimana store diambil dengan findByOwnerId(sellerId). Jadi, produk yang bukan milik toko seller ini akan throw ResourceNotFoundException. |
| Buyer orders    | Service filter order berdasarkan buyerId dari token                                                                                                                            |
| Seller orders   | Service filter order berdasarkan store milik seller aktif                                                                                                                      |
| Driver jobs     | getJobDetail akan menolak akses jika job sudah diambil driver lain                                                                                                             |

---

## API Endpoints

---

### Auth
| Method | Path                      | Akses  | Deskripsi                              |
|--------|---------------------------|--------|----------------------------------------|
| POST   | `/api/auth/register`      | Public | Registrasi user baru                   |
| POST   | `/api/auth/login`         | Public | Login, terima token                    |
| POST   | `/api/auth/select-role`   | Auth   | Pilih role aktif                       |
| POST   | `/api/auth/logout`        | Auth   | Logout, blacklist token                |
| POST   | `/api/auth/refresh`       | Public | Silent refresh token                   |
| GET    | `/api/auth/me`            | Auth   | Profil user aktif                      |
| GET    | `/api/auth/me/summary`    | Auth   | Ringkasan keuangan user aktif          |

---

### Products (Public)
| Method | Path                          | Akses  | Deskripsi                        |
|--------|-------------------------------|--------|----------------------------------|
| GET    | `/api/products`               | Public | Daftar semua produk              |
| GET    | `/api/products/{id}`          | Public | Detail produk                    |
| GET    | `/api/products/store/{storeId}` | Public | Produk berdasarkan toko        |

---

### Stores (Public & Seller)
| Method | Path                  | Akses  | Deskripsi                        |
|--------|-----------------------|--------|----------------------------------|
| GET    | `/api/stores`         | Public | Daftar semua toko                |
| GET    | `/api/stores/{id}`    | Public | Detail toko                      |
| POST   | `/api/seller/store`   | SELLER | Buat toko                        |
| PUT    | `/api/seller/store`   | SELLER | Update toko milik seller aktif   |
| GET    | `/api/seller/store`   | SELLER | Lihat toko milik seller aktif    |

---

### Seller Products
| Method | Path                          | Akses  | Deskripsi                        |
|--------|-------------------------------|--------|----------------------------------|
| GET    | `/api/seller/products`        | SELLER | Daftar produk milik seller aktif |
| POST   | `/api/seller/products`        | SELLER | Tambah produk baru               |
| PUT    | `/api/seller/products/{id}`   | SELLER | Update produk                    |
| DELETE | `/api/seller/products/{id}`   | SELLER | Hapus produk                     |

---

### Seller Orders
| Method | Path                                    | Akses  | Deskripsi                          |
|--------|-----------------------------------------|--------|------------------------------------|
| GET    | `/api/seller/orders/incoming`           | SELLER | Daftar order masuk ke toko         |
| GET    | `/api/seller/orders/{orderId}`          | SELLER | Detail order                       |
| PATCH  | `/api/seller/orders/{orderId}/process`  | SELLER | Proses order (siap kirim)          |
| GET    | `/api/seller/orders/report`             | SELLER | Laporan pendapatan seller          |

---

### Buyer — Cart
| Method | Path                                  | Akses | Deskripsi                          |
|--------|---------------------------------------|-------|------------------------------------|
| GET    | `/api/buyer/cart`                     | BUYER | Lihat isi cart                     |
| POST   | `/api/buyer/cart/items`               | BUYER | Tambah item ke cart                |
| PATCH  | `/api/buyer/cart/items/{cartItemId}`  | BUYER | Update kuantitas item              |
| DELETE | `/api/buyer/cart/items/{cartItemId}`  | BUYER | Hapus satu item dari cart          |
| DELETE | `/api/buyer/cart`                     | BUYER | Kosongkan seluruh cart             |

---

### Buyer Checkout & Orders
| Method | Path                                | Akses | Deskripsi                              |
|--------|-------------------------------------|-------|----------------------------------------|
| POST   | `/api/buyer/discounts/validate`     | BUYER | Validasi kode diskon                   |
| POST   | `/api/buyer/checkout/preview`       | BUYER | Preview kalkulasi harga sebelum bayar  |
| POST   | `/api/buyer/checkout`               | BUYER | Konfirmasi checkout, buat order        |
| GET    | `/api/buyer/orders`                 | BUYER | Daftar order milik buyer               |
| GET    | `/api/buyer/orders/{orderId}`       | BUYER | Detail order                           |
| GET    | `/api/buyer/orders/report`          | BUYER | Laporan pengeluaran buyer              |

---

### Buyer Wallet
| Method | Path                              | Akses | Deskripsi                        |
|--------|-----------------------------------|-------|----------------------------------|
| GET    | `/api/buyer/wallet`               | BUYER | Lihat saldo wallet               |
| POST   | `/api/buyer/wallet/topup`         | BUYER | Top up saldo wallet              |
| GET    | `/api/buyer/wallet/transactions`  | BUYER | Riwayat transaksi wallet         |

---

### Buyer Addresses
| Method | Path                                  | Akses | Deskripsi                        |
|--------|---------------------------------------|-------|----------------------------------|
| GET    | `/api/buyer/addresses`                | BUYER | Daftar alamat milik buyer        |
| POST   | `/api/buyer/addresses`                | BUYER | Tambah alamat baru               |
| PUT    | `/api/buyer/addresses/{id}`           | BUYER | Update alamat                    |
| DELETE | `/api/buyer/addresses/{id}`           | BUYER | Hapus alamat                     |
| PATCH  | `/api/buyer/addresses/{id}/default`   | BUYER | Set alamat sebagai default       |

---

### Driver Jobs
| Method | Path                              | Akses  | Deskripsi                              |
|--------|-----------------------------------|--------|----------------------------------------|
| GET    | `/api/driver/jobs`                | DRIVER | Daftar job tersedia (belum diambil)    |
| GET    | `/api/driver/jobs/active`         | DRIVER | Job yang sedang aktif milik driver     |
| GET    | `/api/driver/jobs/history`        | DRIVER | Riwayat job yang sudah selesai         |
| GET    | `/api/driver/jobs/report`         | DRIVER | Laporan pendapatan driver              |
| GET    | `/api/driver/jobs/{jobId}`        | DRIVER | Detail job (ownership protected)       |
| PATCH  | `/api/driver/jobs/{jobId}/take`   | DRIVER | Ambil job                              |
| PATCH  | `/api/driver/jobs/{jobId}/complete` | DRIVER | Selesaikan job                       |

---

### Discounts (Public)
| Method | Path                          | Akses  | Deskripsi              |
|--------|-------------------------------|--------|------------------------|
| GET    | `/api/discounts/vouchers`     | Public | Daftar semua voucher   |
| GET    | `/api/discounts/vouchers/{id}` | Public | Detail voucher        |
| GET    | `/api/discounts/promos`       | Public | Daftar semua promo     |
| GET    | `/api/discounts/promos/{id}`  | Public | Detail promo           |

---

### Reviews (Public & Auth)
| Method | Path            | Akses | Deskripsi                |
|--------|-----------------|-------|--------------------------|
| GET    | `/api/reviews`  | Public | Daftar review app       |
| POST   | `/api/reviews`  | Auth   | Kirim review app        |

---

### Admin Monitoring
| Method | Path                                    | Akses | Deskripsi                              |
|--------|-----------------------------------------|-------|----------------------------------------|
| GET    | `/api/admin/dashboard`                  | ADMIN | Ringkasan statistik marketplace        |
| GET    | `/api/admin/users`                      | ADMIN | Daftar semua user                      |
| GET    | `/api/admin/orders`                     | ADMIN | Daftar semua order                     |
| GET    | `/api/admin/orders/{orderId}`           | ADMIN | Detail order tertentu                  |
| GET    | `/api/admin/users/{userId}/wallet`      | ADMIN | Wallet milik user tertentu             |
| GET    | `/api/admin/users/{userId}/financial-summary` | ADMIN | Ringkasan keuangan user tertentu |
| GET    | `/api/admin/delivery-jobs`              | ADMIN | Daftar semua delivery job              |

---

### Admin Overdue
| Method | Path                                  | Akses | Deskripsi                              |
|--------|---------------------------------------|-------|----------------------------------------|
| POST   | `/api/admin/overdue/process`          | ADMIN | Trigger proses semua order overdue     |
| POST   | `/api/admin/overdue/process/{orderId}` | ADMIN | Trigger proses satu order overdue     |

---

### Admin Simulasi Waktu
| Method | Path                              | Akses | Deskripsi                              |
|--------|-----------------------------------|-------|----------------------------------------|
| POST   | `/api/admin/simulate/advance`     | ADMIN | Maju waktu sejumlah menit (`?minutes=`) |
| POST   | `/api/admin/simulate/reset`       | ADMIN | Reset offset waktu ke 0               |
| GET    | `/api/admin/simulate/status`      | ADMIN | Lihat waktu simulasi saat ini         |

---

### Admin Discount Management
| Method | Path                      | Akses | Deskripsi          |
|--------|---------------------------|-------|--------------------|
| GET    | `/api/admin/vouchers`     | ADMIN | Daftar semua voucher |
| POST   | `/api/admin/vouchers`     | ADMIN | Buat voucher baru  |
| GET    | `/api/admin/promos`       | ADMIN | Daftar semua promo |
| POST   | `/api/admin/promos`       | ADMIN | Buat promo baru    |