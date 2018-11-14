package com.example.wwx.common.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.util.SparseArray;

import com.example.wwx.common.entity.AlbumModel;
import com.example.wwx.common.entity.PhotoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wwx on 2018/11/13.
 * 获取本地图集数据库数据
 */

public class PhotoDao {

    private ContentResolver mResolver;
    private Context mContext;

    public PhotoDao(Context mContext) {
        this.mContext = mContext;
        mResolver = mContext.getContentResolver();
    }

    /**
     * _ID: 行唯一id
     * DATA: 文件的数据流，类型:数据流
     * DATE_ADDED: 文件被添加到媒体提供者单元的时间 类型
     * SIZE: 文件的大小(以字节为单位)
     */
    public List<PhotoModel> getCurrent() {
        Cursor cursor = mResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DATE_ADDED,
                        MediaStore.Images.ImageColumns.SIZE,
                        MediaStore.Images.ImageColumns.MIME_TYPE},
                null, null, MediaStore.Images.ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext()) {
            return new ArrayList<>();
        }
        List<PhotoModel> photos = new ArrayList<>();
        cursor.moveToLast();
        try {
            while (cursor.moveToPrevious()) {
                if (!isGif(cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.ImageColumns.MIME_TYPE))) &&
                        cursor.getLong(cursor.getColumnIndex(
                                MediaStore.Images.ImageColumns.SIZE)) > 1024 * 10) {
                    PhotoModel photoModel = new PhotoModel();
                    photoModel.type = PhotoModel.TYPE_PHOTO;
                    photoModel.originalPath = cursor.getString(cursor.getColumnIndex(
                            MediaStore.Images.ImageColumns.DATA));
                    photos.add(photoModel);
                }
            }
        } finally {
            cursor.close();
        }
        return photos;
    }

    /**
     * 获取所有专辑
     */
    public List<AlbumModel> getAlbums() {
        List<AlbumModel> albumModels = new ArrayList<>();
        Map<String, AlbumModel> map = new HashMap<>();
        // projection查询要返回的列
        Cursor cursor = mResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.SIZE,
                        MediaStore.Images.ImageColumns.MIME_TYPE},
                null, null, null);
        if (cursor == null || !cursor.moveToNext()) {
            return new ArrayList<>();
        }
        cursor.moveToLast();
        AlbumModel albumModel = new AlbumModel("最近照片", 0, cursor.getString(
                cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)), true);
        albumModel.type = AlbumModel.TYPE_ALL;
        albumModels.add(albumModel);
        try {
            while (cursor.moveToPrevious()) {
                if (!isGif(cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.ImageColumns.MIME_TYPE))) ||
                        cursor.getInt(cursor.getColumnIndex(
                                MediaStore.Images.ImageColumns.SIZE)) < 1024 * 10) {
                    continue;
                }
                albumModel.increaseCount();
                String name = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                // map.keySet()返回此映射中包含的一组键。
                if (map.keySet().contains(name)) {
                    map.get(name).increaseCount();
                } else {
                    AlbumModel model = new AlbumModel(name, 1, cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                    model.type = AlbumModel.TYPE_ALBUM;
                    map.put(name, model);
                    albumModels.add(model);
                }
            }
        } finally {
            cursor.close();
        }
        return albumModels;
    }

    /**
     * 获取专辑下所有图片
     */
    public List<PhotoModel> getAlbum(String name) {
        // projection查询要返回的列
        Cursor cursor = mResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{
                        MediaStore.Images.ImageColumns._ID,
                        //图片名称
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        //文件被添加到媒体提供者单元的时间
                        MediaStore.Images.ImageColumns.DATE_ADDED,
                        MediaStore.Images.ImageColumns.SIZE,
                        MediaStore.Images.ImageColumns.MIME_TYPE},
                "bucket_display_name = ?",
                new String[]{name}, MediaStore.Images.ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext()) {
            return new ArrayList<>();
        }
        List<PhotoModel> photoModels = new ArrayList<>();
        cursor.moveToLast();
        try {
            while (cursor.moveToPrevious()) {
                if (!isGif(cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.ImageColumns.MIME_TYPE)))
                        && cursor.getLong(cursor.getColumnIndex(
                        MediaStore.Images.ImageColumns.SIZE)) > 1024 * 10) {
                    PhotoModel model = new PhotoModel();
                    model.type = PhotoModel.TYPE_PHOTO;
                    model.originalPath = cursor.getString(cursor.getColumnIndex(
                            MediaStore.Images.ImageColumns.DATA));
                    photoModels.add(model);
                }
            }
        } finally {
            cursor.close();
        }
        return photoModels;
    }

    /**
     * 获取缩略图
     * SparseArray取代HashMap
     */
    public SparseArray<String> getThumbNails() {
        Cursor cursor = null;
        try {
            // Thumbnails这个类允许开发人员查询并获得两种缩略图:
            // MINI_KIND: 512 x 384缩略图 MICRO_KIND: 96 x 96缩略图
            // EXTERNAL_CONTENT_URI 外部存储
            cursor = mResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Thumbnails.DATA,
                            MediaStore.Images.Thumbnails.IMAGE_ID},
                    null, null, null);
            cursor.moveToLast();
            SparseArray<String> thumbnails = new SparseArray<>();
            //将光标移动到前一行
            while (cursor.moveToPrevious()) {
                // getColumnIndex 返回给定列名的从零开始的索引，如果列不存在，则返回-1。
                int id = cursor.getInt(cursor.getColumnIndex(
                        MediaStore.Images.Thumbnails.IMAGE_ID));
                String path = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.Thumbnails.DATA));
                thumbnails.put(id, path);
            }
            return thumbnails;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int deletePhoto(String path) {
        // uri:要删除行的url  path是条件，在where中寻找path
        return mResolver.delete(Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.DATA + "=?", new String[]{path});
    }

    public void addPhoto(String path) {
        // 当我们添加一个文件的时候，我们需要刷新媒体库才能立即找得到添加文件
        MediaScannerConnection.scanFile(mContext, new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private boolean isGif(String mineType) {
        return "image/gif".equals(mineType);
    }
}
