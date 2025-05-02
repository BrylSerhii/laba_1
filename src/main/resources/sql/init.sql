CREATE TABLE faculties (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           "limit" INT NOT NULL,
                           status TEXT not null DEFAULT 'OPEN'
);

CREATE TABLE applicants (
                            id SERIAL PRIMARY KEY,
                            full_name VARCHAR(255) NOT NULL,
                            average_grade NUMERIC(4, 2) NOT NULL,
                            faculty_id INT REFERENCES faculties(id),
                            user_id INT references users(id),
                            results TEXT NOT NULL default '0 0 0',
                            sum float
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       login VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);
