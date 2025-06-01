package com.montpellier.stageconnect.offers

data class JobApplication(
    val company: String? = null,
    val position: String? = null,
    val city: String? = null,
    val date: String = "",
    val description: String? = null,
    val status: String = "",
    var candidate: String? = null,
    var university: String? = null,
    var candidateId: String? = null,
    var offerId: String? = null,
    var id: String? = null,
    var companyId: String? = null,
) {
    constructor(
        company: String,
        position: String,
        city: String,
        date: String,
        description: String,
        status: String,
    ) : this(
        company = company,
        position = position,
        city = city,
        date = date,
        description = description,
        status = status,
        candidate = null,
        university = null
    )
}