package com.montpellier.stageconnect.offers

data class Offer (
    val company: String = "",
    val position: String = "",
    val city: String = "",
    val date: String = "",
    val description: String = "",
    val candidateCount: Int? = null,
    val status: String? = null,
    val id: String? = "",
    var companyId: String? = null
) {
    constructor(
        company: String?,
        position: String?,
        city: String?,
        date: String,
        description: String?,
        id: String?
    ) : this(
        company = company.toString(),
        position = position.toString(),
        city = city.toString(),
        date = date,
        description = description.toString(),
        id = id
    )
}
