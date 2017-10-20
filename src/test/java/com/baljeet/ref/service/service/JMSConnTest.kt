package com.baljeet.ref.service.service

import com.baljeet.ref.service.DaggerJMSService
import org.junit.Before
import org.junit.Test
import javax.jms.Connection
import kotlin.test.assertNotNull


class JMSConnTest {

    var conn: Connection? = null

    @Before
    fun setup() {
        conn = DaggerJMSService.create().jmsConnection()
    }

    @Test
    fun shouldConnectOverKerberos() {
        assertNotNull(conn)
    }
}
