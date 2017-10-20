package com.baljeet.ref.service.service

import com.baljeet.ref.service.DealChangeEvent
import com.baljeet.ref.service.FakeService
import com.baljeet.ref.service.events.DealEventListener
import com.baljeet.ref.service.model.LoanDeal
import org.junit.Before
import org.junit.Test
import javax.jms.Session
import kotlin.test.assertEquals

class DealListenerTest {

    var listener: DealEventListener? = null
    var session: Session? = null

    @Before
    fun setup() {
        // Use a test context and only instantiate what is needed what
        val fakes = FakeService.instance
        listener = fakes.dealListener()
        session = fakes.jmsSession()
    }

    @Test
    fun shouldNotifyUI() {
        val deal = LoanDeal().apply { id = 10; isin = "AX-100" }
        val dealDto = listener?.notifyUI(DealChangeEvent(deal))
        assertEquals(dealDto!!.dto.isin, "AX-100")
    }

}