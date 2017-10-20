package com.baljeet.ref.service

import com.baljeet.ref.service.model.*
import dagger.Module
import dagger.Provides
import org.hibernate.cfg.AvailableSettings.*
import org.hibernate.dialect.SQLServer2005Dialect
import org.hibernate.jpa.AvailableSettings.JDBC_URL
import org.hibernate.jpa.HibernatePersistenceProvider
import org.hibernate.tool.schema.Action
import java.net.URL
import java.util.*
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.SharedCacheMode
import javax.persistence.ValidationMode
import javax.persistence.spi.ClassTransformer
import javax.persistence.spi.PersistenceUnitInfo
import javax.persistence.spi.PersistenceUnitTransactionType
import javax.sql.DataSource


fun managedClasses(): List<String> {
    return  listOf(
        LoanDeal::class.java.name,
        DealStatus::class.java.name,
        Seniority::class.java.name,
        Issuer::class.java.name,
        Counterparty::class.java.name,
        CounterpartyMEI::class.java.name
)}

@Module
class JPAProviderModule {

    @Provides
    fun entityManager(emFactory: EntityManagerFactory): EntityManager {
        return emFactory.createEntityManager()
    }

    @Provides @Singleton
    fun entityManagerFactory(): EntityManagerFactory {
        val entityManagerFactory =
                HibernatePersistenceProvider()
                        .createContainerEntityManagerFactory(
                                MyPersistenceUnitInfo(managedClasses()),
                                mapOf(JDBC_URL to "jdbc:jtds:sqlserver://LDNDCM05330V05A/LoanStatic;instance=LF6_MAIN1_DEV;user=sysLoansDev;password=xH8#FCFF;domain=INTRANET",
                                        DRIVER to "net.sourceforge.jtds.jdbc.Driver",
                                        DEFAULT_SCHEMA to "dbo",
                                        DIALECT to SQLServer2005Dialect::class.java,
                                        HBM2DDL_AUTO to Action.NONE,
                                        SHOW_SQL to true,
                                        QUERY_STARTUP_CHECKING to false,
                                        GENERATE_STATISTICS to false,
                                        USE_REFLECTION_OPTIMIZER to false,
                                        USE_SECOND_LEVEL_CACHE to false,
                                        USE_QUERY_CACHE to false,
                                        USE_STRUCTURED_CACHE to false,
                                        SCANNER_DISCOVERY to "",
                                        STATEMENT_BATCH_SIZE to 20,
                                        JMX_ENABLED to false))

        return entityManagerFactory
    }
}

/**
 * In JPA config the persistence-unit config is usually in xml.
 * This is the code equivalent of the XML
 */
class MyPersistenceUnitInfo(val managedClasses: List<String>) : PersistenceUnitInfo {

    override fun getMappingFileNames(): List<String> {
        return listOf()
    }

    override fun getNewTempClassLoader(): ClassLoader? {
        return null
    }

    override fun getPersistenceUnitName(): String {
        return "BaSEPersistenceUnit"
    }

    override fun getSharedCacheMode(): SharedCacheMode {
        return SharedCacheMode.ENABLE_SELECTIVE
    }

    override fun getClassLoader(): ClassLoader? {
        return null
    }

    override fun getTransactionType(): PersistenceUnitTransactionType {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL
    }

    override fun getProperties(): Properties {
        return Properties()
    }

    override fun getPersistenceXMLSchemaVersion(): String {
        TODO("not implemented")
    }

    override fun addTransformer(p0: ClassTransformer?) {
        // No-op
    }

    override fun getManagedClassNames(): List<String> {
        return managedClasses
    }

    override fun getJarFileUrls(): List<URL> {
        return listOf()
    }

    override fun getPersistenceProviderClassName(): String? {
        return org.hibernate.jpa.HibernatePersistenceProvider::class.java.simpleName
    }

    override fun getJtaDataSource(): DataSource? {
        return null
    }

    override fun getNonJtaDataSource(): DataSource? {
        return null
    }

    override fun excludeUnlistedClasses(): Boolean {
        return false
    }

    override fun getValidationMode(): ValidationMode {
        return ValidationMode.AUTO
    }

    override fun getPersistenceUnitRootUrl(): URL? {
        return null
    }
}