package com.github.yu000hong.spring.common.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.annotation.Resource
import javax.sql.DataSource
import java.sql.DatabaseMetaData

@ContextConfiguration(locations = 'classpath:db.xml')
class TestDBUnitUtil extends AbstractTestNGSpringContextTests {
    private static final String ESCAPE = '"'
    private DBUnitUtil dbUnitUtil
    private DatabaseMetaData meta

    @Resource(name = 'dataSource')
    private DataSource dataSource

    @Autowired
    private EmbeddedDatabaseFactoryBean embeddedDatabaseFactoryBean

    @BeforeClass
    public void setup() {
        dbUnitUtil = new DBUnitUtil(dataSource, [])
        dbUnitUtil.setType(DBUnitUtil.DBType.H2)
        dbUnitUtil.setSqlDir("./src/test/resources/sql")
        dbUnitUtil.assertEmpty()
        meta = dataSource.getConnection().getMetaData()
    }

    @Test
    public void testCreateTable() {
        def tableName = 'account'
        dbUnitUtil.createTable(tableName)
        def rs = meta.getTables(null, null, tableName, ['TABLE'] as String[])
        Assert.assertTrue(rs.next())

        dataSource.connection.createStatement()
                .execute("DROP TABLE IF EXISTS $ESCAPE$tableName$ESCAPE")
        dbUnitUtil.assertEmpty()
    }

    @Test
    public void testDropTable() {
        def tableName = 'account'
        dataSource.connection.createStatement()
                .execute("CREATE TABLE IF NOT EXISTS $ESCAPE$tableName$ESCAPE(id BIGINT)")
        dbUnitUtil.dropTable(tableName)
        def rs = meta.getTables(null, null, tableName, ['TABLE'] as String[])
        Assert.assertFalse(rs.next())
        dbUnitUtil.assertEmpty()
    }

}
