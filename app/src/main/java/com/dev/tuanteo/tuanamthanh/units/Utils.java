package com.dev.tuanteo.tuanamthanh.units;

import android.media.MediaMetadataRetriever;

import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;

public class Utils {

    /**
     * Hàm lấy thời lượng bài hát qua url
     * @param url
     * @return
     */
    public static int getSongDuration(String url) {
        int count = 0;
        if (url != null) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(url);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInmillisec = Long.parseLong(time);
                return (int) timeInmillisec;
            } catch (RuntimeException e) {
                count++;
                if (count < 4) {
                    return getSongDuration(url);
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }

    /**
     * Cập nhật thời lượng bài hát cho danh sách nhac online
     * @param listPlaySong
     */
    public static void updateDurationForListSong(ArrayList<Song> listPlaySong) {
        new Thread(() -> {
            for (int i =0; i < listPlaySong.size(); i++) {
                listPlaySong.get(i).setDuration(Utils.getSongDuration(listPlaySong.get(i).getPath()));
            }
        }).start();
    }
}
