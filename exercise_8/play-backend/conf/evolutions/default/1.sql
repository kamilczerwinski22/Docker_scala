# --- !Ups

CREATE TABLE "authToken"
(
    "id"     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INTEGER NOT NULL,
    FOREIGN KEY (userId) references user (id)
);

CREATE TABLE "passwordInfo"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "hasher"      VARCHAR NOT NULL,
    "password"    VARCHAR NOT NULL,
    "salt"        VARCHAR
);

CREATE TABLE "oAuth2Info"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "accessToken" VARCHAR NOT NULL,
    "tokenType"   VARCHAR,
    "expiresIn"   INTEGER
);

CREATE TABLE "user"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "providerId"  VARCHAR NOT NULL,
    "providerKey" VARCHAR NOT NULL,
    "email"       VARCHAR NOT NULL
);

CREATE TABLE "user_address"
(
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"    VARCHAR   NOT NULL,
    "firstname"  VARCHAR   NOT NULL,
    "lastname"   VARCHAR   NOT NULL,
    "address"    VARCHAR   NOT NULL,
    "zipcode"    VARCHAR   NOT NULL,
    "city"       VARCHAR   NOT NULL,
    "country"    VARCHAR   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE "credit_card"
(
    "id"              INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"         INTEGER   NOT NULL,
    "cardholder_name" VARCHAR   NOT NULL,
    "number"          VARCHAR   NOT NULL,
    "exp_date"        VARCHAR   NOT NULL,
    "cvc_code"        VARCHAR   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE "payment"
(
    "id"             INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"        INTEGER   NOT NULL,
    "credit_card_id" INTEGER   NOT NULL,
    "amount"         INTEGER   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (credit_card_id) REFERENCES credit_card (id)
);

CREATE TABLE "voucher"
(
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "code"       VARCHAR   NOT NULL,
    "amount"     INTEGER   NOT NULL,
    "usages"     INTEGER   NOT NULL
);

CREATE TABLE "order_"
(
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id"    INTEGER   NOT NULL,
    "address_id" INTEGER   NOT NULL,
    "payment_id" INTEGER   NOT NULL,
    "voucher_id" INTEGER   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (address_id) REFERENCES user_address (id),
    FOREIGN KEY (payment_id) REFERENCES payment (id),
    FOREIGN KEY (voucher_id) REFERENCES voucher (id)
);

CREATE TABLE "category"
(
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"       VARCHAR   NOT NULL
);

CREATE TABLE "subcategory"
(
    "id"          INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "category_id" INTEger   NOT NULL,
    "name"        VARCHAR   NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE "stock"
(
    "id"          INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "unit_price"  INTEGER   NOT NULL,
    "total_price" INTEGER   NOT NULL,
    "total_stock" INTEGER   NOT NULL
);

CREATE TABLE "product"
(
    "id"             INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "stock_id"       INT       NOT NULL,
    "category_id"    INT       NOT NULL,
    "subcategory_id" INT       NOT NULL,
    "name"           VARCHAR   NOT NULL,
    "image_url"      VARCHAR   NOT NULL,
    "description"    VARCHAR   NOT NULL,
    FOREIGN KEY (stock_id) REFERENCES stock (id),
    FOREIGN KEY (category_id) REFERENCES category (id),
    FOREIGN KEY (subcategory_id) REFERENCES subcategory (id)
);

CREATE TABLE "order_product"
(
    "id"         INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "order_id"   INT       NOT NULL,
    "product_id" INT       NOT NULL,
    "amount"     INT       NOT NULL,
    FOREIGN KEY (order_id) REFERENCES order_ (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);

# --- !Downs

DROP TABLE "authToken";
DROP TABLE "passwordInfo";
DROP TABLE "oAuth2Info";
DROP TABLE "order_product";
DROP TABLE "order_product";
DROP TABLE "product";
DROP TABLE "stock";
DROP TABLE "subcategory";
DROP TABLE "category";
DROP TABLE "order_";
DROP TABLE "voucher";
DROP TABLE "payment";
DROP TABLE "credit_card";
DROP TABLE "user_address";
DROP TABLE "user";
