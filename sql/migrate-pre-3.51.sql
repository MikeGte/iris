ALTER TABLE camera DROP COLUMN dummy_134;
ALTER TABLE camera ADD encoder TEXT;
UPDATE camera SET encoder = '';
ALTER TABLE camera ALTER encoder SET NOT NULL;
ALTER TABLE camera ADD encoder_channel INTEGER;
UPDATE camera SET encoder_channel = 0;
ALTER TABLE camera ALTER encoder_channel SET NOT NULL;
ALTER TABLE camera ADD nvr TEXT;
UPDATE camera SET nvr = '';
ALTER TABLE camera ALTER nvr SET NOT NULL;
