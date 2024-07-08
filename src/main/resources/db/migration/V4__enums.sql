--V4__lesson_media_video

--MEDIA
CREATE TYPE public.media_type
  AS ENUM ('IMAGE', 'VIDEO', 'AUDIO');

ALTER TABLE IF EXISTS public.media
  RENAME type TO type_old;

ALTER TABLE IF EXISTS public.media
  ADD COLUMN type media_type;

--update media table from old type values
UPDATE public.media
SET type = 'IMAGE'
WHERE type_old = 0;

UPDATE public.media
SET type = 'VIDEO'
WHERE type_old = 1;

UPDATE public.media
SET type = 'AUDIO'
WHERE type_old = 2;

ALTER TABLE IF EXISTS public.media
  DROP COLUMN IF EXISTS type_old;

---------------------------------------- QUITZ ----------------------------------------
CREATE TYPE public.quiz_type
  AS ENUM ('MULTIPLE_CHOICE');

ALTER TABLE IF EXISTS public.quizzes
  ALTER COLUMN type TYPE quiz_type using type::quiz_type;

--------------------------------------- USERS ----------------------------------------
CREATE TYPE public.user_role
  AS ENUM ('ADMIN', 'USER');

ALTER TABLE IF EXISTS public.users
  ALTER COLUMN role TYPE user_role using role::user_role;

---------------------------------------- VIDEOS ----------------------------------------
CREATE TYPE public.video_resolution
  AS ENUM ('FOUR_K', 'TWO_K', 'FHD', 'HD', 'SD', 'LD');

CREATE OR REPLACE FUNCTION int_to_video_resolution(int) RETURNS video_resolution AS $$
BEGIN
  CASE $1
    WHEN 0 THEN RETURN 'FOUR_K';
    WHEN 1 THEN RETURN 'TWO_K';
    WHEN 2 THEN RETURN 'FHD';
    WHEN 3 THEN RETURN 'HD';
    WHEN 4 THEN RETURN 'SD';
    WHEN 5 THEN RETURN 'LD';
    ELSE RAISE EXCEPTION 'Invalid integer value: %', $1;
  END CASE;
END;
$$ LANGUAGE plpgsql;

-- Add a new column for video_resolution[]
ALTER TABLE public.media_videos ADD COLUMN resolutions_new video_resolution[];

-- Populate the new column by converting the values
UPDATE public.media_videos
SET resolutions_new = ARRAY(
  SELECT int_to_video_resolution(x)
  FROM unnest(resolutions) AS x
);


-- Remove the old column and rename the new column
ALTER TABLE public.media_videos DROP COLUMN resolutions;
ALTER TABLE public.media_videos RENAME COLUMN resolutions_new TO resolutions;

