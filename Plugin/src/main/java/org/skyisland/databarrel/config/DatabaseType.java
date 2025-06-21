package org.skyisland.databarrel.config;

import lombok.Getter;

@Getter
public enum DatabaseType {
    MYSQL("com.mysql.cj.jdbc.Driver", "mysql"),
    POSTGRESQL("org.postgresql.Driver", "postgres"),
    H2("org.h2.Driver", "h2");

    private final String driverClass;
    private final String dbmsType;

    DatabaseType(String driverName, String dbmsType) {
        this.driverClass = driverName;
        this.dbmsType = dbmsType;
    }

}
