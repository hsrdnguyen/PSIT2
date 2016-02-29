-- Avocado Share schema generation commands

-- CREATE identity avocado_admin WITH ENCRYPTED PASSWORD 'a584efafa8f9ea7fe5cf18442f32b07b';
-- CREATE identity avocado_web WITH ENCRYPTED PASSWORD 'a584efafa8f9ea7fe5cf18442f32b07b';

CREATE SCHEMA avocado_share;

-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avocado_share TO avocado_web;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avocado_share TO avocado_admin;

SET search_path TO avocado_share;

CREATE TABLE access_control
(
  Id int NOT NULL,
  CreationDate timestamp NOT NULL,
  CONSTRAINT pk_access_control PRIMARY KEY (Id)
);

CREATE TABLE file
(
  Id int NOT NULL,
  Title VARCHAR(50) NOT NULL,
  Description VARCHAR(512) NOT NULL,
  Filename VARCHAR(32) NOT NULL,
  Type VARCHAR(32) NOT NULL,
  LastChanged timestamp NOT NULL,
  CONSTRAINT pk_File PRIMARY KEY(Id),
  CONSTRAINT fk_File FOREIGN KEY(Id) REFERENCES access_control(Id)
);

CREATE TABLE accessgroup
(
  Id int NOT NULL,
  Name VARCHAR(32) NOT NULL,
  Description Varchar(512) NOT NULL,
  CONSTRAINT pk_AccessGroup PRIMARY KEY(Id),
  CONSTRAINT fk_AccessGroup_Id FOREIGN KEY(Id) REFERENCES access_control(Id)
);

CREATE TABLE module
(
  Id int NOT NULL,
  Name VARCHAR(32) NOT NULL,
  Description Varchar(512) NOT NULL,
  CONSTRAINT pk_Module PRIMARY KEY(Id),
  CONSTRAINT fk_Module_Id FOREIGN KEY(Id) REFERENCES access_control(Id)
);

CREATE TABLE identity
(
  Id int NOT NULL,
  Prename VARCHAR(50) NOT NULL,
  Surname VARCHAR(50) NOT NULL,
  Avatar VARCHAR(32) NOT NULL,        -- Path to file?
  Description Varchar(512) NOT NULL,
  Password Varchar(10),
  CONSTRAINT pk_identity PRIMARY KEY(Id),
  CONSTRAINT fk_identity_Id FOREIGN KEY(Id) REFERENCES access_control(Id)
);

CREATE TABLE email
(
  identity_id int NOT NULL,
  address VARCHAR(128) NOT NULL,
  verified BOOLEAN NOT NULL,
  CONSTRAINT pk_email PRIMARY KEY(identity_id, address),
  CONSTRAINT fk_email_id FOREIGN KEY(identity_id) REFERENCES identity(Id),
);

CREATE TABLE email_verification
(
  identity_id int NOT NULL,
  address VARCHAR(128) NOT NULL,
  expiry timestamp NOT NULL,
  verification_code VARCHAR(256) NOT NULL,
  CONSTRAINT fk_email_verification FOREIGN KEY(identity_id, address) REFERENCES email(identity_id, address),
  CONSTRAINT pk_email_verification PRIMARY KEY(identity_id, address, verification_code)
);


CREATE TABLE rating
(
  object_id int NOT NULL,
  identity_id int NOT NULL,
  Rating int NOT NULL,
  CONSTRAINT fk_Rating_object_id FOREIGN KEY(object_id) REFERENCES access_control(Id),
  CONSTRAINT fk_Rating_identity_id FOREIGN KEY(identity_id) REFERENCES identity(Id),
  CONSTRAINT pk_Rating PRIMARY KEY(object_id, identity_id),
  CONSTRAINT chk_Rating CHECK (Rating >= 0 AND Rating <= 5)
);

CREATE TABLE category
(
  object_id int NOT NULL,
  Name VARCHAR(100) NOT NULL,
  CONSTRAINT pk_Category PRIMARY KEY(object_id, Name),
  CONSTRAINT fk_Category_object_id FOREIGN KEY (object_id) REFERENCES access_control(Id)
);


CREATE TABLE access_level
(
  Level int NOT NULL,
  Readable BOOLEAN NOT NULL,
  Writable BOOLEAN NOT NULL,
  Manageable BOOLEAN NOT NULL,
  CONSTRAINT pk_access_level PRIMARY KEY (Level),
  CONSTRAINT uc_access_level UNIQUE (Readable, Writable, Manageable),
);

CREATE TABLE rights
(
  object_id int NOT NULL,
  OwnerId int NOT NULL,
  Level int NOT NULL,
  CONSTRAINT fk_Rights_Level FOREIGN KEY(Level) REFERENCES access_level(Level),
  CONSTRAINT fk_Rights_OwnerId FOREIGN KEY(OwnerId) REFERENCES access_control(Id),
  CONSTRAINT fk_Rights_object_id FOREIGN KEY(object_id) REFERENCES access_control(Id),
  CONSTRAINT pk_Rights PRIMARY KEY(object_id, OwnerId)
);


CREATE TABLE default_access
(
  object_id int NOT NULL,
  Level int NOT NULL,
  CONSTRAINT fk_default_access_object_id FOREIGN KEY(object_id) REFERENCES default_access(object_id),
  CONSTRAINT fk_default_access_Level FOREIGN KEY(Level) REFERENCES access_level(Id),
  CONSTRAINT pk_default_access PRIMARY KEY(object_id)
)


CREATE TABLE rating
(
  object_id int NOT NULL,
  identity_id int NOT NULL,
  Rating int NOT NULL,
  CONSTRAINT fk_Rating_FileId FOREIGN KEY(FileId) REFERENCES access_control(Id),
  CONSTRAINT fk_Rating_FileId FOREIGN KEY(identity_id) REFERENCES identity(Id),
  CONSTRAINT pk_Rating PRIMARY KEY(FileId, identity_id)
);

CREATE TABLE uploaded_into
(
  FileId int NOT NULL,
  ModuleId int NOT NULL,
  Level int NOT NULL,
  CONSTRAINT fk_UploadedInto_FileId FOREIGN KEY(FileId) REFERENCES File(Id),
  CONSTRAINT fk_UploadedInto_ModuleId FOREIGN KEY(ModuleId) REFERENCES Module(Id),
);
