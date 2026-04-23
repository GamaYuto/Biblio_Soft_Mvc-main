CREATE TABLE IF NOT EXISTS admin_users (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuario inicial de ejemplo:
-- username: admin
-- password: Admin12345!
INSERT INTO admin_users (username, password_hash, full_name)
VALUES ('admin', '$2a$12$Nbvkhxua7VpVY7owxdBeY.D9X43Gypci4gbajQo11YefVMKccN3Je', 'Administrador General');
