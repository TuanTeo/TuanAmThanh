package com.dev.tuanteo.tuanamthanh.units;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.dev.tuanteo.tuanamthanh.object.Song;

import java.util.ArrayList;

public class Utils {

    /**
     * Hàm lấy thời lượng bài hát qua url
     * @param url
     * @return
     */
    public static int getSongDuration(String url) {
        if (url != null) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(url);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInmillisec = Long.parseLong(time);
                return (int) timeInmillisec;
            } catch (RuntimeException e) {
                return 0;
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


    /*TuanTeo: Xóa bài hát offline */
    public static boolean deleteFileUsingDisplayName(Context context, String displayName) {

        Uri uri = getUriFromDisplayName(context, displayName);
        if (uri != null) {
            final ContentResolver resolver = context.getContentResolver();
            String[] selectionArgsPdf = new String[]{displayName};

            try {
                resolver.delete(uri, MediaStore.Audio.Media.DISPLAY_NAME + "=?", selectionArgsPdf);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                // show some alert message
            }
        }
        return false;

    }

    public static Uri getUriFromDisplayName(Context context, String displayName) {

        String[] projection;
        projection = new String[]{MediaStore.Files.FileColumns._ID};

        // TODO This will break if we have no matching item in the MediaStore.
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?", new String[]{displayName}, null);
        assert cursor != null;
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            long fileId = cursor.getLong(columnIndex);

            cursor.close();
            return Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/" + fileId);
        } else {
            return null;
        }
    }

    public static String getNameFileByPath(String path) {
        String[] splits = path.split("/");
        return splits[splits.length -1];
    }
}
