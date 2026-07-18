INSERT INTO dbo.branches
(
    branch_code,
    branch_name,
    address,
    city,
    country,
    latitude,
    longitude,
    phone_number,
    active,
    updated_at
)
VALUES

-- =========================================================
-- Existing branches
-- =========================================================

(
    'KL001',
    'Kuala Lumpur Headquarters',
    'Menara Maybank, Jalan Tun Perak',
    'Kuala Lumpur',
    'Malaysia',
    3.1478000,
    101.6953000,
    '03-12345678',
    1,
    SYSUTCDATETIME()
),

(
    'PJ001',
    'Petaling Jaya Branch',
    'Section 14',
    'Petaling Jaya',
    'Malaysia',
    3.1073000,
    101.6427000,
    '03-76543210',
    1,
    SYSUTCDATETIME()
),

(
    'SJ001',
    'Subang Jaya Branch',
    'SS15',
    'Subang Jaya',
    'Malaysia',
    3.0738000,
    101.5852000,
    '03-77665544',
    1,
    SYSUTCDATETIME()
),

-- =========================================================
-- Kuala Lumpur and Selangor
-- =========================================================

(
    'KL002',
    'Petronas Twin Towers Branch',
    'Kuala Lumpur City Centre',
    'Kuala Lumpur',
    'Malaysia',
    3.1579000,
    101.7117000,
    '03-10000001',
    1,
    SYSUTCDATETIME()
),

(
    'SL001',
    'Batu Caves Branch',
    'Batu Caves, Gombak',
    'Batu Caves',
    'Malaysia',
    3.2379000,
    101.6840000,
    '03-10000002',
    1,
    SYSUTCDATETIME()
),

(
    'SL002',
    'Kuala Selangor Fireflies Branch',
    'Kampung Kuantan',
    'Kuala Selangor',
    'Malaysia',
    3.3627000,
    101.3025000,
    '03-10000003',
    1,
    SYSUTCDATETIME()
),

-- =========================================================
-- Northern Malaysia
-- =========================================================

(
    'PG001',
    'George Town Heritage Branch',
    'Armenian Street',
    'George Town',
    'Malaysia',
    5.4145000,
    100.3292000,
    '04-10000004',
    1,
    SYSUTCDATETIME()
),

(
    'PG002',
    'Penang Hill Branch',
    'Jalan Stesen Bukit Bendera',
    'George Town',
    'Malaysia',
    5.4085000,
    100.2774000,
    '04-10000005',
    1,
    SYSUTCDATETIME()
),

(
    'KD001',
    'Langkawi Sky Bridge Branch',
    'Gunung Mat Cincang',
    'Langkawi',
    'Malaysia',
    6.3864000,
    99.6621000,
    '04-10000006',
    1,
    SYSUTCDATETIME()
),

(
    'PR001',
    'Kuala Kangsar Royal Town Branch',
    'Bukit Chandan',
    'Kuala Kangsar',
    'Malaysia',
    4.7723000,
    100.9403000,
    '05-10000007',
    1,
    SYSUTCDATETIME()
),

(
    'PR002',
    'Kellies Castle Branch',
    'Batu Gajah',
    'Batu Gajah',
    'Malaysia',
    4.4744000,
    101.0877000,
    '05-10000008',
    1,
    SYSUTCDATETIME()
),

-- =========================================================
-- Central Highlands and East Coast
-- =========================================================

(
    'PH001',
    'Cameron Highlands Branch',
    'Tanah Rata',
    'Cameron Highlands',
    'Malaysia',
    4.4705000,
    101.3767000,
    '05-10000009',
    1,
    SYSUTCDATETIME()
),

(
    'PH002',
    'Genting Highlands Branch',
    'Genting Highlands Resort',
    'Genting Highlands',
    'Malaysia',
    3.4236000,
    101.7932000,
    '03-10000010',
    1,
    SYSUTCDATETIME()
),

(
    'PH003',
    'Taman Negara Branch',
    'Kuala Tahan',
    'Jerantut',
    'Malaysia',
    4.3826000,
    102.4016000,
    '09-10000011',
    1,
    SYSUTCDATETIME()
),

(
    'PH004',
    'Tioman Island Branch',
    'Tekek Village, Tioman Island',
    'Rompin',
    'Malaysia',
    2.8167000,
    104.1619000,
    '09-10000012',
    1,
    SYSUTCDATETIME()
),

(
    'TR001',
    'Redang Island Branch',
    'Pulau Redang',
    'Kuala Nerus',
    'Malaysia',
    5.7844000,
    103.0069000,
    '09-10000013',
    1,
    SYSUTCDATETIME()
),

(
    'TR002',
    'Perhentian Islands Branch',
    'Pulau Perhentian',
    'Besut',
    'Malaysia',
    5.9037000,
    102.7537000,
    '09-10000014',
    1,
    SYSUTCDATETIME()
),

-- =========================================================
-- Southern Malaysia
-- =========================================================

(
    'MK001',
    'Melaka Heritage City Branch',
    'Dutch Square, Bandar Hilir',
    'Melaka',
    'Malaysia',
    2.1949000,
    102.2480000,
    '06-10000015',
    1,
    SYSUTCDATETIME()
),

(
    'NS001',
    'Port Dickson Beach Branch',
    'Pantai Teluk Kemang',
    'Port Dickson',
    'Malaysia',
    2.4505000,
    101.8550000,
    '06-10000016',
    1,
    SYSUTCDATETIME()
),

(
    'JH001',
    'Desaru Coast Branch',
    'Desaru Coast',
    'Bandar Penawar',
    'Malaysia',
    1.5403000,
    104.2644000,
    '07-10000017',
    1,
    SYSUTCDATETIME()
),

-- =========================================================
-- Sabah
-- =========================================================

(
    'SB001',
    'Kinabalu Park Branch',
    'Kinabalu Park, Kundasang',
    'Ranau',
    'Malaysia',
    6.0753000,
    116.5588000,
    '088-1000018',
    1,
    SYSUTCDATETIME()
),

(
    'SB002',
    'Tanjung Aru Beach Branch',
    'Tanjung Aru',
    'Kota Kinabalu',
    'Malaysia',
    5.9484000,
    116.0465000,
    '088-1000019',
    1,
    SYSUTCDATETIME()
),

(
    'SB003',
    'Semporna Islands Branch',
    'Semporna Waterfront',
    'Semporna',
    'Malaysia',
    4.4811000,
    118.6115000,
    '089-1000020',
    1,
    SYSUTCDATETIME()
),

(
    'SB004',
    'Sepilok Orangutan Centre Branch',
    'Sepilok',
    'Sandakan',
    'Malaysia',
    5.8647000,
    117.9519000,
    '089-1000021',
    1,
    SYSUTCDATETIME()
),

-- =========================================================
-- Sarawak
-- =========================================================

(
    'SW001',
    'Kuching Waterfront Branch',
    'Kuching Waterfront, Main Bazaar',
    'Kuching',
    'Malaysia',
    1.5593000,
    110.3444000,
    '082-1000022',
    1,
    SYSUTCDATETIME()
),

(
    'SW002',
    'Bako National Park Branch',
    'Bako National Park',
    'Kuching',
    'Malaysia',
    1.7167000,
    110.4667000,
    '082-1000023',
    1,
    SYSUTCDATETIME()
),

(
    'SW003',
    'Gunung Mulu National Park Branch',
    'Gunung Mulu National Park',
    'Mulu',
    'Malaysia',
    4.0481000,
    114.8125000,
    '085-1000024',
    1,
    SYSUTCDATETIME()
);

GO