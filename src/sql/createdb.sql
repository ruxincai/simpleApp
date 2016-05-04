CREATE TABLE submission (
  id   SERIAL NOT NULL,
  last_update TIMESTAMPTZ NOT NULL,
  userName TEXT NOT NULL,
  city VARCHAR(30),
  text TEXT,

  PRIMARY KEY (id)
);

CREATE TABLE response (
  id SERIAL NOT NULL,
  submission_id INT NOT NULL,
  text TEXT,
  PRIMARY KEY (id),

  FOREIGN KEY (submission_id) REFERENCES submission (id) ON DELETE CASCADE
);

