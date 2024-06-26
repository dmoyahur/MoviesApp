package com.dmoyahur.moviesapp.data.local.search.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MovieSearch")
data class MovieSearchDbo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val overview: String,
    val popularity: Double,
    val releaseDate: String,
    val poster: String?,
    val backdrop: String?,
    val originalTitle: String,
    val originalLanguage: String,
    val voteAverage: Double,
    val timeStamp: Long
)