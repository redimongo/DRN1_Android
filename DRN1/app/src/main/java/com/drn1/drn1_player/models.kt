package com.drn1.drn1_player

class StationsFeed(val data: List<Data>)

class Data(val _id: String, val name: String, val imageurl: String, val listenlive: String)


//FETCH PROGRAMS
class ProgramFeed(val programs: List<Program>)

class Program(val _id: String, val title: String, val icon: String, val banner: String, val url: String)


//FETCH PROGRAMS
class PodcastFeed(val programs: List<PodcastProgram>)

class PodcastProgram(val episode: List<EpisodeFeed>, var icon: String, var title: String)

class EpisodeFeed(val title: String, val summary: String, val enclosureurl: String)


//NOW PLAYING DATA
class NowPlayingJson(val data: List<DataP>)

class DataP(val track: NowPlayingTrack)

class NowPlayingTrack(val artist: String, val title: String, val imageurl: String, val type: String, val url:String)