package org.buriy.medved.backend

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.ConfigurableEnvironment
import java.sql.DriverManager
import java.sql.SQLException


@Order(Ordered.LOWEST_PRECEDENCE)
class EnvironmentPostProcessorImpl: EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {

        val url = environment.getProperty("spring.datasource.url")!!
        val user = environment.getProperty("spring.datasource.username")!!
        val pwd = environment.getProperty("spring.datasource.password")!!
        val dbName = getDatabaseName(url)

        val dbProperties = DBProperties(url, dbName,user, pwd)



        if (!databaseExists(dbProperties)) {
            createDatabase(dbProperties)
        }
    }

    private fun getDatabaseName(url: String): String {
        return url.substringAfterLast("/")
    }

    private fun databaseExists(dbProperties: DBProperties): Boolean {

        val baseUrl = dbProperties.dataSourceUrl.substringBeforeLast("/") + "/postgres"
        val query = "SELECT 1 FROM pg_database WHERE datname = ?"
        return try {
            DriverManager.getConnection(baseUrl, dbProperties.username, dbProperties.password).use { connection ->
                connection.prepareStatement(query).use { preparedStatement ->
                    preparedStatement.setString(1, dbProperties.dbName)
                    preparedStatement.executeQuery().use { resultSet -> resultSet.next() }
                }
            }
        } catch (e: SQLException) {
//            logger.error("Ошибка при проверке базы данных: ${e.message}")
            false
        }
    }

    private fun createDatabase(dbProperties: DBProperties) {

        val baseUrl = dbProperties.dataSourceUrl.substringBeforeLast("/") + "/postgres"
        try {
            DriverManager.getConnection(baseUrl,dbProperties.username, dbProperties.password).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE DATABASE \"${dbProperties.dbName}\"")
//                    logger.info("База данных $dbName успешно создана.")
                }
            }
        } catch (e: SQLException) {
//            logger.error("Ошибка при создании базы данных: ${e.message}")
        }
    }

    data class DBProperties(
        val dataSourceUrl: String,
        val dbName: String,
        val username: String,
        val password: String,
    )
}