Create database if not exists DB_UrbanStyle;
use DB_UrbanStyle;

-- Tablas:
-- Roles 
Create table roles (
id int not null auto_increment primary key,
name varchar(30) not null unique
);

-- categorias
Create table categories (
id int not null auto_increment primary key,
name varchar(50) not null unique,
description varchar(200) null
);

-- colores 
Create table colors (
id int not null auto_increment primary key,
name varchar(30) not null unique
);

-- Tallas
Create table sizes (
id int not null auto_increment primary key,
name varchar(10) not null unique
);

-- usuarios del sistema
Create table users (
id int not null auto_increment primary key,
role_id int not null,
first_name varchar(60) not null,
last_name varchar(60) not null,
dni char(8) not null unique, 
phone varchar(15) null,
username varchar(50) not null unique,
password varchar(100) not null,
active boolean not null default true, 
-- created_at significa la fecha en la que se creo "cuando se creo"
created_at datetime not null default current_timestamp, 
foreign key(role_id) references roles(id)
);

-- clientes aunque queria ponerle custombers para el ingles pero se queda en clientes 
Create table clientes(
id int not null auto_increment primary key,
first_name varchar(60) not null,
last_name varchar(60) not null,
dni char(8) null unique,
phone varchar(15) null,
email varchar(50) not null unique,
username varchar(50) not null unique,
password varchar(100) not null,
-- is_wholesale significa " Es cliente mayorista" y esta por defecto en No
is_wholesaler boolean not null default false,
active boolean not null default true, 
created_at datetime not null default current_timestamp
);
-- Brands (marcas en ingles )
Create table brands (
id int not null auto_increment primary key,
name varchar(50) not null unique
);
-- Productos
Create table products (
id int not null auto_increment primary key,
category_id int not null,
brand_id int null,
name varchar (100) not null,
description varchar(255) null,
price decimal(10,2) not null,
active boolean not null default true,
created_at datetime not null default current_timestamp,
foreign key(category_id) references categories(id),
foreign key(brand_id) references brands(id)
);

-- Variantes de productos ( color, talla)
Create table product_variants (
id int not null auto_increment primary key,
product_id int not null,
color_id int not null,
size_id int not null,
stock int not null default 0,
image_url varchar(255) null,
active boolean not null default true,
created_at datetime not null default current_timestamp,
foreign key(product_id) references products(id),
foreign key(color_id) references colors(id),
foreign key(size_id) references sizes(id),
unique(product_id, color_id, size_id)
);

-- Ventas, boletas y detalles de ventas
-- ventas
Create table sales (
id int not null auto_increment primary key,
user_id int not null, 
cliente_id int null,
sale_date datetime not null default current_timestamp,
subtotal decimal(10,2) not null,
igv decimal(10,2) not null,
total decimal(10,2) not null,
status varchar(20) not null default 'completed',
foreign key (user_id) references users(id),
foreign key (cliente_id) references clientes(id)
);

-- Detalles de ventas
Create table sale_details(
id int not null auto_increment primary key,
sale_id int not null,
product_variant_id int not null,
quantity int not null, -- cantidad
unit_price decimal(10,2) not null,
line_total decimal(10,2) not null,
foreign key (sale_id) references sales(id),
foreign key (product_variant_id) references product_variants(id)
);

-- Documentos ( boleta o factura)
Create table payment_documents(
id int not null auto_increment primary key,
sale_id int not null,
document_type varchar(10) not null,
series varchar(10) not null,
number int not null,
customer_dni char(8) null,
customer_ruc char(11) null,
-- cuando se emitio
issued_at datetime not null default current_timestamp,
foreign key (sale_id) references sales(id),
unique(document_type, series, number)
);

-- Insertar Datos : 
-- 1. roles
INSERT INTO roles (name) VALUES
('ADMIN'), ('SUB_ADMIN'), ('SELLER');

-- 2. categories
INSERT INTO categories (name, description) VALUES
('T-Shirts', 'Casual short-sleeve shirts'),
('Sneakers', 'Casual and sport shoes'),
('Polos', 'Collared shirts'),
('Shorts', 'Short pants'),
('Pants', 'Long pants and jeans'),
('Jackets', 'Outerwear'),
('Sweatshirts', 'Hoodies and crewnecks');

-- 3. colors
INSERT INTO colors (name) VALUES
('Black'), ('White'), ('Gray'), ('Beige'),
('Blue'), ('Red'), ('Green');

-- 4. sizes
INSERT INTO sizes (name) VALUES
('S'), ('M'), ('L'), ('XL'),
('38'), ('39'), ('40'), ('41'), ('42'), ('43');

-- 5 Usuario administrador de prueba (username: admi / password: admi123)
INSERT INTO users (role_id, first_name, last_name, dni, phone, username, password) VALUES
(1, 'Admin', 'Principal', '99999999', NULL, 'admi', '$2a$10$i09ELfmMBDyz1r88F/eKLe36u9E8hulq0g05j7GKtUycK9byIdWqK');


-- 6. clientes
INSERT INTO clientes (first_name, last_name, dni, phone, email, username, password, is_wholesaler) VALUES
('Carlos', 'Ramirez', '70456789', '987654324', 'carlos@example.com', 'cramirez', '$2a$10$examplehash4examplehash4examplehash4examplehash4exa', FALSE),
('Lucía',  'Torres',  NULL,       '987654325', 'lucia@example.com',  'ltorres',  '$2a$10$examplehash5examplehash5examplehash5examplehash5exa', TRUE);

-- 7. brands
INSERT INTO brands (name) VALUES
('Nike'), ('Adidas'), ('Puma'), ('Urban Style');

-- 8. products
-- ------------------------------------------------------------
INSERT INTO products (category_id, brand_id, name, description, price) VALUES
(1, 4, 'Polera Oversize',        'Corte holgado, 100% algodón',           45.00),
(2, 1, 'Air Force 1',            'Zapatillas clásicas blancas',           380.00),
(3, 4, 'Polo Cuello Redondo',    'Corte slim fit',                        55.00),
(4, 4, 'Short Cargo',            'Con bolsillos laterales',               60.00),
(6, 2, 'Casaca Bomber',          'Resistente al viento y al agua',        150.00);

-- ------------------------------------------------------------
-- 9. product_variants (sin imagen todavía, image_url queda NULL)
-- ------------------------------------------------------------
-- Polera Oversize (product_id = 1)
INSERT INTO product_variants (product_id, color_id, size_id, stock) VALUES
(1, 1, 1, 20),
(1, 1, 2, 35),
(1, 1, 3, 15),
(1, 2, 2, 10),

-- Air Force 1 (product_id = 2), tallas 40-42
(2, 2, 7, 5),
(2, 2, 8, 8),
(2, 2, 9, 0),

-- Polo Cuello Redondo (product_id = 3)
(3, 1, 2, 12),
(3, 5, 2, 7);





