package dev.mikita.bettercity.api.userservice

import dev.mikita.bettercity.api.issueservice.Coordinates
import java.util.Date

class Issues : ArrayList<Issue>()

data class Issue(
    val id: Long,
    val photo: String,
    val title: String,
    val description: String,
    val creationDate: Date,
    val coordinates: Coordinates,
    val authorId: String,
    val categoryId: Long,
    val status: String
)

data class Resident(
    val uid: String,
    val photo: String,
    val firstName: String,
    val lastName: String
)

data class CurrentResident(
    val uid: String,
    val photo: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

data class Service(
    val uid: String,
    val photo: String,
    val name: String,
    val description: String
)

data class ItemsCount(
    val count: Int
)