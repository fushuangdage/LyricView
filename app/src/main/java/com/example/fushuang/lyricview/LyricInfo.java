package com.example.fushuang.lyricview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/6/7.
 */

public class LyricInfo {

    List<LineInfo> song_lines=new ArrayList<>();

    String song_artist;  // 歌手
    String song_title;  // 标题
    String song_album;  // 专辑

    long song_offset;  // 偏移量

    public List<LineInfo> getSong_lines() {
        return song_lines;
    }

    public void setSong_lines(List<LineInfo> song_lines) {
        this.song_lines = song_lines;
    }

    public String getSong_artist() {
        return song_artist;
    }

    public void setSong_artist(String song_artist) {
        this.song_artist = song_artist;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getSong_album() {
        return song_album;
    }

    public void setSong_album(String song_album) {
        this.song_album = song_album;
    }

    public long getSong_offset() {
        return song_offset;
    }

    public void setSong_offset(long song_offset) {
        this.song_offset = song_offset;
    }

    public void addSong_lines(LineInfo lineInfo) {
        song_lines.add(lineInfo);
    }
}
