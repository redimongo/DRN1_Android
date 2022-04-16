package com.drn1.drn1_player.services

class Constants {
    interface ACTION {
        companion object {
            const val MAIN_ACTION = "com.marothiatechs.customnotification.action.main"
            const val INIT_ACTION = "com.marothiatechs.customnotification.action.init"
            const val PREV_ACTION = "com.marothiatechs.customnotification.action.prev"
            const val PLAY_ACTION = "com.marothiatechs.customnotification.action.play"
            const val NEXT_ACTION = "com.marothiatechs.customnotification.action.next"
            const val STARTFOREGROUND_ACTION =
                "com.marothiatechs.customnotification.action.startforeground"
            const val STOPFOREGROUND_ACTION =
                "com.marothiatechs.customnotification.action.stopforeground"
        }
    }

    interface NOTIFICATION_ID {
        companion object {
            const val FOREGROUND_SERVICE = 101
        }
    }
}