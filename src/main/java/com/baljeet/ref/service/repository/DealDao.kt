package com.baljeet.ref.service.repository

import com.baljeet.ref.service.model.DealStatus
import com.baljeet.ref.service.model.LoanDeal
import dagger.Lazy
import javax.inject.Inject
import javax.persistence.EntityManager


class DealDao @Inject constructor(var eManager: Lazy<EntityManager>) {

    fun get(id: Int): LoanDeal {
        return eManager.get().find(LoanDeal::class.java, id)
    }

    fun save(deal: LoanDeal) {
        return eManager.get().persist(deal)
    }

}

class DealStatusDao @Inject constructor(var eManager: Lazy<EntityManager>) {

    fun get(id: Int): DealStatus {
        return eManager.get().find(DealStatus::class.java, id)
    }

}