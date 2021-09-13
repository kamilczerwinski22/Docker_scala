# --- !Ups
CREATE TABLE "user" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "email" VARCHAR,
    "password" VARCHAR,
    "nickname" VARCHAR
);

CREATE TABLE "user_info" (
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"    VARCHAR   NOT NULL,
    "firstname"  VARCHAR   NOT NULL,
    "lastname"   VARCHAR   NOT NULL,
    "address"    VARCHAR   NOT NULL,
    "zipcode"    VARCHAR   NOT NULL,
    "city"       VARCHAR   NOT NULL,
    "country"    VARCHAR   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE "coupon" (
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "amount"     INTEGER   NOT NULL,
    "usages"     INTEGER   NOT NULL
);

CREATE TABLE "stock" (
    "id"          INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "unit_price"  INTEGER   NOT NULL,
    "total_price" INTEGER   NOT NULL,
    "total_stock" INTEGER   NOT NULL
);

CREATE TABLE "category" (
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"       VARCHAR   NOT NULL
);

CREATE TABLE "subcategory" (
    "id"          INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "category_id" INTEGER   NOT NULL,
    "name"        VARCHAR   NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category (id)
);


CREATE TABLE "product" (
    "id"             INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "stock_id"       INTEGER   NOT NULL,
    "category_id"    INTEGER   NOT NULL,
    "subcategory_id" INTEGER   NOT NULL,
    "description"    VARCHAR   NOT NULL,
    "name"           VARCHAR   NOT NULL,
    FOREIGN KEY (stock_id) REFERENCES stock (id),
    FOREIGN KEY (category_id) REFERENCES category (id),
    FOREIGN KEY (subcategory_id) REFERENCES subcategory (id)
);

CREATE TABLE "payment" (
    "id"             INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"        INTEGER   NOT NULL,
    "amount"         INTEGER   NOT NULL,
    "payment_money_id" INTEGER   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (payment_money_id) REFERENCES credit_card (id)
);

CREATE TABLE "order_" (
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"    INTEGER   NOT NULL,
    "payment_id" INTEGER   NOT NULL,
    "coupon_id" INTEGER   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (payment_id) REFERENCES payment (id),
    FOREIGN KEY (coupon_id) REFERENCES coupon (id)
);

CREATE TABLE "order_item" (
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product_id" INTEGER   NOT NULL,
    "order_id"   INTEGER   NOT NULL,
    "amount"     INTEGER   NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product (id),
    FOREIGN KEY (order_id) REFERENCES order_ (id)
);

CREATE TABLE "payment_money"
(
    "id"              INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"         INTEGER   NOT NULL,
    "cardholder_name" VARCHAR   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

# --- !Downs
DROP TABLE "order_item"
DROP TABLE "user"
DROP TABLE "stock"
DROP TABLE "category"
DROP TABLE "coupon"
DROP TABLE "subcategory"
DROP TABLE "payment"
DROP TABLE "product"
DROP TABLE "order"
DROP TABLE "user_info"
DROP TABLE "payment_money"

