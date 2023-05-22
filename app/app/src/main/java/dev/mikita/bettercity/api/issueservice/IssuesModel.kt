package dev.mikita.bettercity.api.issueservice

import java.util.Date

class Issues : ArrayList<Issue>()
class Categories : ArrayList<Category>()

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

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

data class Likes(
    val count: Int
)

data class LikeStatus(
    val status: Boolean
)

data class IssueReservation(
    val id: Long,
    val creationDate: Date,
    val serviceId: String,
    val issueId: Long
)

data class IssueSolution(
    val id: Long,
    val photo: String,
    val description: String,
    val creationDate: Date,
    val serviceId: String,
    val issueId: Long
)

data class Category(
    val id: Long,
    val name: String
)
