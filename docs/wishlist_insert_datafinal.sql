USE wishlistdb;

INSERT INTO users (username, password) VALUES 
('Abdul', 'abdul123'),
('Enes', 'enes123');

INSERT INTO wishlists (user_id, name, description) VALUES
(1, 'Camping Gear', 'Essential equipment for camping trips'),
(2, 'Tech Wishlist', 'My dream gadgets and accessories'),
(1, 'Reading List', 'Books I want to read this year');

INSERT INTO wishlists (user_id, name, description) VALUES
(1,'Test wishlist','This is for testing purposes')

INSERT INTO wishlist_items (wishlist_id, name, description) VALUES
(1, 'Flashlight', 'A durable LED flashlight for outdoor use'),
(1, 'Tent', 'A waterproof camping tent for two people'),
(2, 'Mechanical Keyboard', 'A high-quality mechanical keyboard for gaming'),
(2, 'Gaming Mouse', 'An ergonomic gaming mouse with RGB lighting'),
(3, 'Dune by Frank Herbert', 'A sci-fi classic I’ve been meaning to read'),
(3, 'The Hobbit', 'A timeless fantasy novel by J.R.R. Tolkien');

INSERT INTO wishlist_items (wishlist_id, name, description) VALUES
(1,'Test wish','This is testing wishes')