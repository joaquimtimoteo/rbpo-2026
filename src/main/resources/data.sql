-- Backfill new Task 4 security columns when upgrading a Task 3 database.
UPDATE users SET role = 'STUDENT' WHERE role IS NULL;
UPDATE users SET enabled = TRUE WHERE enabled IS NULL;
UPDATE users SET account_non_locked = TRUE WHERE account_non_locked IS NULL;
ALTER TABLE users ALTER COLUMN role SET NOT NULL;
ALTER TABLE users ALTER COLUMN enabled SET NOT NULL;
ALTER TABLE users ALTER COLUMN account_non_locked SET NOT NULL;

-- Idempotent demo data for local development. Change credentials through environment variables.
INSERT INTO users (id, full_name, email, role, enabled, account_non_locked) VALUES
 (9001, 'Иван Петров', 'ivan.petrov@example.test', 'STUDENT', TRUE, TRUE),
 (9002, 'Анна Смирнова', 'anna.smirnova@example.test', 'STUDENT', TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;
INSERT INTO rooms (id, number, capacity, available) VALUES
 (9001, 'A-101', 2, TRUE),
 (9002, 'A-102', 3, TRUE),
 (9003, 'B-201', 1, TRUE)
ON CONFLICT (id) DO NOTHING;
INSERT INTO housing_applications (id, applicant_id, submitted_at, status) VALUES
 (9001, 9001, CURRENT_DATE, 'APPROVED'),
 (9002, 9002, CURRENT_DATE, 'NEW')
ON CONFLICT (id) DO NOTHING;
INSERT INTO residence_contracts (id, application_id, room_id, move_in_date, move_out_date, status) VALUES
 (9001, 9001, 9001, CURRENT_DATE, NULL, 'ACTIVE')
ON CONFLICT (id) DO NOTHING;
INSERT INTO room_transfer_requests (id, contract_id, target_room_id, submitted_at, status) VALUES
 (9001, 9001, 9002, CURRENT_DATE, 'NEW')
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('users','id'), GREATEST((SELECT COALESCE(MAX(id),1) FROM users),1));
SELECT setval(pg_get_serial_sequence('rooms','id'), GREATEST((SELECT COALESCE(MAX(id),1) FROM rooms),1));
SELECT setval(pg_get_serial_sequence('housing_applications','id'), GREATEST((SELECT COALESCE(MAX(id),1) FROM housing_applications),1));
SELECT setval(pg_get_serial_sequence('residence_contracts','id'), GREATEST((SELECT COALESCE(MAX(id),1) FROM residence_contracts),1));
SELECT setval(pg_get_serial_sequence('room_transfer_requests','id'), GREATEST((SELECT COALESCE(MAX(id),1) FROM room_transfer_requests),1));
