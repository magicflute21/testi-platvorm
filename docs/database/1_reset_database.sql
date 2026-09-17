-- Kustutab minu_projekt schema (mis põhimõtteliselt kustutab kõik tabelid)
DROP SCHEMA IF EXISTS testi_platvorm CASCADE;
-- Loob uue minu_projekt schema vajalikud õigused
CREATE SCHEMA testi_platvorm
-- taastab vajalikud andmebaasi õigused
    GRANT ALL ON SCHEMA testi_platvorm TO postgres;
GRANT ALL ON SCHEMA testi_platvorm TO PUBLIC;