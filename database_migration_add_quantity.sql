-- Migration script to add quantity fields for package quantity feature
-- Run this script to update your database schema

-- Add package_quantity column to listings table
ALTER TABLE listings ADD COLUMN package_quantity INT DEFAULT 1;

-- Add quantity column to listingpackage table  
ALTER TABLE listingpackage ADD COLUMN quantity INT DEFAULT 1;

-- Update existing records to have quantity = 1 (default value)
UPDATE listings SET package_quantity = 1 WHERE package_quantity IS NULL;
UPDATE listingpackage SET quantity = 1 WHERE quantity IS NULL;

-- Add comments for documentation
COMMENT ON COLUMN listings.package_quantity IS 'Quantity of packages purchased for this listing';
COMMENT ON COLUMN listingpackage.quantity IS 'Quantity of packages purchased';
