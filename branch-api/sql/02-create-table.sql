USE TESTDB;
GO

IF OBJECT_ID('dbo.branches', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.branches (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,

        branch_code NVARCHAR(50) NOT NULL,
        branch_name NVARCHAR(255) NOT NULL,
        address NVARCHAR(1000) NOT NULL,
        city NVARCHAR(150) NOT NULL,
        country NVARCHAR(100) NOT NULL,

        latitude DECIMAL(10,7) NOT NULL,
        longitude DECIMAL(10,7) NOT NULL,

        phone_number NVARCHAR(30) NULL,
        active BIT NOT NULL
            CONSTRAINT DF_branches_active DEFAULT 1,

        created_at DATETIME2 NOT NULL
            CONSTRAINT DF_branches_created_at
            DEFAULT SYSUTCDATETIME(),

        updated_at DATETIME2 NOT NULL
            CONSTRAINT DF_branches_updated_at
            DEFAULT SYSUTCDATETIME(),

        CONSTRAINT UQ_branches_branch_code
            UNIQUE (branch_code),

        CONSTRAINT CK_branches_latitude
            CHECK (latitude BETWEEN -90 AND 90),

        CONSTRAINT CK_branches_longitude
            CHECK (longitude BETWEEN -180 AND 180)
    );
END;
GO



/*
USE TESTDB_INTEGRATION;
GO

IF OBJECT_ID(N'dbo.branches', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.branches (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,

        branch_code NVARCHAR(50) NOT NULL,
        branch_name NVARCHAR(255) NOT NULL,
        address NVARCHAR(1000) NOT NULL,
        city NVARCHAR(150) NOT NULL,
        country NVARCHAR(100) NOT NULL,

        latitude DECIMAL(10,7) NOT NULL,
        longitude DECIMAL(10,7) NOT NULL,

        phone_number NVARCHAR(30) NULL,

        active BIT NOT NULL
            CONSTRAINT DF_integration_branches_active
            DEFAULT 1,

        created_at DATETIME2 NOT NULL
            CONSTRAINT DF_integration_branches_created_at
            DEFAULT SYSUTCDATETIME(),

        updated_at DATETIME2 NOT NULL
            CONSTRAINT DF_integration_branches_updated_at
            DEFAULT SYSUTCDATETIME(),

        CONSTRAINT UQ_integration_branches_branch_code
            UNIQUE (branch_code),

        CONSTRAINT CK_integration_branches_latitude
            CHECK (latitude BETWEEN -90 AND 90),

        CONSTRAINT CK_integration_branches_longitude
            CHECK (longitude BETWEEN -180 AND 180)
    );
END;
GO

*/