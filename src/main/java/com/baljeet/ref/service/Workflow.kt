package com.baljeet.ref.service

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString


open class FlowData(var isTerminated: Boolean = false, var error: Exception? = null)
typealias Action<T> = (T) -> T
typealias TranslateAction<T, R> = (T) -> R

infix fun <T : FlowData> T.pipeTo(action: Action<T>): T {
    return if (this.isTerminated) this else try {
        println("Pipe... ${reflectionToString(this)}")
        action(this)
    } catch (ex: Exception) {
        this.apply { isTerminated = true; error = ex }
    }
}

infix fun <T : FlowData> T.asyncPipeTo(action: Action<T>): T {
    val data = this
    async { data.pipeTo(action) }
    return data
}

inline infix fun <T : FlowData, reified R : FlowData> T.translate(action: TranslateAction<T, R>): R {
    return try {
        println("Translate... ${reflectionToString(this)}")
        action(this)
    } catch (ex: Exception) {
        val translated = R::class.java.newInstance()
        translated.apply { isTerminated = true; error = ex }
    }
}

infix fun <T : FlowData> T.filter(predicate: (T) -> Boolean): T {
    println("Filter... ${reflectionToString(this)}")
    return this.apply { isTerminated = this.isTerminated || predicate(this) }
}

fun <T : FlowData> log(data: T): T = data.apply { print(this) }


/********************************************************************/
/**** Sample workflow ****/
/********************************************************************/

class Payload(var foo: Int = 0, var bar: String = "") : FlowData()

class BarcapPayload(var bfoo: Int = 1, var bbar: String = "b") : FlowData()

var inActiveInstruments = fun(payload: BarcapPayload): Boolean {
    return payload.bbar.contains("INACTIVE")
}

fun BarcapPayload.isApproved(): Boolean {
    return this.bbar.contains("OK")
}

fun run(payload: Payload,
        getTrade: Action<Payload>,
        translate: TranslateAction<Payload, BarcapPayload>,
        saveToDB: Action<BarcapPayload>,
        invokeSvc: Action<BarcapPayload>,
        notifyUser: Action<BarcapPayload>,
        publish: Action<BarcapPayload>): BarcapPayload {
    return (payload
            pipeTo getTrade
            translate translate
            pipeTo { log(it) }
            filter inActiveInstruments
            filter { it.bbar.matches(Regex("AB[1-4][2-3]-XYZ")) }
            pipeTo {
                when {
                    it.isApproved() -> saveToDB(it) pipeTo notifyUser
                    it.bbar == "Process" -> invokeSvc(it)
                    else -> it
                }
            }
            asyncPipeTo publish)
}
