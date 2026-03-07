INSERT INTO cpus (model, brand, socket, cores, price) VALUES ('Ryzen 5 7600', 'AMD', 'AM5', 6, 279.99);
INSERT INTO cpus (model, brand, socket, cores, price) VALUES ('Ryzen 7 7700X', 'AMD', 'AM5', 8, 449.99);
INSERT INTO cpus (model, brand, socket, cores, price) VALUES ('Core i5-14600K', 'Intel', 'LGA1700', 14, 409.99);

INSERT INTO gpus (model, brand, vramGb, price) VALUES ('RTX 4060', 'NVIDIA', 8, 429.99);
INSERT INTO gpus (model, brand, vramGb, price) VALUES ('RTX 4070', 'NVIDIA', 12, 749.99);
INSERT INTO gpus (model, brand, vramGb, price) VALUES ('RX 7600', 'AMD', 8, 369.99);

INSERT INTO motherboards (model, brand, socket, formFactor, price) VALUES ('MSI B650M Pro', 'MSI', 'AM5', 'mATX', 189.99);
INSERT INTO motherboards (model, brand, socket, formFactor, price) VALUES ('ASUS ROG B650E-F', 'ASUS', 'AM5', 'ATX', 339.99);
INSERT INTO motherboards (model, brand, socket, formFactor, price) VALUES ('Gigabyte B760M DS3H', 'Gigabyte', 'LGA1700', 'mATX', 179.99);

INSERT INTO user_preferences (preferredCpuBrand, preferredGpuBrand, preferredMotherboardBrand, maxBudget)
VALUES ('AMD', 'NVIDIA', 'MSI', 1300.00);
