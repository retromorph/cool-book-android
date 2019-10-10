package ru.izifak.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioBook(
    @PrimaryKey var name: String, @ColumnInfo(name = "time") var time: Long, @ColumnInfo(name = "image") var image: String,
    @ColumnInfo(name = "source") var source: String, @ColumnInfo(name = "order") var order: Long
)