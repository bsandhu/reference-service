package com.baljeet.ref.service

import com.baljeet.ref.service.events.DealEventListener
import com.baljeet.ref.service.service.DealService
import com.google.common.eventbus.EventBus
import dagger.Component
import dagger.Module
import dagger.Provides
import org.hibernate.cfg.AvailableSettings.*
import org.hibernate.dialect.H2Dialect
import org.hibernate.jpa.AvailableSettings
import org.hibernate.jpa.HibernatePersistenceProvider
import org.hibernate.tool.schema.Action
import javax.inject.Singleton
import javax.jms.Session
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import com.fakejms.FakeSession

// TODO should be in the test package - some tooling issues with Dagger2

@Component(modules = arrayOf(
        FakeJPAProviderModule::class,
        FakeJMSProviderModule::class,
        FakeEventBusProviderModule::class))
@Singleton
interface DealServiceWithFakes {
    fun dealService(): DealService
    fun dealListener(): DealEventListener
    fun eventBus(): EventBus
    fun envConfig(): EnvConfig
    fun jmsSession(): Session
}

@Module
class FakeJPAProviderModule {

    @Provides
    @Singleton
    fun envConfig(): EnvConfig {
        return EnvConfig()
    }

    @Provides
    fun entityManager(emFactory: EntityManagerFactory): EntityManager {
        return emFactory.createEntityManager()
    }

    @Provides
    @Singleton
    fun entityManagerFactory(): EntityManagerFactory {
        val entityManagerFactory =
                HibernatePersistenceProvider()
                        .createContainerEntityManagerFactory(
                                MyPersistenceUnitInfo(managedClasses()),
                                mapOf(AvailableSettings.JDBC_URL to "jdbc:h2:mem:test;USER=sa;PASSWORD=;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;INIT=RUNSCRIPT FROM 'classpath:h2Init.sql'",
                                        DRIVER to "org.h2.Driver",
                                        DEFAULT_SCHEMA to "dbo",
                                        DIALECT to H2Dialect::class.java,
                                        HBM2DDL_AUTO to Action.CREATE,
                                        SHOW_SQL to true,
                                        "hibernate.hbm2ddl.import_files" to "h2.testData.sql"))

        return entityManagerFactory
    }
}

@Module
class FakeEventBusProviderModule {

    @Provides
    @Singleton
    fun eventBus(): EventBus {
        return EventBus()
    }

}

@Module
class FakeJMSProviderModule {

    @Provides
    fun session(): Session {
        return FakeSession()
    }
}