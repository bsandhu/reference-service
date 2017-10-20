package com.baljeet.ref.service

import com.baljeet.ref.service.dto.DealStatusDto
import com.baljeet.ref.service.dto.MonetaryDto
import com.baljeet.ref.service.dto.SeniorityDto
import com.baljeet.ref.service.model.*
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


class JSONTest {

    @Test
    fun convertToDto() {
        val deal = LoanDeal()
        deal.active = true
        deal.description = "test deal"
        deal.amount = BigDecimal("1000.56")
        deal.cancelDate = LocalDate.of(2018, 1, 1)
        deal.agentMEI = ""
        deal.externalId = 50001
        deal.isin = "IS500"

        val seniority = Seniority()
        seniority.seniorityId = 1
        seniority.description = "A"
        seniority.ranking = 1

        deal.seniority = seniority

        val dto = deal.toDto({ _: DealStatus? -> DealStatusDto(id = 100, desc = "Test") })

        assertNull(dto.agentSDSId)
        assertEquals(dto.agentMEI, "")
        assertTrue(dto.active ?: false)

        // Note: Kotlin is smart here and recognizes that null is not a problem here
        assertEquals(dto.externalId, 50001)
        assertEquals(dto.CancelledDate, "My 2018-01-01")
        assertEquals(dto.dealStatus, DealStatusDto(id = 100, desc = "Test"))
        assertEquals(dto.seniority, SeniorityDto(id = 1, desc = "A"))
        assertEquals(dto.amount, BigDecimal("1000.56"))
        assertEquals(dto.isin, "IS500")

        println(dto)
    }

    @Test
    fun convertToModel() {
        val dto = LoanDealDto(
                agentSDSId = null,
                amount = BigDecimal("100.25"),
                active = false,
                dealType = LoanDealType.SYNDICATED.name,
                seniority = SeniorityDto(id = 1, desc = "Test"))

        val deal = dto.toModel { _ -> null }
        assertNull(deal.agentSDSId)
        assertNull(deal.dealStatus)
        assertEquals(deal.amount, BigDecimal("100.25"))
        assertFalse(deal.active ?: true)
        assertEquals(deal.dealType, LoanDealType.SYNDICATED)
        assertEquals(deal.seniority?.seniorityId, 1)
        println(deal)
    }
}