package ru.izifak.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioBook(@PrimaryKey var name: String, var time: Long, var image: String, var source: String)