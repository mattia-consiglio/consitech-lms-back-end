--V4__lesson_media_video

--MEDIA
CREATE TYPE public.media_type
  AS ENUM ('IMAGE', 'VIDEO', 'AUDIO');

ALTER TABLE IF EXISTS public.media
  RENAME type TO type_old;

ALTER TABLE IF EXISTS public.media
  ADD COLUMN type media_type;

--update media table from old type values
DO $$
    BEGIN
        IF EXISTS
            ( SELECT 1
              FROM   information_schema.tables
              WHERE  table_schema = 'public'
              AND    table_name = 'media'
            )
        THEN
            UPDATE public.media
            SET type = 'IMAGE'
            WHERE type_old = 0;
            
            UPDATE public.media
            SET type = 'VIDEO'
            WHERE type_old = 1;

            UPDATE public.media
            SET type = 'AUDIO'
            WHERE type_old = 2;
        END IF ;
    END
$$ ;

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
