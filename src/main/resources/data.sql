INSERT INTO shopping_list (title) VALUES ('Grocery');

INSERT INTO item (name, category, price) VALUES ('Milk', 'Dairy', 2.49);
INSERT INTO item (name, category, price) VALUES ('Bread', 'Bakery', 1.99);

INSERT INTO shopping_list_item (item_id, list_id, quantity) VALUES (1, 1, 1);
INSERT INTO shopping_list_item (item_id, list_id, quantity) VALUES (2, 1, 1);