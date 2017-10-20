package com.baljeet.ref.service.events

import com.baljeet.ref.service.*
import com.baljeet.ref.service.model.DealMapper
import com.baljeet.ref.service.model.LoanDealDto
import com.baljeet.ref.service.repository.DealDao
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.Subscribe
import javax.inject.Inject
import javax.jms.*


class DealEventListener @Inject constructor(
        val mapper: DealMapper,
        val dao: DealDao,
        val session: Session,
        val config: EnvConfig) {


    class EvDataModel(val srcEv: DealChangeEvent) : FlowData()
    class EvDataDto(var dto: LoanDealDto) : FlowData()

    var reloadFromDB = fun(data: EvDataModel): EvDataModel {
        return data.apply { dao.get(this.srcEv.deal.id!!) }
    }

    var publish = fun(data: EvDataDto): EvDataDto {
        println("Publish deal ${data.dto}")
        val topic = session.createTopic(config.get(Topics.DEAL_TOPIC))
        val msg = session.createTextMessage(ObjectMapper().writeValueAsString(data))
        val producer = session.createProducer(topic)
        producer.send(topic, msg)
        return data
    }

    var toDTO = fun(ev: EvDataModel): EvDataDto {
        return EvDataDto(mapper.modelToDto(ev.srcEv.deal))
    }

    @Subscribe
    fun notifyUI(ev: DealChangeEvent): EvDataDto {
        return (EvDataModel(ev)
                translate toDTO
                pipeTo { println("JSON to publish $it"); it }
                asyncPipeTo {
                    when {
                        it.dto.active ?: false -> publish(it)
                        else -> it
                    }
                })
    }

    @Subscribe
    fun notifyWorkflowSvc(e: DealChangeEvent) {
        println("DealEventListener")
/*        (FlowData()
                andThen getDeal
                andThen translate
                andThen { log(it) }
                andThen {
                    val status = it["tradeStatus"] as String
                    when {
                        isApproved(status) -> saveToDB(it) andThen publish
                        status in "Process" -> invokeSvc(it)
                        else -> it
            }
        }
        andThen publish)*/
    }
}