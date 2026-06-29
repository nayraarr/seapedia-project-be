# SEAPEDIA Backend Documentation

## Tech Stack

- Java 21, Spring Boot 3
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA + Hibernate (PostgreSQL)
- Spring Validation (spring-boot-starter-validation)
- Gradle (Kotlin DSL)

---

## Deployment

| Platform | URL                                   |
|----------|---------------------------------------|
| Frontend | https://seapedia-project-fe.vercel.app |
| Backend  | https://seapedia-project-be-production.up.railway.app |
| API Documentation | https://ristek.link/SeapediaAPIDocCompfest18 |

---

## Getting Started


### Environment Variables

#### .env.development / .env.production file
```dotenv
APP_PORT=8080
DATABASE_URL=<ISI_DENGAN_DATABASE_URL_KAMU>
DATABASE_USERNAME=<ISI_USERNAME_DATABASE_KAMU>
DATABASE_PASSWORD=<ISI_PASSWORD_DATABASE_KAMU>
JWT_SECRET=<ISI_SECRET_KAMU>
JWT_EXPIRATION=900000
ALLOWED_ORIGINS=http://localhost:5173
```
| Variable            | Keterangan                                      | Contoh                        |
|---------------------|-------------------------------------------------|-------------------------------|
| `APP_PORT`          | Port server (default: `8080`)                   | `8080`                        |
| `DATABASE_URL`      | JDBC URL PostgreSQL                             | `jdbc:postgresql://...`       |
| `DATABASE_USERNAME` | Username database                               | `postgres`                    |
| `DATABASE_PASSWORD` | Password database                               | `seapedia123`                 |
| `JWT_SECRET`        | Secret key JWT, Base64-encoded, min 256-bit     | `(base64 string)`             |
| `JWT_EXPIRATION`    | Durasi token dalam milliseconds (default: 900000 = 15 menit) | `900000`   |

### Menjalankan Server

Ada dua cara:

#### A. Docker

Pastikan Docker Desktop sudah terinstall dan berjalan. Repository backend (`seapedia-project-be`) dan frontend (`seapedia-project-fe`) harus ada di folder yang sama (sibling).

```bash
# Dari folder seapedia-project-be
docker compose up --build
```

Menjalankan 3 container: PostgreSQL (5432), Spring Boot (8080), Nginx frontend (3000). Seed data berjalan otomatis.

Akses aplikasi di `http://localhost:3000`.

#### B. Local Development (manual)

Buat file `.env.development` (lihat `.env.example`), lalu:

```bash
# Git Bash / WSL / Linux / Mac
set -a && source .env.development && set +a
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

```powershell
# PowerShell (Windows)
Get-Content .env.development | Foreach-Object { if ($_ -match '^([^#=]+)=(.*)$') { Set-Item -Path "env:$($matches[1])" -Value $matches[2] } }
$env:SPRING_PROFILES_ACTIVE="dev"
.\gradlew.bat bootRun
```

--- 

### Seed Data (Demo Accounts)

Seed data sudah disertakan di src/main/resources/data.sql dan berjalan otomatis saat server start. Tidak perlu setup manual tambahan.

Pastikan konfigurasi berikut ada di application.properties (sudah ada secara default):
```properties
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

Setiap INSERT memakai ON CONFLICT (id) DO NOTHING, sehingga aman dijalankan berulang kali.

**Demo Accounts**

| Username | Role                         | Password   | Saldo Wallet | Toko                        | Produk                                                            | Alamat                  |
|----------|------------------------------|------------|-------------|-----------------------------|-------------------------------------------------------------------|-------------------------|
| `admin`  | Admin                        | `admin123` | Rp0         | —                           | —                                                                 | —                       |
| `budi`   | **SELLER + BUYER**           | `budi123`  | Rp500.000   | Toko Segar Laut             | Salmon 500g, Udang Vannamei 1kg, Cumi-Cumi 500g                  | Jl. Kenanga, Jaktim     |
| `sari`   | **BUYER + DRIVER + SELLER**  | `sari123`  | Rp750.000   | Toko Aneka Bumbu & Frozen   | Nugget Ayam 500g, Kentang Goreng 1kg, Sosis Sapi 500g            | Jl. Melati & Kantor, Bandung |
| `dimas`  | **SELLER + DRIVER + BUYER**  | `dimas123` | Rp250.000   | Toko Elektronik             | Power Bank 10000mAh, Earphone Bluetooth, Kabel USB-C 2m          | —                       |
| `rina`   | **BUYER + SELLER + DRIVER**  | `rina123`  | Rp1.000.000 | Toko Fashion                | Topi Snapback, Tote Bag Kanvas, Scarf Polos                       | Jl. Anggrek, Surabaya   |
| `arya`   | **SELLER + BUYER**           | `arya123`  | Rp300.000   | Aria Fashion                | Jam Tangan Pria, Kacamata Hitam                                   | Jl. Flamboyan, Semarang |
| `sinta`  | **SELLER + BUYER**           | `sinta123` | Rp450.000   | Sinta Elektronik            | Mouse Wireless, Keyboard Mekanik, Speaker Bluetooth               | Jl. Mawar, Yogyakarta   |

> Login menggunakan endpoint `POST /api/auth/login`. Jika user memiliki lebih dari satu role, sistem akan mengarahkan ke halaman **select-role** untuk memilih role aktif.
>
> Contoh login sebagai `budi`:
> ```json
> { "username": "budi", "password": "budi123" }
> ```


---

## Admin Setup

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


### 1. Single-Store Checkout

Cart hanya boleh berisi produk dari **satu toko** dalam satu waktu.

Enforcement pada codebase ini berlapis dua:

- CartServiceImpl, saat menambah produk ke cart, jika cart sudah berisi produk dari toko lain, throw BadRequestException.
- OrderServiceImpl (computeCheckout), saat checkout, setiap item di-validasi ulang bahwa `product.store.id == cart.storeId`. Jika tidak cocok, throw BadRequestException("Cart contains product from another store").

Untuk membeli dari toko berbeda, user harus mengosongkan cart terlebih dahulu (baik dengan menghapus item cart ataupun dengan checkout.


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


### SQL Injection

Seluruh query database menggunakan Spring Data JPA dengan method derivation (findBy...) atau @Query dengan parameter binding. Tidak ada query string yang dibangun secara manual, ORM Hibernate menangani parameterisasi secara otomatis.

### XSS (Cross-Site Scripting)

Input teks dari user (nama produk, deskripsi) diproses melalui `SanitizerUtil.clean()` sebelum disimpan ke database. Saat ini sanitizer melakukan `trim()`. Untuk produksi, disarankan mengintegrasikan library Jsoup untuk strip HTML tags.

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
| Tipe session            | Stateless — tidak ada HTTP session, hanya JWT                            |
| Default expiry          | 15 menit (`JWT_EXPIRATION=900000`)                                       |
| Override expiry         | Set env var `JWT_EXPIRATION` dalam milliseconds                              |
| Refresh token           | Token di-refresh otomatis oleh frontend jika sisa waktu kurang 2 menit       |
| Logout aktif            | Token di-blacklist via `jti` (JWT ID) di in-memory store                     |
| Blacklist persistence   | In-memory — hilang saat server restart (acceptable untuk development)    |
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

### Auth
| Method | Path                      | Akses  | Deskripsi                              |
|--------|---------------------------|--------|----------------------------------------|
| POST   | `/api/auth/register`      | Public | Registrasi user baru                   |
| POST   | `/api/auth/login`         | Public | Login, terima token                    |
| POST   | `/api/auth/select-role`   | Auth   | Pilih role aktif                       |
| POST   | `/api/auth/logout`        | Auth   | Logout, blacklist token                |
| POST   | `/api/auth/refresh`       | Public* | Silent refresh token                   |
| GET    | `/api/auth/me`            | Auth   | Profil user aktif                      |
| PATCH  | `/api/auth/me`            | Auth   | Update profil (fullName)               |
| GET    | `/api/auth/me/summary`    | Auth   | Ringkasan keuangan user aktif          |

> *Refresh tetap membutuhkan token valid di header Authorization — token lama digunakan untuk validasi dan penerbitan token baru.


### Products (Public)
| Method | Path                          | Akses  | Deskripsi                        |
|--------|-------------------------------|--------|----------------------------------|
| GET    | `/api/products`               | Public | Daftar semua produk              |
| GET    | `/api/products/{id}`          | Public | Detail produk                    |
| GET    | `/api/products/{id}/similar`  | Public | Produk serupa (kategori sama)    |
| GET    | `/api/products/store/{storeId}` | Public | Produk berdasarkan toko        |


### Stores (Public & Seller)
| Method | Path                  | Akses  | Deskripsi                        |
|--------|-----------------------|--------|----------------------------------|
| GET    | `/api/stores`         | Public | Daftar semua toko                |
| GET    | `/api/stores/{id}`    | Public | Detail toko                      |
| POST   | `/api/seller/store`   | SELLER | Buat toko                        |
| PUT    | `/api/seller/store`   | SELLER | Update toko milik seller aktif   |
| GET    | `/api/seller/store`   | SELLER | Lihat toko milik seller aktif    |


### Seller Products
| Method | Path                          | Akses  | Deskripsi                        |
|--------|-------------------------------|--------|----------------------------------|
| GET    | `/api/seller/products`        | SELLER | Daftar produk milik seller aktif |
| POST   | `/api/seller/products`        | SELLER | Tambah produk baru               |
| PUT    | `/api/seller/products/{id}`   | SELLER | Update produk                    |
| DELETE | `/api/seller/products/{id}`   | SELLER | Hapus produk                     |


### Seller Orders
| Method | Path                                    | Akses  | Deskripsi                          |
|--------|-----------------------------------------|--------|------------------------------------|
| GET    | `/api/seller/orders/incoming`           | SELLER | Daftar order masuk ke toko         |
| GET    | `/api/seller/orders/{orderId}`          | SELLER | Detail order                       |
| PATCH  | `/api/seller/orders/{orderId}/process`  | SELLER | Proses order (siap kirim)          |
| GET    | `/api/seller/orders/report`             | SELLER | Laporan pendapatan seller          |


### Buyer — Cart
| Method | Path                                  | Akses | Deskripsi                          |
|--------|---------------------------------------|-------|------------------------------------|
| GET    | `/api/buyer/cart`                     | BUYER | Lihat isi cart                     |
| POST   | `/api/buyer/cart/items`               | BUYER | Tambah item ke cart                |
| PATCH  | `/api/buyer/cart/items/{cartItemId}`  | BUYER | Update kuantitas item              |
| DELETE | `/api/buyer/cart/items/{cartItemId}`  | BUYER | Hapus satu item dari cart          |
| DELETE | `/api/buyer/cart`                     | BUYER | Kosongkan seluruh cart             |


### Buyer Checkout & Orders
| Method | Path                                | Akses | Deskripsi                              |
|--------|-------------------------------------|-------|----------------------------------------|
| POST   | `/api/buyer/discounts/validate`     | BUYER | Validasi kode diskon                   |
| POST   | `/api/buyer/checkout/preview`       | BUYER | Preview kalkulasi harga sebelum bayar  |
| POST   | `/api/buyer/checkout`               | BUYER | Konfirmasi checkout, buat order        |
| GET    | `/api/buyer/orders`                 | BUYER | Daftar order milik buyer               |
| GET    | `/api/buyer/orders/{orderId}`       | BUYER | Detail order                           |
| GET    | `/api/buyer/orders/report`          | BUYER | Laporan pengeluaran buyer              |


### Buyer Wallet
| Method | Path                              | Akses | Deskripsi                        |
|--------|-----------------------------------|-------|----------------------------------|
| GET    | `/api/buyer/wallet`               | BUYER | Lihat saldo wallet               |
| POST   | `/api/buyer/wallet/topup`         | BUYER | Top up saldo wallet              |
| GET    | `/api/buyer/wallet/transactions`  | BUYER | Riwayat transaksi wallet         |


### Buyer Addresses
| Method | Path                                  | Akses | Deskripsi                        |
|--------|---------------------------------------|-------|----------------------------------|
| GET    | `/api/buyer/addresses`                | BUYER | Daftar alamat milik buyer        |
| POST   | `/api/buyer/addresses`                | BUYER | Tambah alamat baru               |
| PUT    | `/api/buyer/addresses/{id}`           | BUYER | Update alamat                    |
| DELETE | `/api/buyer/addresses/{id}`           | BUYER | Hapus alamat                     |
| PATCH  | `/api/buyer/addresses/{id}/default`   | BUYER | Set alamat sebagai default       |


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


### Discounts (Public)
| Method | Path                          | Akses  | Deskripsi              |
|--------|-------------------------------|--------|------------------------|
| GET    | `/api/discounts/vouchers`     | Public | Daftar semua voucher   |
| GET    | `/api/discounts/vouchers/{id}` | Public | Detail voucher        |
| GET    | `/api/discounts/promos`       | Public | Daftar semua promo     |
| GET    | `/api/discounts/promos/{id}`  | Public | Detail promo           |


### Reviews (Public & Auth)
| Method | Path            | Akses | Deskripsi                |
|--------|-----------------|-------|--------------------------|
| GET    | `/api/reviews`  | Public | Daftar review app       |
| POST   | `/api/reviews`  | Auth   | Kirim review app        |


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


### Admin Overdue
| Method | Path                                  | Akses | Deskripsi                              |
|--------|---------------------------------------|-------|----------------------------------------|
| POST   | `/api/admin/overdue/process`          | ADMIN | Trigger proses semua order overdue     |
| POST   | `/api/admin/overdue/process/{orderId}` | ADMIN | Trigger proses satu order overdue     |


### Admin Simulasi Waktu
| Method | Path                              | Akses | Deskripsi                              |
|--------|-----------------------------------|-------|----------------------------------------|
| POST   | `/api/admin/simulate/advance`     | ADMIN | Maju waktu sejumlah menit (`?minutes=`) |
| POST   | `/api/admin/simulate/reset`       | ADMIN | Reset offset waktu ke 0               |
| GET    | `/api/admin/simulate/status`      | ADMIN | Lihat waktu simulasi saat ini         |


### Admin Discount Management
| Method | Path                      | Akses | Deskripsi          |
|--------|---------------------------|-------|--------------------|
| GET    | `/api/admin/vouchers`     | ADMIN | Daftar semua voucher |
| POST   | `/api/admin/vouchers`     | ADMIN | Buat voucher baru  |
| GET    | `/api/admin/promos`       | ADMIN | Daftar semua promo |
| POST   | `/api/admin/promos`       | ADMIN | Buat promo baru    |

---

## Testing Guide


### Persiapan (lengkapnya untuk frontend ada di README frontend)

1. Jalankan backend (`./gradlew bootRun`) dan frontend (`npm run dev`).
2. Pastikan database PostgreSQL sudah berjalan dan environment variables sudah dikonfigurasi.
3. Pastikan user admin sudah ada di database (lihat bagian Admin Setup di README backend).


### Skenario 0: Guest bisa melihat katalog & review aplikasi
Step di bawah ini dilakukan tanpa login atau register terlebih dahulu:

1. buka `/products` lalu guest bisa melihat daftar produk dan masuk ke detail produknya (`/products/{id}`).
2. Buka `/stores` dan detail toko lalu guest bisa melihat profil toko tanpa login.
3. Di endpoint `/` atau Home, cari section Ulasan Aplikasi lalu guest mengisi nama, rating, dan komentar dan bisa submit review tanpa login.
4. Refresh halaman lalu review yang baru disubmit tampil di antarmuka.
5. Coba submit review dengan komentar berisi tag HTML/script, contoh: `<script>alert(1)</script>` atau `<b>test</b>`. Frontend menampilkan sebagai text biasa dan tidak dieksekusi sebagai HTML.


### Skenario 1: Registrasi dan login multi-Role

1. Buka `/register`, daftarkan user dengan role BUYER, SELLER, dan DRIVER sekaligus.
2. Login akun tersebut. Karena memiliki beberapa role, user diarahkan ke `/select-role`.
3. Pilih role SELLER lalu token baru diberikan dengan `activeRole: SELLER`.
4. Coba akses `/dashboard/buyer` secara manual di URL bar. Platform akan redirect ke `/dashboard/seller` (bukan error).


### Skenario 2: Seller bisa melakukan setup toko dan produk

1. Login sebagai Seller.
2. Buat toko dengan nama tertentu.
3. Coba edit toko dengan nama yang sama menggunakan akun seller lain. Sistem pasti akan menolak (nama toko harus unik).
4. Buka Kelola Produk, tambahkan minimal 2 produk dengan stok lebih dari 0.
5. Buka `/products` (sebagai guest/buyer). Kedua produk yang baru dibuat muncul di katalog publik.
6. Coba update/edit salah satu produk (ubah harga/stok) dan hapus produk lainnya. Perubahan ini tercermin di katalog publik.


### Skenario 3: Buyer melakukan top up, kelola address, dan checkout single-store

1. Login sebagai Buyer (akun berbeda dari Seller).
2. Buka Wallet, lakukan top-up sehingga saldo bertambah, transaksi tercatat di riwayat wallet.
3. Dari dashboard, buka alamat pengiriman, tambahkan alamat baru dan set sebagai default.
4. Buka halaman produk, tambahkan produk dari dari suatu toko ke cart.
5. Coba tambahkan produk dari toko lain maka muncul banner konflik di halaman cart, tombol checkout nonaktif.
6. Kosongkan cart, tambah produk dari satu toko saja.
7. Buka halaman cart, pilih alamat dan metode pengiriman.
8. Masukkan kode diskon Voucher lalu lihat preview. Ulangi dengan kode Promo di transaksi lain untuk membandingkan kedua tipe diskon.
9. Preview checkout menampilkan subtotal, discountAmount, delivery fee, PPN 12%, dan totalAmount secara terpisah.
10. Konfirmasi checkout lalu order terbuat dengan status awal Sedang Dikemas. Cek stok produk berkurang dan saldo wallet berkurang.


### Skenario 4: Seller melakukan proses order

1. Login sebagai Seller.
2. Buka Order Masuk, lalu order dari buyer muncul dengan status Sedang Dikemas.
3. Klik Proses maka status berubah menjadi Menunggu Pengirim, job pengiriman tersedia untuk driver.


### Skenario 5: Driver bisa mencari, mengambil dan menyelesaikan Job

1. Login sebagai Driver.
2. Buka Job Tersedia, maka job dari order yang diproses seller muncul.
3. Klik Ambil Job maka status order berubah menjadi `SEDANG_DIKIRIM`, job akan dipindah ke Job Aktif.
4. Buka detail job, driver lain tidak bisa mengakses job ini (dicoba login menggunakan akun driver lain).
5. Klik Selesaikan (Konfirmasi Job Selesai) maka status order berubah menjadi `SELESAI`, wallet driver dan seller dikreditkan.
6. Buka Riwayat & Penghasilan maka job yang baru selesai dan earning-nya tercatat.


### Skenario 6: Buyer bisa melihat riwayat dan status order

1. Login sebagai Buyer yang sama dari Skenario 3.
2. Buka Riwayat Pesanan  maka order yang sudah dibuat tampil dengan status terkini (SELESAI, sesuai dengan Skenario 5).
3. Klik salah satu order, buka detailnya, maka menampilkan timeline status* (Sedang Dikemas → Menunggu Pengirim → Sedang Dikirim → Selesai) lengkap dengan timestamp tiap perubahan.


### Skenario 7: Admin dapat memonitoring, melakukan discount management, dan simulasi overdue

1. Login sebagai Admin.
2. Monitoring dilakukan dengan membuka Admin Dashboard, lalu cek masing-masing menu Pengguna Terbaru, Toko Terbaru, Produk Terbaru, Pesanan Terbaru, Diskon Terbaru (Voucher/Promo), dan Delivery Job Terbaru.
3. Admin bisa membuat dan melihat seluruh voucher baru (misal kode, persentase, tanggal expired) dan promo baru.
4. Admin bisa melakukan simulasi overdue:
    - Buat order baru sebagai Buyer, proses oleh Seller, tetapi jangan diambil driver.
    - Lihat tab Simulasi Waktu, advance/majukan waktu sesuai SLA metode yang dipilih:
        - INSTANT: minimal 121 menit
        - NEXT DAY: minimal 2.881 menit
        - REGULAR: minimal 10.081 menit
    - Trigger Proses Overdue (atau tunggu scheduler otomatis tiap 30 menit).
    - Cek order sebagai Buyer. Maka statusnya berubah menjadi `DIKEMBALIKAN`, saldo wallet buyer kembali ke nilai sebelum checkout, dan stok produk terkait auto-return.