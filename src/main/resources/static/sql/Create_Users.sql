-- Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    contact_number VARCHAR(10) UNIQUE,
    role VARCHAR(50),
    enabled BOOLEAN DEFAULT TRUE,
    
    -- Constraints
    CHECK (char_length(name) >= 2 AND char_length(name) <= 20),
    CHECK (char_length(password) >= 5),
    CHECK (contact_number ~ '^[0-9]{10}$')
);

{
  "name": "Sothea",
  "email": "sothea@example.com",
  "password": "secret123",
  "contactNumber": "0123456789",
  "role": "ROLE_CUSTOMER",
  "enabled": true
}