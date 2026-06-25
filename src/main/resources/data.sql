INSERT INTO users (id, username, email, password_hash, is_admin, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin',    'admin@example.com',    '$2a$10$wKQwqz9kHiw/C3r4nRe06OhTw4ps4GnBTBXzBlDhIb3iDv08evb0C', true,  now()),
    ('22222222-2222-2222-2222-222222222222', 'seller01', 'seller01@example.com', '$2a$10$iBWo82HVgtyhKeRmuricMenI8Ed/o321X/lN6JmWZkkAm62phmlv.', false, now()),
    ('33333333-3333-3333-3333-333333333333', 'buyer01',  'buyer01@example.com',  '$2a$10$YoGEyqp1ty5Nyx0WKOzDk.M3M4NI8sUbjLDcMJTVjhoeZwVEJJnN6', false, now()),
    ('44444444-4444-4444-4444-444444444444', 'driver01', 'driver01@example.com', '$2a$10$mpyaPWxcJzWNeJjS8/QEYO1KF003w52tOidgCax1xL1.C.F/BjWCO', false, now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (id, user_id, role)
VALUES
    ('a1111111-aaaa-1111-aaaa-111111111111', '22222222-2222-2222-2222-222222222222', 'SELLER'),
    ('a2222222-aaaa-2222-aaaa-222222222222', '33333333-3333-3333-3333-333333333333', 'BUYER'),
    ('a3333333-aaaa-3333-aaaa-333333333333', '44444444-4444-4444-4444-444444444444', 'DRIVER')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO wallets (id, user_id, balance, created_at, updated_at)
VALUES
    ('b1111111-bbbb-1111-bbbb-111111111111', '11111111-1111-1111-1111-111111111111', 0,       now(), now()),
    ('b2222222-bbbb-2222-bbbb-222222222222', '22222222-2222-2222-2222-222222222222', 0,       now(), now()),
    ('b3333333-bbbb-3333-bbbb-333333333333', '33333333-3333-3333-3333-333333333333', 1000000, now(), now()),
    ('b4444444-bbbb-4444-bbbb-444444444444', '44444444-4444-4444-4444-444444444444', 0,       now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO stores (id, name, description, owner_id, created_at, updated_at)
VALUES
    ('55555555-5555-5555-5555-555555555555', 'Toko Jaya Abadi',
     'Toko serba ada dengan produk berkualitas, melayani seluruh Indonesia.',
     '22222222-2222-2222-2222-222222222222', now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, name, description, price, stock, store_id, created_at, updated_at)
VALUES
    ('66666666-6666-6666-6666-666666666666', 'Kaos Polos Premium',
     'Kaos polos bahan cotton combed 30s, nyaman dipakai sehari-hari.',
     75000, 100, '55555555-5555-5555-5555-555555555555', now(), now()),
    ('77777777-7777-7777-7777-777777777777', 'Celana Jeans Slim Fit',
     'Celana jeans slim fit bahan denim premium, tersedia berbagai ukuran.',
     185000, 50, '55555555-5555-5555-5555-555555555555', now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO addresses (id, user_id, label, recipient_name, phone, full_address, city, postal_code, is_default, created_at, updated_at)
VALUES
    ('88888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333333',
     'Rumah', 'Budi Santoso', '081234567890',
     'Jl. Mawar No. 12 RT 03/RW 05', 'Jakarta Selatan', '12345', true, now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO vouchers (id, code, description, discount_type, discount_value, max_discount_amount, min_purchase_amount, usage_limit, used_count, expiry_date, active, created_at, updated_at)
VALUES
    ('99999999-9999-9999-9999-999999999999', 'DISKON10',
     'Diskon 10% untuk semua pembelian, maksimal potongan 50.000.',
     'PERCENTAGE', 10, 50000, 100000, 100, 0, '2026-12-31 23:59:59', true, now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO promos (id, code, description, discount_type, discount_value, max_discount_amount, min_purchase_amount, expiry_date, active, created_at, updated_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'PROMOAKHIRTAHUN',
     'Promo akhir tahun, potongan tetap Rp25.000.',
     'FIXED', 25000, 25000, 150000, '2026-12-31 23:59:59', true, now(), now())
    ON CONFLICT (id) DO NOTHING;

INSERT INTO app_reviews (id, reviewer_name, rating, comment, created_at)
VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Andi Wijaya', 5,
     'Aplikasi Seapedia sangat membantu dan mudah digunakan!', now())
    ON CONFLICT (id) DO NOTHING;