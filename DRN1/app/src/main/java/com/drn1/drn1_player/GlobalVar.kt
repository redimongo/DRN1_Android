package com.drn1.drn1_player


object DataHolder {
    private var Score = "DRN1"

    private var UUID = "null"

    var current = "null";

    private var MediaPlayerImage = "https://storage.googleapis.com/ad-system/bannerads/qrcode-4.jpeg"

    private var Media_Type = "radio"
    fun set_Media_Type(s: String) {
        Media_Type = s
    }

    fun get_Media_Type(): String {
        return Media_Type
    }

    private var Artist = "Loading"
    fun set_Artist(s: String) {
        Artist = s
    }

    fun get_Artist(): String {
        return Artist
    }

    private var Adstichr = "Loading"
    fun set_AdStichrURL(s: String) {
        Adstichr = s
    }

    fun get_AdStichrURL(): String {
        return Adstichr
    }

    private var AdstichrType = "S"
    fun set_AdstichrType(s: String) {
        AdstichrType = s
    }

    fun get_AdstichrType(): String {
        return AdstichrType
    }

    private var Song = "Loading"
    fun set_Song(s: String) {
        Song = s
    }

    fun get_Song(): String {
        return Song
    }




    fun set_MediaPlayerImage(s: String) {
        MediaPlayerImage = s
    }

    fun get_MediaPlayerImage(): String {
        return MediaPlayerImage
    }

    fun set_Uuid(s: String) {
        UUID = s
    }

    fun get_Uuid(): String {
        return UUID
    }



    fun set_Score(s: String) {
        Score = s
    }

    fun get_Score(): String {
        return Score
    }

    private var MDIA = "https://api.drn1.com.au:9000/station/DRN1"
    fun set_Media(s: String) {
        MDIA = s
    }

    fun get_Media(): String {
        return MDIA
    }


}
