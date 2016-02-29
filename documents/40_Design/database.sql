-- Avocado Share schema generation commands

CREATE SCHEMA avocado;
CREATE TABLE avocado.AccessControl
(
  Id int NOT NULL,
  CreationDate timestamp NOT NULL,
  CONSTRAINT pk_AccessControl PRIMARY KEY (Id)
);

CREATE TABLE avocado.File
(
  Id int NOT NULL,
  Title VARCHAR(50) NOT NULL,
  Description VARCHAR(512) NOT NULL,
  Filename VARCHAR(32) NOT NULL,
  Type VARCHAR(32) NOT NULL,
  LastChanged timestamp NOT NULL,
  CONSTRAINT pk_File PRIMARY KEY(Id),
  CONSTRAINT fk_File FOREIGN KEY(Id) REFERENCES AccessControl(Id)
);

CREATE TABLE avocado.Group
(
  Id int NOT NULL,
  Name VARCHAR(32) NOT NULL,
  Description Varchar(512) NOT NULL,
  CONSTRAINT pk_Group PRIMARY KEY(Id),
  CONSTRAINT fk_Group FOREIGN KEY(Id) REFERENCES AccessControl(Id)
);

CREATE TABLE avocado.Module
(
  Id int NOT NULL,
  Name VARCHAR(32) NOT NULL,
  Description Varchar(512) NOT NULL,
  CONSTRAINT pk_Group PRIMARY KEY(Id),
  CONSTRAINT fk_Group FOREIGN KEY(Id) REFERENCES AccessControl(Id)
);

CREATE TABLE User
(
  Id int NOT NULL,
  Prename VARCHAR(50) NOT NULL,
  Surname VARCHAR(50) NOT NULL,
  Avatar VARCHAR(32) NOT NULL,        -- Path to file?
  Description Varchar(512) NOT NULL,
  Salt Varchar(8),
  Password Varchar(10),
  CONSTRAINT pk_Group PRIMARY KEY(Id),
  CONSTRAINT fk_Group FOREIGN KEY(Id) REFERENCES AccessControl(Id)
);

CREATE TABLE EMail
(
  Id int NOT NULL,
  Address VARCHAR(128) NOT NULL,
  Verified BOOLEAN NOT NULL,
  CONSTRAINT pk_EMail PRIMARY KEY(Id, Address)
);

CREATE TABLE EMailVerification
(
  Id int NOT NULL,
  Address VARCHAR(128) NOT NULL,
  Expiry timestamp NOT NULL,
  VerificationCode VARCHAR(256) NOT NULL
);


CREATE TABLE Rating
(
  ObjectId int NOT NULL,
  UserId int NOT NULL,
  Rating int NOT NULL,
  CONSTRAINT fk_Rating_ObjectId FOREIGN KEY(ObjectId) REFERENCES AccessControl(Id),
  CONSTRAINT fk_Rating_UserId FOREIGN KEY(UserId) REFERENCES User(Id),
  CONSTRAINT pk_Rating PRIMARY KEY(ObjectId, UserId),
  CONSTRAINT chk_Rating CHECK (Rating >= 0 AND Rating <= 5)
);

CREATE TABLE Category
(
  ObjectId int NOT NULL,
  Name VARCHAR(100) NOT NULL,
  CONSTRAINT pk_Category PRIMARY KEY(ObjectId, Name),
  CONSTRAINT fk_Category_ObjectId FOREIGN KEY (ObjectId) REFERENCES AccessControl(Id)
);

CREATE TABLE Rights
(
  ObjectId int NOT NULL,
  OwnerId int NOT NULL,
  Level int NOT NULL,
  CONSTRAINT fk_Rights_Level FOREIGN KEY(Level) REFERENCES AccessLevel(Level),
  CONSTRAINT fk_Rights_OwnerId FOREIGN KEY(OwnerId) REFERENCES AccessControl(Id),
  CONSTRAINT fk_Rights_OwnerId FOREIGN KEY(ObjectId) REFERENCES AccessControl(Id),
);

CREATE TABLE AccessLevel
(
  Level int NOT NULL,
  Readable BOOLEAN NOT NULL,
  Writable BOOLEAN NOT NULL,
  Manageable BOOLEAN NOT NULL,
  CONSTRAINT pk_AccessLevel PRIMARY KEY (Level),
  CONSTRAINT uc_AccessLevel UNIQUE (Readable, Writable, Manageable)
);

CREATE TABLE Rating
(
  FileId int NOT NULL,
  UserId int NOT NULL,
  Rating int NOT NULL,
  CONSTRAINT fk_Rating_FileId FOREIGN KEY(FileId) REFERENCES File(Id),
  CONSTRAINT fk_Rating_FileId FOREIGN KEY(UserId) REFERENCES User(Id),
  CONSTRAINT pk_Rating PRIMARY KEY(FileId, UserId),
);

CREATE TABLE Rights
(
  OwnerId int NOT NULL,
  ObjectId int NOT NULL,
  Level int NOT NULL,
  CONSTRAINT fk_Rights_OwnerId FOREIGN KEY(OwnerId) REFERENCES AccessControl(Id),
  CONSTRAINT fk_Rights_ObjectId FOREIGN KEY(ObjectId) REFERENCES AccessControl(Id),
);


CREATE TABLE UploadedInto
(
  FileId int NOT NULL,
  ModuleId int NOT NULL,
  Level int NOT NULL,
  CONSTRAINT fk_UploadedInto_FileId FOREIGN KEY(FileId) REFERENCES File(Id),
  CONSTRAINT fk_UploadedInto_ModuleId FOREIGN KEY(ModuleId) REFERENCES Module(Id),
);
