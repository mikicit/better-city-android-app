package dev.mikita.bettercity.main.data

import com.google.android.gms.maps.model.LatLng
import java.util.Date

data class IssueCardData(
    val id: Long,
    val photo: String,
    val title: String,
    val description: String,
    val creationDate: Date,
    val coordinates: LatLng,
    val authorId: String,
    val categoryId: Long,
    val status: String
)
