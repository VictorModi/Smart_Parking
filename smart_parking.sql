CREATE TABLE user (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    permission_level INTEGER NOT NULL,
    password VARCHAR(128) NOT NULL,
    two_factor_auth_enabled BOOLEAN DEFAULT FALSE,
    two_factor_secret_key VARCHAR(128),
    two_factor_recovery_code VARCHAR(16),
    created_by INTEGER,
    last_login_time DATETIME DEFAULT NOW(),
    account_created_time DATETIME DEFAULT NOW(),
    is_disabled BOOLEAN DEFAULT FALSE,
    display_name VARCHAR(50),
    username VARCHAR(20) NOT NULL UNIQUE,
    contact_email VARCHAR(32),
    note VARCHAR(128)
);

INSERT INTO user (username, permission_level, password, created_by, display_name, contact_email, note)
VALUES
("superAdmin", 0, "c2a0e092c688b00d25c0f8906ff699b466bdae7142732e4855fcddf5a759cb5095e3e1af9c035c8bcf3529fc19645926c9cdaf204255bc80f057dbc0b5aecf6f", NOW(), "超管", "victormodi@outlook.com", ""),
("admin", 0, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "管理员", "victormodi@outlook.com", ""),
("systemAdmin", 1, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "系统管理员", "victormodi@outlook.com", ""),
("parkingAdmin", 2, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "车位管理员", "victormodi@outlook.com", ""),
("chargeAdmin", 3, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "收费管理员", "victormodi@outlook.com", ""),
("secrityAdmin", 4, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "安保管理员", "victormodi@outlook.com", ""),
("customerServiceAdmin", 5, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "客服管理员", "victormodi@outlook.com", ""),
("nothingAdmin", -1, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "被禁用", "victormodi@outlook.com", ""),
("parkingAdmin2", 2, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "车位管理员2", "victormodi@outlook.com", ""),
("parkingAdmin3", 2, "e36f27fe6966f8903ea5b44e19e7b7bf52f034ba8f8a391f64422fcb10a1b91bcee52bb9c69d72b35160f8e6d066834c87902170b441a7a6234b7e427ee35574", NOW(), "车位管理员3", "victormodi@outlook.com", "");

-- 超管密码 superAdmin 其他均是admin

CREATE TABLE cookie (
    id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL REFERENCES user(id),
    token VARCHAR(64) NOT NULL,
    expiration_date DATETIME NOT NULL,
    last_use DATETIME DEFAULT NOW(),
    create_at DATETIME DEFAULT NOW(),
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO `cookie` (`create_at`, `expiration_date`, `is_active`, `last_use`, `token`, `user_id`) VALUES
('2024-06-05 00:59:05', '2024-06-08 00:59:05', 0, '2024-06-05 00:59:05', '29c1cf3c0f082a4ca819df79dfd83786750e2d8a', 1),
('2024-06-05 00:59:18', '2024-06-08 00:59:19', 0, '2024-06-05 00:59:18', '3d5d8a61b48a30f6dc9541b04b6f4867f774d2a0', 1),
('2024-06-05 01:05:50', '2024-06-08 01:05:51', 0, '2024-06-05 01:05:50', '0100ef3d6d7836f6a20a9314ce35925668e3b816', 1),
('2024-06-05 01:07:25', '2024-06-08 01:07:26', 0, '2024-06-05 01:07:25', '2641f9fd53478494c6e291d3d621f1b6b940f642', 1),
('2024-06-05 01:11:48', '2024-06-08 01:11:49', 0, '2024-06-05 01:11:48', 'f15a6858e440c7eba8fc45d581b3df7e6f7b7808', 1),
('2024-06-05 01:13:04', '2024-06-08 01:13:04', 0, '2024-06-05 01:13:04', '4d116b870a651ee6751bf2a808d94e83faee1ea5', 1),
('2024-06-05 01:26:54', '2024-06-08 01:26:54', 0, '2024-06-05 01:27:02', '7868a07de3f0f0c01a4bd46be57cac20dd938708', 1),
('2024-06-05 09:14:11', '2024-06-08 09:14:12', 0, '2024-06-05 09:45:37', '25589cd0a941bf20d944f8155553b6c57b25d55e', 1),
('2024-06-05 09:45:43', '2024-06-08 09:45:44', 0, '2024-06-05 09:46:19', '6cd3e150b38cf57ccdbaa06f5586cdd9fe794633', 1),
('2024-06-05 09:46:24', '2024-06-08 09:46:25', 0, '2024-06-05 10:25:11', 'e3c7e11a6ded54faa7a5804e6cec4d27936c299c', 1);

CREATE TABLE car_information (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    license_plate VARCHAR(20) UNIQUE,
    owner_name VARCHAR(50),
    contact_number VARCHAR(20),
    brand VARCHAR(50),
    model VARCHAR(50),
    color VARCHAR(20)
);

INSERT INTO car_information (license_plate, owner_name, contact_number, brand, model, color)
VALUES
    ('京A12345', '王伟', '13800000001', 'Toyota', 'Camry', 'Black'),
    ('沪B67890', '李娜', '13800000002', 'Honda', 'Accord', 'White'),
    ('粤C23456', '张强', '13800000003', 'BMW', 'X5', 'Blue'),
    ('川D78901', '刘洋', '13800000004', 'Mercedes', 'C-Class', 'Silver'),
    ('津E34567', '赵敏', '13800000005', 'Audi', 'A4', 'Red'),
    ('浙F45678', '孙鹏', '13800000006', 'Volkswagen', 'Passat', 'Green'),
    ('苏G56789', '周晓', '13800000007', 'Ford', 'Focus', 'Yellow'),
    ('鄂H67890', '何莉', '13800000008', 'Nissan', 'Altima', 'Black'),
    ('湘J78901', '杨波', '13800000009', 'Chevrolet', 'Malibu', 'White'),
    ('皖K89012', '沈静', '13800000010', 'Mazda', 'CX-5', 'Blue');

CREATE TABLE parking_space (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(100),
    current_car_id INTEGER REFERENCES car_information(id)
);

INSERT INTO parking_space (location, current_car_id)
VALUES
    ('地下停车场A1', 1),
    ('地下停车场A2', 2),
    ('地面停车场B1', 3),
    ('地面停车场B2', 4),
    ('地下停车场C1', 5),
    ('地下停车场C2', 6),
    ('地面停车场D1', 7),
    ('地面停车场D2', 8),
    ('地下停车场E1', 9),
    ('地下停车场E2', 10);
