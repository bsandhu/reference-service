package com.baljeet.ref.service.service

import com.baljeet.ref.service.DealChangeEvent
import com.baljeet.ref.service.model.DealMapper
import com.baljeet.ref.service.model.LoanDeal
import com.baljeet.ref.service.model.LoanDealDto
import com.baljeet.ref.service.repository.DealDao
import com.google.common.eventbus.EventBus
import javax.inject.Inject


class DealService @Inject constructor(
        var eventBus: EventBus,
        var dealDao: DealDao,
        var dtoMapper: DealMapper) {

    fun getDeal(id: Int) : LoanDealDto {
        val deal = dealDao.get(id)
        return dtoMapper.modelToDto(deal)
    }

    fun saveDeal(deal: LoanDeal): Boolean {
        dealDao.save(deal)

        eventBus.post(DealChangeEvent(deal))
        return true
    }
}

