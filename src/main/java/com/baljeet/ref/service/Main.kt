package com.baljeet.ref.service

import com.baljeet.ref.service.controllers.DealController
import com.baljeet.ref.service.events.DealEventListener
import com.baljeet.ref.service.service.DealService
import com.google.common.eventbus.EventBus
import dagger.Component
import dagger.Lazy
import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.util.scan.StandardJarScanner
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.AllowSymLinkAliasChecker
import org.eclipse.jetty.webapp.WebAppContext
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Singleton
import javax.jms.Connection
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory




@Component(modules = arrayOf(JPAProviderModule::class, EventBusProviderModule::class))
@Singleton
internal interface ServiceRefImpl {
    fun dealService(): DealService
    fun dealController(): DealController
    fun entityManagerFactory(): EntityManagerFactory

    fun entityManager(): Lazy<EntityManager>
}

@Component(modules = arrayOf(JMSProviderModule::class))
@Singleton
internal interface JMSService {
    fun jmsConnection() : Connection
}

object Main {

    private val LOGGER = LoggerFactory.getLogger(Main::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        // Kotlin is here!
        // https://github.com/spring-projects/spring-framework/blob/master/spring-beans/src/main/java/org/springframework/beans/BeanUtils.java
        startTomcat()
        //startEventBus()
        //startJetty()
    }

    private fun startTomcat() {
        val file = File("src/main/webapp")

        val tc = Tomcat()
        tc.setPort(8080)
        tc.setBaseDir("target/tcBase")

        val ctx = tc.addWebapp("", File("src/main/webapp").absolutePath)

        // Disable Jar scanning for TLD files, causes startup to crawl
        val scanner = StandardJarScanner()
        scanner.isScanAllDirectories = false
        scanner.isScanAllFiles = false
        scanner.isScanBootstrapClassPath = false
        scanner.isScanClassPath = false
        ctx.jarScanner = scanner

        LOGGER.info("Starting Tomcat. Deploying.. ${file.absolutePath}")
        tc.start()
        tc.server.await()
    }

    private fun startJetty() {
        val server = Server()

        // HTTP connector
        val http = ServerConnector(server)
        http.host = "localhost"
        http.port = 8080
        http.idleTimeout = 30000

        server.addConnector(http)

        val webapp = WebAppContext()
        webapp.contextPath = "/"

        val deployDir = File("src/main/webapp")
        webapp.resourceBase = deployDir.absolutePath
        webapp.addAliasCheck(AllowSymLinkAliasChecker())

        server.handler = webapp
        server.start()
        server.join()
    }

}

