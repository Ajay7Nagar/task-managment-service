-- Create database schema and initial admin user
-- This script is executed when the PostgreSQL container starts

-- Set timezone
SET timezone = 'UTC';

-- Create extension for UUID generation if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Note: Tables will be created automatically by Hibernate DDL
-- This script can be used for initial data setup

-- Insert initial admin user (password: admin123)
-- This will be executed after tables are created by the application
-- INSERT INTO users (username, email, password, first_name, last_name, role, is_active, created_at, updated_at)
-- VALUES ('admin', 'admin@taskmanagement.com', '$2a$10$NlvSsNU/v4W8O.YlD0zKF.KE/lmyVCFLAHoL.mHVCONkjZf5zz.RO', 'System', 'Administrator', 'ADMIN', true, NOW(), NOW())
-- ON CONFLICT (username) DO NOTHING;