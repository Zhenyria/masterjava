DROP TABLE IF EXISTS users_groups;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS user_seq;
DROP TABLE IF EXISTS cities;
DROP SEQUENCE IF EXISTS cities_seq;
DROP TABLE IF EXISTS groups;
DROP SEQUENCE IF EXISTS groups_seq;
DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS projects_seq;
DROP TYPE IF EXISTS user_flag;
DROP TYPE IF EXISTS group_type;

CREATE TYPE group_type AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');
CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');

CREATE SEQUENCE projects_seq START 100000;

CREATE TABLE projects
(
    id          INTEGER PRIMARY KEY DEFAULT nextval('projects_seq'),
    name_val    TEXT NOT NULL,
    description TEXT NOT NULL,
    CONSTRAINT idx_projects_name_val UNIQUE (name_val)
);

ALTER SEQUENCE projects_seq OWNED BY projects.id;

CREATE SEQUENCE groups_seq START 100000;

CREATE TABLE groups
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('groups_seq'),
    project_id INTEGER    NOT NULL,
    name_val   TEXT       NOT NULL,
    type_val   group_type NOT NULL,
    CONSTRAINT fk_groups_project_id_projects_id FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT idx_groups_name_type UNIQUE (name_val, type_val)
);

ALTER SEQUENCE groups_seq OWNED BY groups.id;

CREATE SEQUENCE cities_seq START 100000;

CREATE TABLE cities
(
    id       INTEGER PRIMARY KEY DEFAULT nextval('cities_seq'),
    code     TEXT NOT NULL,
    name_val TEXT NOT NULL,
    CONSTRAINT idx_cities_name UNIQUE (name_val)
);

ALTER SEQUENCE cities_seq OWNED BY cities.id;

CREATE SEQUENCE user_seq START 100000;

CREATE TABLE users
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
    full_name TEXT      NOT NULL,
    email     TEXT      NOT NULL,
    flag      user_flag NOT NULL,
    city_id   INTEGER   NOT NULL,
    CONSTRAINT fk_users_city_id_cities_id FOREIGN KEY (city_id) REFERENCES cities (id)
);

CREATE UNIQUE INDEX email_idx ON users (email);

ALTER SEQUENCE user_seq OWNED BY users.id;

CREATE TABLE users_groups
(
    user_id  INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    CONSTRAINT fk_users_groups_user_id_users_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_users_groups_group_id_groups_id FOREIGN KEY (group_id) REFERENCES groups (id)
);

