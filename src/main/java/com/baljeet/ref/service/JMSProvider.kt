package com.baljeet.ref.service

import com.solacesystems.jms.SolJmsUtility
import com.solacesystems.jms.SupportedProperty
import com.solacesystems.jms.SupportedProperty.*
import dagger.Module
import dagger.Provides
import java.security.Security
import java.util.*
import javax.jms.*
import javax.security.auth.login.LoginContext

interface MessageCreator {

    fun getTextMsg(): TextMessage
    fun getMapMsg(): MapMessage
    fun getObjectMsg(): ObjectMessage

}


@Module
class JMSProviderModule {

    val BASE_ENV = System.getProperty("BASE_ENV") ?: "DEV"

    @Provides
    fun jmsConnection(): Connection {
        val props = this.javaClass.classLoader.getResourceAsStream("properties/endpoint.properties")
        val endpointProps = Properties().apply { load(props) }

        // Pass on config file to JAAS
        // See: http://docs.oracle.com/javase/7/docs/technotes/guides/security/jgss/tutorials/LoginConfigFile.html
        val key = "login.config.url.1"
        val loginConfigUrl = "file:C:/src/creditit_syndicate/BaSE/syndicate-commons/src/main/resources/META-INF/jaas.config"
        Security.setProperty(key, loginConfigUrl)
        val lc = LoginContext("SolaceGSS")
        lc.login()

        val connectionFactory = SolJmsUtility.createConnectionFactory()
        connectionFactory.host = "${endpointProps["${BASE_ENV}.solace_host"]}"
        connectionFactory.port = "${endpointProps["${BASE_ENV}.solace_port"]}".toInt()
        connectionFactory.vpn = "${endpointProps["${BASE_ENV}.solace_vpn"]}"
        connectionFactory.username = "${endpointProps["${BASE_ENV}.solace_vpn_username"]}"
        connectionFactory.directTransport = true
        connectionFactory.dynamicDurables = false
        connectionFactory.authenticationScheme = "${endpointProps["${BASE_ENV}.solace_authenticationScheme"]}"
        connectionFactory.krbServiceName = "HOST"
        return connectionFactory.createConnection()
    }

    /**
     * A Session creates a single, client connection to a Solace message router for sending and receiving messages
     */
    @Provides
    fun jmsSession(conn: Connection): Session {
        // Transacted, Acknowledged
        return conn.createSession(true, SOL_CLIENT_ACKNOWLEDGE)
    }
}