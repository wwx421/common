package com.example.wwx.common.entity;

/**
 * Created by wwx on 2018/11/13.
 * 相册实体类
 */

public class AlbumModel {

    public static final int TYPE_ALL = 1;
    public static final int TYPE_ALBUM = 2;

    public String name;
    public int count;
    public String recent;
    public boolean isCheck;
    public int type = TYPE_ALBUM;

    public AlbumModel() {
    }

    public AlbumModel(String name) {
        this.name = name;
    }

    public AlbumModel(String name, int count, String recent) {
        this.name = name;
        this.count = count;
        this.recent = recent;
    }

    public AlbumModel(String name, int count, String recent, boolean isCheck) {
        this.name = name;
        this.count = count;
        this.recent = recent;
        this.isCheck = isCheck;
    }

    public void increaseCount() {
        count++;
    }
}
