package com.baljeet.ref.service.model

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*


enum class CounterpartyTypeEnum(val symbol: String) {
    LEGALENTITY("L"),
    SUBACCOUNT("S"),
    BRANCH("B"),
    UNKNOWN("");
}

@Entity
@Table(name = "CounterpartyMEI", schema = "DBO")
class CounterpartyMEI : Serializable {
    @Id
    @Column(name = "CounterpartyId")
    var countyerpartyId: Int? = null

    @Column(name = "MEICode", length = 20)
    var meiCode: String? = null

    @Column(name = "UpdateDate")
    var updateDate: LocalDate? = null
}

@Converter
class CounterpartyTypeEnumConverter : AttributeConverter<CounterpartyTypeEnum, String> {

    @Throws(IllegalArgumentException::class)
    override fun convertToDatabaseColumn(attribute: CounterpartyTypeEnum?): String {
        if (null == attribute)
            throw IllegalArgumentException("Attribute parameter cannot be null.")
        else
            return attribute.symbol
    }

    @Throws(IllegalArgumentException::class)
    override fun convertToEntityAttribute(value: String?): CounterpartyTypeEnum {
        if (null == value)
            throw IllegalArgumentException("Value parameter cannot be null.")
        else
            return CounterpartyTypeEnum.values().filter {it.symbol.equals(value)}[0]
    }
}

@Entity
@Table(schema = "DBO")
class Counterparty : Serializable {
    @Id
    @Column(name = "Id")
    var id: Int? = null

    @Column(name = "Name", nullable = false, length = 255)
    var name: String? = null

    @Column(name = "Type", nullable = false)
    @Convert(converter = CounterpartyTypeEnumConverter::class)
    var counterpartyType: CounterpartyTypeEnum? = null

    @Column(name = "UltimateParentId", nullable = true)
    var ultimateParentId: Int? = null

    @Column(name = "CounterpartyGroupId", nullable = true)
    var counterpartyGroupId: Int? = null

    @Column(name = "AOTApproved", nullable = false)
    var aotApproved: Boolean? = null

    @Column(name = "Active", nullable = false)
    var active: Boolean? = null

    @Column(name = "ShortName", length = 20)
    var shortName: String? = null

    @Column(name = "SICCode", nullable = true)
    var sicCode: Int? = null

    @Column(name = "BICCode", nullable = true)
    var bicCode: Int? = null

    @Column(name = "LocationOfOperationId", nullable = true)
    var locationOfOperationId: Int? = null

    @Column(name = "LocationOfIncorporationId", nullable = true)
    var locationOfIncorporationId: Int? = null

    @Column(name = "CountryOfOperationCode", length = 3)
    var countryOfOperationCode: String? = null

    @Column(name = "CountryOfIncorporationCode", length = 3)
    var countryOfIncorporationCode: String? = null

    @Column(name = "IsBank", nullable = false)
    var isBank: Boolean? = null

    @Column(name = "IsMonet", nullable = false)
    var isMonet: Boolean? = null

    @Column(name = "IsAtlas", nullable = false)
    var isAtlas: Boolean? = null

    @Column(name = "IsRegW")
    var isRegW: Boolean? = null

    @Column(name = "IsProcessAgentRequired", nullable = false)
    var isProcessAgentRequired: Boolean? = null

    @Column(name = "IsClearParEligibleUS", nullable = false)
    var isClearParEligibleUS: Boolean? = null

    @Column(name = "IsClearParEligibleUK", nullable = false)
    var isClearParEligibleUK: Boolean? = null

    @OneToOne
    @JoinColumn(name = "Id", referencedColumnName = "counterpartyId")
    var counterpartyMEI: CounterpartyMEI? = null

    @Transient
    var meiId: String? = null
}
