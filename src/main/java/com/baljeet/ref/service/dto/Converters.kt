package com.baljeet.ref.service.dto

import org.javamoney.moneta.Money
import java.io.Serializable
import java.math.BigDecimal
import javax.money.Monetary
import javax.money.MonetaryAmount
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties


fun unlessNull(obj: Any?, fn: () -> Serializable?) : Serializable? {
    return if (obj != null) fn() else null
}

fun toDto(model: Any?): Serializable? {
    return when (model) {
        is MonetaryAmount -> MonetaryDto(amount = model.number?.toDouble() ?: 0.0, currency = model.currency?.currencyCode)
        is Boolean -> model
        is Number -> model
        is String -> model
        else -> model?.toString()
    }
}

fun toModel(dto: Any?): Serializable? {
    return when (dto) {
        is MonetaryDto -> {
            return unlessNull(dto.currency) {
                Money.of(
                        BigDecimal(dto.amount.toString()),
                        Monetary.getCurrency(dto.currency)) as Serializable
            }
        }
        is Boolean -> dto
        is Number -> dto
        else -> dto?.toString()
    }
}

fun <T : Any> modelToDto(model: T,
                         dto: Any,
                         mapping: Map<String, (T) -> Serializable?> = mapOf(),
                         ignoreCase: Boolean = false) {
    copy(model, dto,
            mapping = mapping,
            ignoreCase = ignoreCase,
            defaultPropConversionFn = { o: Any? -> toDto(o) })
}

fun <T : Any> dtoToModel(dto: T,
                         model: Any,
                         mapping: Map<String, (T) -> Serializable?> = mapOf(),
                         ignoreCase: Boolean = false) {
    copy(dto, model,
            mapping = mapping,
            ignoreCase = ignoreCase,
            defaultPropConversionFn = { o: Any? -> toModel(o) })
}

/**
 * Copy all Props with the same name (case sensitive)
 * @param src object to copy from
 * @param dest object to copy to
 * @param defaultPropConversionFn Function to use to convert src prop -> dest prop
 * @param mapping Override default conversion fn if custom mapping supplied
 * @param ignoreCase while matching prop names between src and dest
 */
fun <T : Any> copy(src: T,
                   dest: Any,
                   defaultPropConversionFn: (a: Any?) -> Serializable?,
                   mapping: Map<String, (T) -> Serializable?> = mapOf(),
                   ignoreCase: Boolean = false) {

    val destProps = dest.javaClass.kotlin.memberProperties
    val srcProps = (src as Any).javaClass.kotlin.memberProperties

    for (destProp in destProps) {
        // Use mapping fn if available
        val destMappingFn = mapping[destProp.name]
        var mappedValue: Serializable? = null

        if (destMappingFn != null) {
            mappedValue = destMappingFn(src)
        } else {
            // Map from src prop with same name
            val srcProp = srcProps.find { it.name == destProp.name }
            if (srcProp != null) {
                val srcPropValue = srcProp.getter.call(src)
                mappedValue = defaultPropConversionFn(srcPropValue)
            }
        }
        setSafely(destProp as KMutableProperty<*>, dest, mappedValue)
    }
}

fun setSafely(prop: KMutableProperty<*>, dest: Any, value: Serializable?) {
    try {
        if (value != null) {
            prop.setter.call(dest, value)
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        throw Exception("""
                    Error while invoking setter on Object ${dest}
                    Prop: ${prop.name}
                    Prop type: ${prop.returnType}
                    New value: ${value}
                    Msg: ${e.message}""")
    }
}
