package com.example.joymp3.data.model
import android.os.Parcel
import android.os.Parcelable

data class SongModel(
    val id: Long,
    val title: String?,
    val artist: String?,
    val duration: Long,
    val data: String?,
    val image: String?,
    val trackUri: String? // Добавлено новое свойство
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString() // Чтение trackUri из Parcel
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(artist)
        dest.writeLong(duration)
        dest.writeString(data)
        dest.writeString(image)
        dest.writeString(trackUri) // Запись trackUri в Parcel
    }

    companion object CREATOR : Parcelable.Creator<SongModel> {
        override fun createFromParcel(parcel: Parcel): SongModel {
            return SongModel(parcel)
        }

        override fun newArray(size: Int): Array<SongModel?> {
            return arrayOfNulls(size)
        }
    }
}
