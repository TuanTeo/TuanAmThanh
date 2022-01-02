package com.dev.tuanteo.tuanamthanh.object;

public class Song implements Comparable<Song>{
    private String id;
    private String name;
    private String album;
    private String artist; //singer
    private String path;
    private int mDuration;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    @Override
    public int compareTo(Song song) {
        if (song == null || song.getName() == null) {
            return 0;
        }
        return this.getName().compareTo(song.getName());
    }
}
