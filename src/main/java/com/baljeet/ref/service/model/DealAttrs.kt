package com.baljeet.ref.service.model

import com.baljeet.ref.service.dto.DealStatusDto
import com.baljeet.ref.service.dto.SeniorityDto
import com.baljeet.ref.service.repository.DealStatusDao
import org.hibernate.envers.Audited
import javax.persistence.Table
import javax.persistence.*
import java.io.Serializable
import org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED
import javax.inject.Inject


enum class LoanDealType {
    BILATERAL,
    CLUB,
    SYNDICATED,
    OTHER
}

@Entity
@Table(schema = "DBO", name = "DealStatus")
class DealStatus : Serializable {
        @Id
        @Column(name = "Id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null

        @Column(name = "Description", nullable = false)
        var description: String = ""

        @Column(name = "Position", nullable = false)
        val rank: Int? = null
}

class DealStatusMapper @Inject constructor(var dao: DealStatusDao) {
    fun toDto(status: DealStatus?): DealStatusDto? {
        val id = status?.id
        if (id != null) {
            val fromDB = dao.get(id)
            return DealStatusDto(fromDB.id, fromDB.description)
        }
        return null
    }

    fun toModel(dto: DealStatusDto?): DealStatus? {
        return DealStatus().apply {
            id = dto?.id
            description = dto?.desc ?: ""
        }
    }
}

@Entity
@Audited
@Table(name = "DealIssuerMap", schema = "IBSD")
class Issuer : Serializable {
        @Id
        @Column(name = "Id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null

        @ManyToOne
        @JoinColumn(name = "DealId", nullable = false)
        var dealId: LoanDeal? = null

        @OneToOne
        @JoinColumn(name = "CounterpartyId", referencedColumnName = "Id", nullable = false)
        @Audited(targetAuditMode = NOT_AUDITED)
        var counterparty: Counterparty? = null

        @Column(name = "IsPrimary")
        var isPrimary: Boolean? = null
}

@Entity
@Audited
@Table(name = "Seniority", schema = "dbo")
class Seniority : Serializable {
        @Id
        @Column(name = "Id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var seniorityId: Int? = null

        @Column(name = "Description", nullable = false, length = 255)
        var description: String = ""

        @Column(name = "Ranking", nullable = false)
        var ranking: Int? = null

}

// Note:
//    Single expression function
//    Extension functions
// When a function returns a single expression, the curly braces can be omitted
fun Seniority.toCustomDto() = SeniorityDto(id = this.seniorityId, desc = this.description)

fun SeniorityDto.toModel(): Seniority {
    val s = Seniority()
    s.seniorityId = this.id
    s.description = this.desc ?: ""
    return s
}

// Note:
//    Use 'with' to run against the given receiver
/*
fun SeniorityDto.toModel() : Seniority {
    val model = Seniority()
    with(model){
        seniorityId = id
        description = desc ?: ""
    }
    return model
}*/
