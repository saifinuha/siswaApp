## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## postgreSQL starter

just make your own postgresql database
```sql
DROP DATABASE IF EXISTS "dbProjectSiswa";

CREATE DATABASE "dbProjectSiswa"
    WITH
    OWNER = yourname
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role VARCHAR(20)
);

INSERT INTO users (username, password, role)
VALUES (
    'admin',
    crypt('admin', gen_salt('bf')),
    'admin'
);

CREATE TABLE pembayaran_spp (
    id_siswa VARCHAR(20) PRIMARY KEY,
    nama_siswa VARCHAR(100),
    kelas VARCHAR(20),
    jurusan VARCHAR(50),
    pembayaran VARCHAR(50),
    jumlah INTEGER
);
```
