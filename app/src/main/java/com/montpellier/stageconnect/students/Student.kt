package com.montpellier.stageconnect.students

data class Student (
    var email: String = "",
    var name: String = "",
    var university: String = "",
    var id: String? = "",
    var cv: String? = "",
    var applicationsCount: Number? = 0,
    var universityId: String? = "",
)