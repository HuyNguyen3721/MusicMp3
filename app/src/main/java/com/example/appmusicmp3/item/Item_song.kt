package com.example.appmusicmp3.item

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Item_song(val uriImage: String?, val songName: String?, val singer: String?, val link_music: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uriImage)
        parcel.writeString(songName)
        parcel.writeString(singer)
        parcel.writeString(link_music)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item_song> {
        override fun createFromParcel(parcel: Parcel): Item_song {
            return Item_song(parcel)
        }

        override fun newArray(size: Int): Array<Item_song?> {
            return arrayOfNulls(size)
        }
    }
}
