INSERT INTO users (id, username, email, password_hash, is_admin, full_name, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin',   'admin@example.com',       '$2b$10$gDdlF1zmMooNz4xd2L3vvubQ6IsLoHGa2BjCD7cy5gQq.eCT41cJK', true,  'Admin', now()),
    ('22222222-2222-2222-2222-222222222222', 'budi',    'budi@example.com',        '$2b$10$MmN3y3i7bCAV/MuxTe38IeI7CP3XZJC2jMmdTSf4dJzi1zmfmenEW', false, 'Budi Prasetyo', now()),
    ('33333333-3333-3333-3333-333333333333', 'sari',    'sari@example.com',        '$2b$10$.jxpqRTBdpk452334FIREent6NyqYf3Hh.q4B6X8Y.y4fgysjgApW', false, 'Sari Dewi', now()),
    ('44444444-4444-4444-4444-444444444444', 'dimas',   'dimas@example.com',       '$2b$10$33nQtPJNb.TgIuu1AffTf.VTZJzWbkYOKsRpmnuezkhFnVW4XhLN.', false, 'Dimas Aditya', now()),
    ('55555555-5555-5555-5555-555555555555', 'rina',    'rina@example.com',        '$2b$10$olT9B/glAOzWlr3ktkHmregNcQGo6bsRph5WzrIqlfl338QmQx86O', false, 'Rina Kusuma', now())
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (id, user_id, role)
VALUES
    ('a0000001-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '22222222-2222-2222-2222-222222222222', 'SELLER'),
    ('a0000001-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '22222222-2222-2222-2222-222222222222', 'BUYER'),
    ('a0000002-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '33333333-3333-3333-3333-333333333333', 'BUYER'),
    ('a0000002-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '33333333-3333-3333-3333-333333333333', 'DRIVER'),
    ('a0000003-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '44444444-4444-4444-4444-444444444444', 'SELLER'),
    ('a0000003-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '44444444-4444-4444-4444-444444444444', 'DRIVER'),
    ('a0000004-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '55555555-5555-5555-5555-555555555555', 'BUYER'),
    ('a0000004-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '55555555-5555-5555-5555-555555555555', 'SELLER'),
    ('a0000004-aaaa-aaaa-aaaa-aaaaaaaaaaa3', '55555555-5555-5555-5555-555555555555', 'DRIVER')
ON CONFLICT DO NOTHING;

INSERT INTO wallets (id, user_id, balance, created_at, updated_at)
VALUES
    ('b0000000-bbbb-bbbb-bbbb-bbbbbbbbb001', '11111111-1111-1111-1111-111111111111', 0,       now(), now()),
    ('b0000000-bbbb-bbbb-bbbb-bbbbbbbbb002', '22222222-2222-2222-2222-222222222222', 500000,  now(), now()),
    ('b0000000-bbbb-bbbb-bbbb-bbbbbbbbb003', '33333333-3333-3333-3333-333333333333', 750000,  now(), now()),
    ('b0000000-bbbb-bbbb-bbbb-bbbbbbbbb005', '55555555-5555-5555-5555-555555555555', 1000000, now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO stores (id, name, description, owner_id, created_at, updated_at)
VALUES
    ('c0000000-cccc-cccc-cccc-cccccccccc01', 'Toko Segar Laut',
     'Menyediakan aneka seafood segar langsung dari nelayan. Pengiriman cepat & terpercaya.',
     '22222222-2222-2222-2222-222222222222', now(), now()),
    ('c0000000-cccc-cccc-cccc-cccccccccc02', 'Toko Elektronik',
     'Gadget, aksesoris, dan perlengkapan digital dengan harga terjangkau.',
     '44444444-4444-4444-4444-444444444444', now(), now()),
    ('c0000000-cccc-cccc-cccc-cccccccccc03', 'Toko Fashion',
     'Aksesoris dan pakaian stylish untuk pria & wanita, kekinian dan nyaman.',
     '55555555-5555-5555-5555-555555555555', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO products (id, name, description, price, stock, sold_count, image_url, store_id, created_at, updated_at)
VALUES
    ('d0000000-dddd-dddd-dddd-dddddddddd01', 'Ikan Salmon Segar 500g',
     'Ikan salmon segar impor, kaya omega-3. Porsi 500 gram, siap masak.', 95000, 30, 15,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/ikan_salmon_1.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc01', now(), now()),
    ('d0000000-dddd-dddd-dddd-dddddddddd02', 'Udang Vannamei 1kg',
     'Udang vannamei ukuran besar, bersih, tanpa kulit. Cocok untuk bakar & goreng.', 75000, 50, 27,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/udang_vaname_1.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc01', now(), now()),
    ('d0000000-dddd-dddd-dddd-dddddddddd03', 'Cumi-Cumi Segar 500g',
     'Cumi-cumi segar, ukuran sedang, sudah dibersihkan. Nyaman diolah jadi berbagai masakan.', 55000, 40, 12,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/cumi_cumi_segar_1.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc01', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO products (id, name, description, price, stock, sold_count, image_url, store_id, created_at, updated_at)
VALUES
    ('d0000000-dddd-dddd-dddd-dddddddddd04', 'Power Bank 10000mAh',
     'Power bank kapasitas 10000mAh, dual port, fast charging. Bawa gadget tetap nyala.', 120000, 25, 8,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/power_bank_1.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc02', now(), now()),
    ('d0000000-dddd-dddd-dddd-dddddddddd05', 'Earphone Bluetooth',
     'Earphone wireless Bluetooth 5.3, bass jernih, tahan 8 jam pemakaian.', 85000, 40, 42,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/earphone_bluetooth_1.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc02', now(), now()),
    ('d0000000-dddd-dddd-dddd-dddddddddd06', 'Kabel USB-C 2m',
     'Kabel USB-C ke USB-A panjang 2 meter, fast charging & data sync, woven braided.', 25000, 100, 75,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/kabel_usb_c.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc02', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO products (id, name, description, price, stock, sold_count, image_url, store_id, created_at, updated_at)
VALUES
    ('d0000000-dddd-dddd-dddd-dddddddddd07', 'Topi Snapback',
     'Topi snapback casual, adjustable, bahan katun premium. Cocok sehari-hari.', 45000, 60, 33,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/topi_snapback.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc03', now(), now()),
    ('d0000000-dddd-dddd-dddd-dddddddddd08', 'Tote Bag Kanvas Besar',
     'Tote bag kanvas tebal, kapasitas besar, bisa dilipat. Cocok belanja & kuliah.', 35000, 80, 91,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/totebag_canvas_1.jpg',
     'c0000000-cccc-cccc-cccc-cccccccccc03', now(), now()),
    ('d0000000-dddd-dddd-dddd-dddddddddd09', 'Scarf Polos',
     'Scarf polos bahan ceruti lembut, tidak gerah. Tersedia 5 warna.', 28000, 90, 64,
     'https://res.cloudinary.com/dhpxtqzt5/image/upload/scarf_polos.png',
     'c0000000-cccc-cccc-cccc-cccccccccc03', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO addresses (id, user_id, label, recipient_name, phone, full_address, city, postal_code, is_default, created_at, updated_at)
VALUES
    ('e0000000-eeee-eeee-eeee-eeeeeeeeee01', '22222222-2222-2222-2222-222222222222',
     'Rumah', 'Budi Prasetyo', '081234567890',
     'Jl. Kenanga No. 15, RT 05/RW 02', 'Jakarta Timur', '13560', true, now(), now()),
    ('e0000000-eeee-eeee-eeee-eeeeeeeeee02', '33333333-3333-3333-3333-333333333333',
     'Rumah', 'Sari Dewi', '087812345678',
     'Jl. Melati No. 8, RT 02/RW 04', 'Bandung', '40123', true, now(), now()),
    ('e0000000-eeee-eeee-eeee-eeeeeeeeee03', '33333333-3333-3333-3333-333333333333',
     'Kantor', 'Sari Dewi', '087812345678',
     'Jl. Asia Afrika No. 45, Lt. 3', 'Bandung', '40111', false, now(), now()),
    ('e0000000-eeee-eeee-eeee-eeeeeeeeee05', '55555555-5555-5555-5555-555555555555',
     'Rumah', 'Rina Kusuma', '085611223344',
     'Jl. Anggrek No. 22, RT 01/RW 03', 'Surabaya', '60241', true, now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO vouchers (id, code, description, discount_type, discount_value, max_discount_amount, min_purchase_amount, usage_limit, used_count, expiry_date, active, created_at, updated_at)
VALUES
    ('f0000000-ffff-ffff-ffff-fffffffffff1', 'DISKON10',
     'Diskon 10% untuk semua pembelian, maksimal potongan Rp50.000.',
     'PERCENTAGE', 10, 50000, 100000, 100, 0, '2026-12-31 23:59:59', true, now(), now()),
    ('f0000000-ffff-ffff-ffff-fffffffffff2', 'HEMAT25',
     'Potongan tetap Rp25.000 untuk belanja minimal Rp150.000.',
     'FIXED', 25000, 25000, 150000, 50, 0, '2026-12-31 23:59:59', true, now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO promos (id, code, description, discount_type, discount_value, max_discount_amount, min_purchase_amount, expiry_date, active, created_at, updated_at)
VALUES
    ('f0000000-ffff-ffff-ffff-fffffffffff3', 'PROMOAKHIRTAHUN',
     'Promo akhir tahun, potongan tetap Rp25.000 untuk belanja minimal Rp150.000.',
     'FIXED', 25000, 25000, 150000, '2026-12-31 23:59:59', true, now(), now()),
    ('f0000000-ffff-ffff-ffff-fffffffffff4', 'GRATIS',
     'Promo diskon 10% gratis tanpa minimal pembelian, maks potongan Rp20.000.',
     'PERCENTAGE', 10, 20000, 0, '2026-12-31 23:59:59', true, now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO app_reviews (id, reviewer_name, rating, comment, created_at)
VALUES
    ('ffffffff-ffff-ffff-ffff-fffffffffff1', 'Budi Prasetyo', 5,
     'Seapedia keren banget! Pengiriman cepat dan produk selalu segar.', now()),
    ('ffffffff-ffff-ffff-ffff-fffffffffff2', 'Sari Dewi', 4,
     'Aplikasinya bagus, fitur multi-role memudahkan saya jadi pembeli & driver.', now()),
    ('ffffffff-ffff-ffff-ffff-fffffffffff3', 'Rina Kusuma', 5,
     'Bisa jualan sambil jadi driver, lumayan tambah penghasilan!', now())
ON CONFLICT DO NOTHING;
