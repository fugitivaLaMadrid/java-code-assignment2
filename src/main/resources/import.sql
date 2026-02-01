-- ---------------------------
-- Stores
-- ---------------------------
INSERT INTO store(id, name, quantityProductsInStock) VALUES (1, 'HAARLEM', 10);
INSERT INTO store(id, name, quantityProductsInStock) VALUES (2, 'AMSTERDAM', 5);
INSERT INTO store(id, name, quantityProductsInStock) VALUES (3, 'HENGELO', 3);

-- Add dummy entries for tests
INSERT INTO store(id, name, quantityProductsInStock) VALUES (9999, 'DUMMY STORE', 0);

ALTER SEQUENCE store_seq RESTART WITH 10000;
-- ---------------------------
-- Products
-- ---------------------------
INSERT INTO product(id, name, stock) VALUES (1, 'TONSTAD', 10);
INSERT INTO product(id, name, stock) VALUES (2, 'KALLAX', 5);
INSERT INTO product(id, name, stock) VALUES (3, 'BESTÅ', 3);
INSERT INTO product(id, name, stock) VALUES (4, 'MALM', 7); -- added to fix runtime error
ALTER SEQUENCE product_seq RESTART WITH 5;

-- ---------------------------
-- Warehouses
-- ---------------------------
INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES (1, 'MWH.001', 'ZWOLLE-001', 100, 10, '2024-07-01', null);
INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES (2, 'MWH.012', 'AMSTERDAM-001', 50, 5, '2023-07-01', null);
INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES (3, 'MWH.023', 'TILBURG-001', 30, 27, '2021-02-01', null);
INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES (4, 'MWH.045', 'AMSTERDAM-002', 70, 15, '2025-01-01', null);
INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES (5, 'MWH.056', 'ZWOLLE-002', 60, 8, '2025-02-01', null);

ALTER SEQUENCE warehouse_seq RESTART WITH 6;
