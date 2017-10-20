package com.baljeet.ref.service


import com.baljeet.ref.service.dto.DealStatusDto
import com.baljeet.ref.service.model.DealStatus
import com.baljeet.ref.service.model.LoanDeal
import com.baljeet.ref.service.model.Seniority
import com.baljeet.ref.service.service.DealService
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


/**
 * Kotlin built in Singleton via 'object'
 */
object FakeService {
    var instance: DealServiceWithFakes = DaggerDealServiceWithFakes.create()
}

class DealServiceTest {

    var svc: DealService? = null
    var evBus: EventBus? = null

    @Before
    fun setup() {
        // Use a test context and only instantiate what is needed what
        val fakes = FakeService.instance
        evBus = fakes.eventBus()
        svc = fakes.dealService()
    }

    @Test
    fun shouldRead() {
        val dealDto = svc?.getDeal(1001)

        assertNull(dealDto?.agentMEI)
        assertEquals(dealDto?.agentSDSId, 123456)
        assertNull(dealDto?.CancelledDate)

        // Note: Data class is generating the equals()
        assertEquals(dealDto?.amount, BigDecimal("1000.00"))
        assertEquals(dealDto?.dealStatus, DealStatusDto(id = 3, desc = "In Syndication"))
    }

    @Test
    fun shouldSave() {
        val deal = LoanDeal()
        deal.agentSDSId = 1000
        deal.dealStatus = DealStatus().apply { id = 1 }
        deal.seniority = Seniority().apply { seniorityId = 1 }
        deal.description = "Test Deal 2"

        val fakeListener = FakeDealListener()
        evBus?.register(fakeListener)
        svc?.saveDeal(deal)
        assertNotNull(fakeListener.payload)
        assertTrue(fakeListener.payload?.deal is LoanDeal)
    }
}

class FakeDealListener {

    var payload: DealChangeEvent? = null

    @Subscribe
    fun handle(ev: DealChangeEvent) {
        println("FakeDealListener... $ev")
        this.payload = ev
    }
}
