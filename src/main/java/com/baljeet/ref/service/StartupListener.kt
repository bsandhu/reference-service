package com.baljeet.ref.service

import java.lang.System.*
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener


class StartupListener : ServletContextListener {

    override fun contextDestroyed(ev: ServletContextEvent?) {
        // Shutdown hooks - graceful disconnect
        // No-op
    }

    override fun contextInitialized(ev: ServletContextEvent?) {
        val ser = DaggerServiceRefImpl.builder().build()

        // There is one context per "web application" per Java Virtual Machine.
        ev!!.servletContext.setAttribute("dagger", ser)

        // Start Lazy factories
        // Note: In Kotlin
        //    Structural  equality is checked with  ==
        //    Referential equality is checked with  ===
        if (getProperty("BASE_ENV") != "LOCAL") ser.entityManagerFactory()

    }
}