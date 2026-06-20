INSERT INTO products (id, name, description, price, stock, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Baju Batik Premium', 'Batik tulis asli Yogyakarta', 250000, 50, NOW(), NOW()),
    (gen_random_uuid(), 'Sepatu Kulit Handmade', 'Sepatu kulit sapi asli', 450000, 30, NOW(), NOW()),
    (gen_random_uuid(), 'Tas Rotan Artisan', 'Kerajinan tangan Bali', 175000, 100, NOW(), NOW())
    ON CONFLICT DO NOTHING;