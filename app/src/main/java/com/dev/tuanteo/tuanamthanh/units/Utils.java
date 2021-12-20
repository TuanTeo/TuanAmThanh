package com.dev.tuanteo.tuanamthanh.units;

import android.media.MediaMetadataRetriever;

public class Utils {

    /**
     * Hàm lấy thời lượng bài hát qua url
     * @param url
     * @return
     */
    public static int getSongDuration(String url) {
        if (url != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(url);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInmillisec = Long.parseLong(time);
            return (int) timeInmillisec;
        } else {
            return 0;
        }
    }
}
