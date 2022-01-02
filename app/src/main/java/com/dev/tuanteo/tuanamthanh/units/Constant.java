package com.dev.tuanteo.tuanamthanh.units;

public class Constant {
    /*TuanTeo: Key để chuyển dữ liệu từ MainActivity vs Service */
    public static final String SONG_PATH_START_SERVICE = "song path to start service";
    public static final String SONG_ID_TO_START_SERVICE = "song id to start service";
    public static final String SONG_IMAGE_START_SERVICE = "song image start service";

    /*TuanTeo: Key chuyển dữ liệu từ Service vs MainActivity */
    public static final String SINGER_NAME_TO_UPDATE_UI = "singer name to update";
    public static final String SONG_NAME_TO_UPDATE_UI = "song name to update";
    public static final String SONG_IMAGE_TO_UPDATE_UI = "song image to update";
    public static final String IS_ONLINE_LIST = "is online list";


    /*TuanTeo: Action filter cho broadcast update Main UI */
    public static final String ACTION_UPDATE_UI = "action update ui";
    public static final String NOTIFICATION_ACTION = "notification action";
    public static final String NOTIFICATION_PREVIOUS_ACTION = "notification previous action";
    public static final String NOTIFICATION_NEXT_ACTION = "notification next action";
    public static final String NOTIFICATION_PLAY_PAUSE_ACTION = "notification play pause action";
    public static final String NOTIFICATION_ACTION_NAME = "notification action name";
}
