-- Avocado Share schema generation commands

-- Create users with password set to "secret password"
-- CREATE identity avocado_admin WITH ENCRYPTED PASSWORD 'a584efafa8f9ea7fe5cf18442f32b07b';
-- CREATE identity avocado_web WITH ENCRYPTED PASSWORD 'a584efafa8f9ea7fe5cf18442f32b07b';

CREATE SCHEMA avocado_share;

-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avocado_share TO avocado_web;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avocado_share TO avocado_admin;

SET search_path TO avocado_share;

CREATE TABLE access_control
(
  Id INTEGER NOT NULL,
  creation_date timestamp NOT NULL,
  CONSTRAINT pk_access_control PRIMARY KEY (Id)
);

CREATE TABLE file
(
  Id INTEGER NOT NULL,
  Title VARCHAR(50) NOT NULL,
  Description VARCHAR(512) NOT NULL,
  LastChanged timestamp NOT NULL,
  CONSTRAINT pk_File PRIMARY KEY(Id),
  CONSTRAINT fk_File FOREIGN KEY(Id) REFERENCES access_control(Id)
);

CREATE TABLE file_version
(
  id INTEGER NOT NULL,
  type VARCHAR(32) NOT NULL,
  filename VARCHAR(32) NOT NULL,
  creation_date timestamp NOT NULL,
  CONSTRAINT pk_file_version PRIMARY KEY(id, filename),
  CONSTRAINT fk_file_version FOREIGN KEY(id) REFERENCES file(id)
)

CREATE TABLE access_group
(
  Id INTEGER NOT NULL,
  Name VARCHAR(32) NOT NULL,
  Description VARCHAR(512) NOT NULL,
  CONSTRAINT pk_access_group PRIMARY KEY(Id),
  CONSTRAINT fk_access_group_Id FOREIGN KEY(Id) REFERENCES access_control(Id)
);

CREATE TABLE module
(
  Id INTEGER NOT NULL,
  Name VARCHAR(32) NOT NULL,
  Description VARCHAR(512) NOT NULL,
  CONSTRAINT pk_Module PRIMARY KEY(Id),
  CONSTRAINT fk_Module_Id FOREIGN KEY(Id) REFERENCES access_control(Id)
);

CREATE TABLE identity
(
  id INTEGER NOT NULL,
  prename VARCHAR(50) NOT NULL,
  surname VARCHAR(50) NOT NULL,
  avatar VARCHAR(32) NOT NULL,        -- Path to file?
  description VARCHAR(512) NOT NULL,
  password VARCHAR(10) NOT NULL,
  CONSTRAINT pk_identity PRIMARY KEY(Id),
  CONSTRAINT fk_identity_id FOREIGN KEY(Id) REFERENCES access_control(Id)
);


CREATE TABLE ownership
(
  owner_id INTEGER NOT NULL,
  object_id INTEGER NOT NULL,
  CONSTRAINT fk_ownership_owner_id FOREIGN KEY(owner_id) REFERENCES access_control(id),
  CONSTRAINT fk_ownership_objectid FOREIGN KEY(object_id) REFERENCES access_control(id),
  CONSTRAINT pk_ownership PRIMARY KEY(object_id),
  CONSTRAINT uc_ownership UNIQUE(object_id, owner_id)
);

CREATE TABLE email
(
  identity_id INTEGER NOT NULL,
  address VARCHAR(128) NOT NULL,
  verified BOOLEAN NOT NULL,
  CONSTRAINT pk_email PRIMARY KEY(identity_id, address),
  CONSTRAINT fk_email_id FOREIGN KEY(identity_id) REFERENCES identity(Id)
);

CREATE TABLE email_verification
(
  identity_id INTEGER NOT NULL,
  address VARCHAR(128) NOT NULL,
  expiry timestamp NOT NULL,
  verification_code VARCHAR(256) NOT NULL,
  CONSTRAINT fk_email_verification FOREIGN KEY(identity_id, address) REFERENCES email(identity_id, address),
  CONSTRAINT pk_email_verification PRIMARY KEY(identity_id, address, verification_code)
);


CREATE TABLE category
(
  object_id INTEGER NOT NULL,
  Name VARCHAR(100) NOT NULL,
  CONSTRAINT pk_Category PRIMARY KEY(object_id, Name),
  CONSTRAINT fk_Category_object_id FOREIGN KEY (object_id) REFERENCES access_control(Id)
);


CREATE TABLE access_level
(
  Level INTEGER NOT NULL,
  Readable BOOLEAN NOT NULL,
  Writable BOOLEAN NOT NULL,
  Manageable BOOLEAN NOT NULL,
  CONSTRAINT pk_access_level PRIMARY KEY (Level),
  CONSTRAINT uc_access_level UNIQUE (Readable, Writable, Manageable)
);

CREATE TABLE rights
(
  object_id INTEGER NOT NULL,
  owner_id INTEGER NOT NULL,
  Level INTEGER NOT NULL,
  CONSTRAINT fk_Rights_Level FOREIGN KEY(Level) REFERENCES access_level(Level),
  CONSTRAINT fk_Rights_owner_id FOREIGN KEY(owner_id) REFERENCES access_control(Id),
  CONSTRAINT fk_Rights_object_id FOREIGN KEY(object_id) REFERENCES access_control(Id),
  CONSTRAINT pk_Rights PRIMARY KEY(object_id, owner_id)
);


CREATE TABLE default_access
(
  object_id INTEGER NOT NULL,
  Level INTEGER NOT NULL,
  CONSTRAINT fk_default_access_object_id FOREIGN KEY(object_id) REFERENCES access_control(id),
  CONSTRAINT fk_default_access_Level FOREIGN KEY(Level) REFERENCES access_level(level),
  CONSTRAINT pk_default_access PRIMARY KEY(object_id)
);


CREATE TABLE rating
(
  object_id INTEGER NOT NULL,
  identity_id INTEGER NOT NULL,
  Rating INTEGER NOT NULL,
  CONSTRAINT fk_Rating_object_id FOREIGN KEY(object_id) REFERENCES access_control(Id),
  CONSTRAINT fk_Rating_identity_id FOREIGN KEY(identity_id) REFERENCES identity(Id),
  CONSTRAINT pk_Rating PRIMARY KEY(object_id, identity_id),
  CONSTRAINT chk_Rating CHECK (Rating >= 0 AND Rating <= 5)
);

CREATE TABLE uploaded_into
(
  file_id INTEGER NOT NULL,
  module_id INTEGER NOT NULL,
  Level INTEGER NOT NULL,
  CONSTRAINT fk_uploaded_into_file_id FOREIGN KEY(file_id) REFERENCES File(Id),
  CONSTRAINT fk_uploaded_into_module_id FOREIGN KEY(module_id) REFERENCES Module(Id),
  CONSTRAINT pk_uploaded_into PRIMARY KEY(file_id),
  CONSTRAINT uc_uploaded_into UNIQUE(file_id, module_id)
);
