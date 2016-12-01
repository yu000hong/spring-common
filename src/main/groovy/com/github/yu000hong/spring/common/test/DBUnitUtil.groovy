package com.github.yu000hong.spring.common.test

import groovy.sql.Sql
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseDataSourceConnection
import org.dbunit.database.DefaultMetadataHandler
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.CompositeDataSet
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.h2.H2DataTypeFactory
import org.dbunit.ext.mysql.MySqlDataTypeFactory
import org.dbunit.ext.mysql.MySqlMetadataHandler
import org.dbunit.operation.DatabaseOperation

import javax.sql.DataSource

/**
 * DBUnit测试工具类
 * 使用这个工具类需要满足一定规范:
 * - 测试数据必须放在./src/test/resources/data目录下
 * - 数据表的定义必须放在./sql目录下
 */
public class DBUnitUtil {
    protected File datasetDir = new File('./src/test/resources/data')
    protected File sqlDir = new File('./sql')
    protected DBType type = DBType.MySQL
    protected DataSource dataSource
    protected IDataSet dataSet

    public DBUnitUtil(DataSource dataSource, List<String> datasetFileNames) {
        IDataSet[] dataSets = new IDataSet[datasetFileNames.size()]
        if (datasetFileNames) {
            (0..datasetFileNames.size() - 1).each { i ->
                def builder = new FlatXmlDataSetBuilder()
                builder.setColumnSensing(true)
                def file = new File(datasetDir, datasetFileNames[i])
                dataSets[i] = builder.build(file)
            }
        }
        this.dataSet = new CompositeDataSet(dataSets)
        this.dataSource = dataSource
    }

    public void setDatasetDir(String dir) {
        datasetDir = new File(dir)
    }

    public void setSqlDir(String dir) {
        sqlDir = new File(dir)
    }

    public void setType(DBType type) {
        this.type = type
    }

    public void assertEmpty() {
        def meta = dataSource.getConnection().getMetaData()
        def resultSet = meta.getTables(null, null, null, ['TABLE'] as String[])
        if (resultSet.next()) {
            throw new AssertionError()
        }
    }

    /**
     * 创建表
     * @param table 表名
     */
    public void createTable(String table) {
        def sql = new Sql(dataSource)
        def file = new File(sqlDir, "${table}.sql")
        sql.execute(file.text)
        sql.close()
    }

    /**
     * 删除表
     * @param table 表名
     */
    public void dropTable(String table) {
        def sql = new Sql(dataSource)
        def escape
        switch (type) {
            case DBType.MySQL:
                escape = '`'
                break
            case DBType.H2:
                escape = '"'
                break
            default:
                escape = ''
                break
        }
        sql.execute("DROP TABLE IF EXISTS $escape${table}$escape".toString())
        sql.close()
    }

    /**
     * 数据初始化
     */
    public void cleanAndInsert() {
        IDatabaseConnection dbConn = getConnection()
        DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet)
    }

    /**
     * 获取MySQL连接
     */
    public IDatabaseConnection getConnection() {
        IDatabaseConnection conn = new DatabaseDataSourceConnection(dataSource)
        DatabaseConfig config = conn.getConfig()
        switch (type) {
            case DBType.MySQL:
                config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory())
                config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler())
                config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE)
                config.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, '`')
                break
            case DBType.H2:
                config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory())
                config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new DefaultMetadataHandler())
                config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE)
                config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE)
                config.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, '"')
                break
        }
        return conn
    }

    public static enum DBType {
        MySQL,
        H2
    }

}
