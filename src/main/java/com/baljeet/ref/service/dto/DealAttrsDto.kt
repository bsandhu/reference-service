package com.baljeet.ref.service.dto

import java.io.Serializable


data class DealStatusDto(
        var id: Int?,
        var desc: String = ""
) : Serializable

data class MonetaryDto(
        var currency: String?,
        var amount: Double = 0.0
) : Serializable

data class SeniorityDto(
        var id: Int? = null,
        var desc: String? = ""
) : Serializable
