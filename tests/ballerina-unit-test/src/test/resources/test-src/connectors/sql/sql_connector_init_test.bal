import ballerina/sql;
import ballerina/jdbc;

sql:PoolOptions properties = { maximumPoolSize: 1,
    idleTimeout: 600000, connectionTimeout: 30000, autoCommit: true, maxLifetime: 1800000,
    minimumIdle: 1, validationTimeout: 5000,
    connectionInitSql: "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS" };

map propertiesMap = { "loginTimeout": 109 };
sql:PoolOptions properties3 = { dataSourceClassName: "org.hsqldb.jdbc.JDBCDataSource" };

map propertiesMap2 = { "loginTimeout": 109 };
sql:PoolOptions properties4 = { dataSourceClassName: "org.hsqldb.jdbc.JDBCDataSource" };

sql:PoolOptions properties5 = { dataSourceClassName: "org.hsqldb.jdbc.JDBCDataSource" };

map propertiesMap3 = { "url": "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT" };
sql:PoolOptions properties6 = { dataSourceClassName: "org.hsqldb.jdbc.JDBCDataSource" };

function testConnectionPoolProperties1() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        password: "",
        poolOptions: properties
    };


    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}

function testConnectionPoolProperties2() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        poolOptions: properties
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}

function testConnectionPoolProperties3() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA"
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}


function testConnectorWithDefaultPropertiesForListedDB() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        poolOptions: {}
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}

function testConnectorWithWorkers() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        poolOptions: {}
    };

    worker w1 {
        int x = 0;
        json y;

        table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

        json j = check <json>dt;
        testDB.stop();
        return j;
    }
    worker w2 {
        int x = 10;
    }
}

function testConnectorWithDataSourceClass() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        poolOptions: properties3,
        dbOptions: propertiesMap
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}

function testConnectorWithDataSourceClassAndProps() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        password: "",
        poolOptions: properties4,
        dbOptions: propertiesMap2
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}

function testConnectorWithDataSourceClassWithoutURL() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        password: "",
        poolOptions: properties5
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}

function testConnectorWithDataSourceClassURLPriority() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        password: "",
        poolOptions: properties6,
        dbOptions: propertiesMap3
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}


function testPropertiesGetUsedOnlyIfDataSourceGiven() returns (json) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR_INIT",
        username: "SA",
        password: "",
        poolOptions: { maximumPoolSize: 1 },
        dbOptions: { "invalidProperty": 109 }
    };

    table dt = check testDB->select("SELECT  FirstName from Customers where registrationID = 1", ());

    json j = check <json>dt;
    testDB.stop();
    return j;
}
