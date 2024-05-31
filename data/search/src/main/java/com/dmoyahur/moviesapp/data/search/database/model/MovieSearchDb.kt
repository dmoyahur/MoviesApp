package com.dmoyahur.moviesapp.data.search.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MovieSearch")
data class MovieSearchDb(
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