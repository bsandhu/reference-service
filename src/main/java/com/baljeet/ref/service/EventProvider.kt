package com.baljeet.ref.service

import com.baljeet.ref.service.events.DealEventListener
import com.baljeet.ref.service.model.LoanDeal
import com.google.common.eventbus.EventBus
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton


class DealChangeEvent(val deal: LoanDeal)

@Module
class EventBusProviderModule @Inject constructor(val dealEvListener: DealEventListener) {

    @Provides
    @Singleton
    fun eventBus(): EventBus {
        val eventBus = EventBus()
        eventBus.register(dealEvListener)
        return eventBus
    }
}