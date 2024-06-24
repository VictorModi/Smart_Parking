package mba.vm.smart.parking

import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

/**
 * SmartParking - mba.vm.smart.parking
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/3 下午4:16
 */

object DataSourceProvider {
    private val dataSource: HikariDataSource

    init {
        val config = HikariConfig()
        config.jdbcUrl = ConfigReader.getProperty("db.url")
        config.username = ConfigReader.getProperty("db.username")
        config.password = ConfigReader.getProperty("db.password")

        dataSource = HikariDataSource(config)
    }

    private fun getDataSource(): DataSource {
        return dataSource
    }

    private fun getSQLDelightDriver() : JdbcDriver {
        return getDataSource().asJdbcDriver()
    }

    fun getDatabase(): Database {
        return Database(getSQLDelightDriver())
    }

}
