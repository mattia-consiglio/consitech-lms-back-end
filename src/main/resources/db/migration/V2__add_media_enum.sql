-- V1.1.5__ADD_MEDIA_ENUM
ALTER TABLE IF EXISTS public.media DROP CONSTRAINT IF EXISTS media_type_check;
ALTER TABLE IF EXISTS public.media
    ADD CONSTRAINT media_type_check CHECK (type >= 0 AND type <= 2)
    NOT VALID;