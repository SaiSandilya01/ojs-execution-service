CREATE TABLE problems (
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title         TEXT        NOT NULL,
  description   TEXT        NOT NULL,
  difficulty    difficulty_level NOT NULL DEFAULT 'easy',
  tags          TEXT[],                 -- e.g. '{array,hash-table}'
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Keep titles unique if you like
CREATE UNIQUE INDEX problems_title_unique_idx ON problems (title);

-- Helpful for listing/filtering
CREATE INDEX problems_difficulty_idx ON problems (difficulty);

CREATE TABLE problem_test_cases (
  id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  problem_id  BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
  input       TEXT   NOT NULL,
  output      TEXT   NOT NULL,
  is_hidden   BOOLEAN NOT NULL DEFAULT TRUE,   -- sample vs hidden
  weight      INTEGER NOT NULL DEFAULT 1,      -- optional scoring weight
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX problem_test_cases_problem_id_idx
  ON problem_test_cases (problem_id);

CREATE TABLE submissions (
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  problem_id    BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
  -- user_id   BIGINT NULL REFERENCES users(id),   -- if/when you add auth
  code          TEXT        NOT NULL,
  language      TEXT        NOT NULL,              -- e.g. 'cpp', 'java', 'python3'
  status        submission_status NOT NULL DEFAULT 'PENDING',
  success       BOOLEAN,                           -- null while pending
  passed_cases  INTEGER NOT NULL DEFAULT 0,
  total_cases   INTEGER NOT NULL DEFAULT 0,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX submissions_problem_id_idx ON submissions (problem_id);
CREATE INDEX submissions_status_idx ON submissions (status);
-- if you add user_id, index (user_id, problem_id) for "my submissions" queries
