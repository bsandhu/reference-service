package com.baljeet.ref.service

import com.baljeet.ref.service.BarcapPayload
import com.baljeet.ref.service.Payload
import com.baljeet.ref.service.run
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class WorkflowTest {

    @Test
    fun invokeSvcForProcessed() {
        var invokedSvc = false
        val savedToDB = false
        val data = Payload(foo = 10, bar = "bar")
        run(data,
                { it },
                { d: Payload -> BarcapPayload(bfoo = d.foo, bbar = "Process") },
                { it },
                { it.apply { println("InvokeSvc"); invokedSvc = true } },
                { it },
                { it })

        assertFalse(savedToDB)
        assertTrue(invokedSvc)
        assertFalse(data.isTerminated)
    }

    @Test
    fun translationError() {
        var invokedSvc = false
        val savedToDB = false
        val data = Payload(foo = 10, bar = "bar")
        val result= run(data,
                { it },
                { _: Payload -> throw Exception("Translation error") },
                { it },
                { it.apply { println("InvokeSvc"); invokedSvc = true } },
                { it },
                { it })

        assertFalse(savedToDB)
        assertFalse(invokedSvc)
        assertTrue(result.isTerminated)
    }

    @Test
    fun filter() {
        var invokedSvc = false
        val savedToDB = false
        val data = Payload(foo = 10, bar = "bar")
        val result= run(data,
                { it },
                { BarcapPayload(bfoo = it.foo, bbar = "INACTIVE") },
                { it },
                { it.apply { println("InvokeSvc"); invokedSvc = true } },
                { it },
                { it })

        assertFalse(savedToDB)
        assertFalse(invokedSvc)
        assertTrue(result.isTerminated)
    }
}