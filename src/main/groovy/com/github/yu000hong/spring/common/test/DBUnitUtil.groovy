package com.github.yu000hong.spring.common.test

import groovy.sql.Sql
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseDataSourceConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.CompositeDataSet
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
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
    protected final File datasetDir = new File('./src/test/resources/data')
    protected final File sqlDir = new File('./sql')
    protected DataSource dataSource
    protected IDataSet dataSet

    public DBUnitUtil(DataSource dataSource, List<String> datasetFileNames) {
        IDataSet[] dataSets = new IDataSet[datasetFileNames.size()]
        (0..datasetFileNames.size() - 1).each { i ->
            def builder = new FlatXmlDataSetBuilder()
            builder.setColumnSensing(true)
            def file = new File(datasetDir, datasetFileNames[i])
            dataSets[i] = builder.build(file)
        }
        this.dataSet = new CompositeDataSet(dataSets)
        this.dataSource = dataSource
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
        sql.execute("DROP TABLE IF EXISTS `${table}`".toString())
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
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory())
        config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler())
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE)
        config.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, '`')
        return conn
    }

}
