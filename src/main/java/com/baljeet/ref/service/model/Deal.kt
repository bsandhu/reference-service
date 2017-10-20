package com.baljeet.ref.service.model

import com.baljeet.ref.service.dto.*
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.persistence.*


@Entity
@Table(schema = "IBSD", name = "Deal")
class LoanDeal : Serializable {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "Description", nullable = false, length = 512)
    var description: String? = null

    @OneToOne
    @JoinColumn(name = "DealStatusId", referencedColumnName = "Id", nullable = false)
    @Audited(targetAuditMode = NOT_AUDITED)
    var dealStatus: DealStatus? = null

    // @SkipAudit
    // @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER, mappedBy = "deal")
    // @JsonManagedReference(value = "dealInstrument")
    // var instruments: MutableSet<LoanInstrument> = HashSet<LoanInstrument>(),

    @Transient
    var dealType: LoanDealType? = null

    @Column(name = "StreetName", length = 255)
    var streetName: String? = null

    @Column(name = "AgentSDSId", nullable = false)
    var agentSDSId: Int? = null

    @Column(name = "AgentMEI", length = 10)
    var agentMEI: String? = null

    @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER, mappedBy = "dealId")
    var issuers: Set<Issuer>? = null

    // @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER, mappedBy = "dealId")
    // @JsonManagedReference(value = "dealGuarantors")
    // var guarantors: Set<Guarantor>? = null

    var amount: BigDecimal? = null

    @Column(name = "ExpiryDate")
    var expiryDate: LocalDate? = null

    @Column(name = "CancelDate")
    var cancelDate: LocalDate? = null

    @Column(name = "GoverningLaw", nullable = true, length = 255)
    var governingLaw: String? = null

    @Column(name = "CUSIP", nullable = true, length = 9)
    var cusip: String? = null

    @Column(name = "ISIN", nullable = true, length = 12)
    var isin: String? = null

    @Column(name = "ClearparSyndicationId", nullable = true, length = 20)
    var clearparSyndicationId: String? = null

    @Column(name = "IsOriginalLender", nullable = false)
    var isOriginalLender: Boolean = false

    @Column(name = "IsClearparEligibleUS", nullable = false)
    var isClearParEligibleUS: Boolean = false

    @Column(name = "IsClearParEligibleUK", nullable = false)
    var isClearParEligibleUK: Boolean = false

    @Column(name = "IsEnrichmentComplete")
    var isEnrichmentComplete: Boolean? = null

    @Column(name = "Active", nullable = false)
    var active: Boolean = true

    @Column(name = "CreditAgreementDate", nullable = true, length = 300)
    var creditAgreementDate: String? = null

    @Column(name = "CreditAgreementAmount", nullable = true, length = 300)
    var creditAgreementAmount: String? = null

    @Column(name = "ExternalId", nullable = true)
    var externalId: Int? = null

    @Column(name = "ExternalSource", nullable = true, length = 50)
    var externalSource: String? = null

    @OneToOne
    @JoinColumn(name = "SeniorityId", referencedColumnName = "Id", nullable = false)
    @Audited(targetAuditMode = NOT_AUDITED)
    var seniority: Seniority? = null
}

// Data classes generate getter/setter/hash code/equals
data class LoanDealDto(
        var dealStatus: DealStatusDto? = null,
        var active: Boolean? = null,
        var dealType: String? = null,
        var agentMEI: String? = null,
        var agentSDSId: Int? = null,
        var CancelledDate: String? = null,
        var seniority: SeniorityDto? = null,
        var amount: BigDecimal? = null,
        var externalId: Int? = null,
        var isin: String? = null
)

/**
 * Small wrapper around functional signatures to allow injection of heavier dependencies
 */
class DealMapper @Inject constructor(var statusMapper: DealStatusMapper) {
    fun modelToDto(deal: LoanDeal) = deal.toDto(statusMapper::toDto)
    fun dtoToModel(dto: LoanDealDto) = dto.toModel(statusMapper::toModel)
}

/**
 * Mapper functions with functional signatures to facilitate testing
 */
fun LoanDeal.toDto(statusMapper: (DealStatus?) -> DealStatusDto?): LoanDealDto {
    val dto = LoanDealDto()

    modelToDto(this, dto,
            mapping = mapOf(
                    LoanDealDto::seniority.name to { l: LoanDeal -> l.seniority?.toCustomDto() },
                    LoanDealDto::CancelledDate.name to { l: LoanDeal -> unlessNull(l.cancelDate) { "My ${l.cancelDate}" } },
                    LoanDealDto::dealStatus.name to { l: LoanDeal -> statusMapper(l.dealStatus) }))
    return dto
}

fun LoanDealDto.toModel(statusMapper: (DealStatusDto?) -> DealStatus?): LoanDeal {
    val model = LoanDeal()

    dtoToModel(this, model,
            mapping = mapOf(
                    LoanDeal::seniority.name to { l: LoanDealDto -> l.seniority?.toModel() },
                    LoanDeal::dealType.name to { l: LoanDealDto -> unlessNull(l.dealType) { LoanDealType.valueOf(l.dealType!!) } },
                    //LoanDeal::cancelDate.name to { l: LoanDealDto -> if (l.cancelDate != null) "My ${l.cancelDate}" else null },
                    LoanDeal::dealStatus.name to { l: LoanDealDto -> statusMapper(l.dealStatus) })
    )
    return model
}
