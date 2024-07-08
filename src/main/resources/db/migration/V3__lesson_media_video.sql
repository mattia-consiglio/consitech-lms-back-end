--V3__lesson_media_video
ALTER TABLE IF EXISTS public.lessons DROP COLUMN IF EXISTS video_thumbnail;

ALTER TABLE IF EXISTS public.lessons RENAME video_id TO video_id_old;

ALTER TABLE IF EXISTS public.lessons ADD COLUMN video_id uuid;

ALTER TABLE IF EXISTS public.lessons DROP COLUMN IF EXISTS video_id_old;