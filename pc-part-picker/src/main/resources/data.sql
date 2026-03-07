INSERT INTO cpus (model, brand, socket, cores, price) VALUES ('Ryzen 5 7600', 'AMD', 'AM5', 6, 279.99);
INSERT INTO cpus (model, brand, socket, cores, price) VALUES ('Ryzen 7 7700X', 'AMD', 'AM5', 8, 449.99);
INSERT INTO cpus (model, brand, socket, cores, price) VALUES ('Core i5-14600K', 'Intel', 'LGA1700', 14, 409.99);

INSERT INTO gpus (model, brand, vramGb, price) VALUES ('RTX 4060', 'NVIDIA', 8, 429.99);
INSERT INTO gpus (model, brand, vramGb, price) VALUES ('RTX 4070', 'NVIDIA', 12, 749.99);
INSERT INTO gpus (model, brand, vramGb, price) VALUES ('RX 7600', 'AMD', 8, 369.99);

INSERT INTO motherboards (model, brand, socket, formFactor, price) VALUES ('MSI B650M Pro', 'MSI', 'AM5', 'mATX', 189.99);
INSERT INTO motherboards (model, brand, socket, formFactor, price) VALUES ('ASUS ROG B650E-F', 'ASUS', 'AM5', 'ATX', 339.99);
INSERT INTO motherboards (model, brand, socket, formFactor, price) VALUES ('Gigabyte B760M DS3H', 'Gigabyte', 'LGA1700', 'mATX', 179.99);

INSERT INTO rams (model, brand, ddrType, speedMhz, capacityGb, price) VALUES ('Vengeance RGB 16GB', 'Corsair', 'DDR5', 6000, 16, 89.99);
INSERT INTO rams (model, brand, ddrType, speedMhz, capacityGb, price) VALUES ('Ripjaws V 32GB', 'G.Skill', 'DDR4', 3200, 32, 109.99);
INSERT INTO rams (model, brand, ddrType, speedMhz, capacityGb, price) VALUES ('Trident Z5 32GB', 'G.Skill', 'DDR5', 6400, 32, 159.99);

INSERT INTO psus (model, brand, wattage, efficiencyRating, modularType, price) VALUES ('RM750e', 'Corsair', 750, '80+ Gold', 'Fully Modular', 109.99);
INSERT INTO psus (model, brand, wattage, efficiencyRating, modularType, price) VALUES ('SuperNOVA 850 GT', 'EVGA', 850, '80+ Gold', 'Fully Modular', 139.99);
INSERT INTO psus (model, brand, wattage, efficiencyRating, modularType, price) VALUES ('Focus GX-650', 'Seasonic', 650, '80+ Gold', 'Fully Modular', 99.99);

INSERT INTO storages (model, brand, type, capacityGb, price) VALUES ('980 Pro', 'Samsung', 'NVMe SSD', 1000, 129.99);
INSERT INTO storages (model, brand, type, capacityGb, price) VALUES ('SN850X', 'Western Digital', 'NVMe SSD', 2000, 189.99);
INSERT INTO storages (model, brand, type, capacityGb, price) VALUES ('MX500', 'Crucial', 'SATA SSD', 1000, 79.99);

INSERT INTO cases (model, brand, formFactor, maxGpuLengthMm, price) VALUES ('H510 Flow', 'NZXT', 'ATX', 381, 89.99);
INSERT INTO cases (model, brand, formFactor, maxGpuLengthMm, price) VALUES ('Meshify 2 Compact', 'Fractal Design', 'ATX', 315, 119.99);
INSERT INTO cases (model, brand, formFactor, maxGpuLengthMm, price) VALUES ('NR200P', 'Cooler Master', 'Mini-ITX', 330, 99.99);

INSERT INTO coolers (model, brand, socket, maxTdp, type, price) VALUES ('NH-D15', 'Noctua', 'AM5', 200, 'Air', 109.99);
INSERT INTO coolers (model, brand, socket, maxTdp, type, price) VALUES ('Dark Rock Pro 4', 'be quiet!', 'LGA1700', 250, 'Air', 89.99);
INSERT INTO coolers (model, brand, socket, maxTdp, type, price) VALUES ('iCUE H150i Elite', 'Corsair', 'AM5', 250, 'AIO 360mm', 169.99);

INSERT INTO user_preferences (preferredCpuBrand, preferredGpuBrand, preferredMotherboardBrand, maxBudget)
VALUES ('AMD', 'NVIDIA', 'MSI', 1300.00);
