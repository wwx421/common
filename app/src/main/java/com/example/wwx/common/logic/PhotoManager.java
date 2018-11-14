package com.example.wwx.common.logic;

import android.content.Context;
import android.os.Handler;

import com.example.wwx.common.dao.PhotoDao;
import com.example.wwx.common.entity.AlbumModel;
import com.example.wwx.common.entity.PhotoModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wwx on 2018/11/14.
 * 用来处理图片选择
 */

public class PhotoManager {

    private static final Object lock = new Object();
    private PhotoDao mPhotoDao;
    private Handler mMainHandler;
    private Context mContext;

    public PhotoManager(Context context) {
        this.mContext = context;
        mPhotoDao = new PhotoDao(context);
        mMainHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取本地照片接口
     */
    public interface OnLocalPhotoListener {
        void onPhotoLoaded(List<PhotoModel> photoModels);
    }

    /**
     * 获取本地相册接口
     */
    public interface OnLocalAlbumListener {
        void onAlbumLoaded(List<AlbumModel> albumModels);
    }

    /**
     * 获取所有图片，按时间排序
     */
    public void getAllPhoto(final OnLocalPhotoListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                synchronized (lock) {
                    final List<PhotoModel> list = new ArrayList<>();
                    PhotoModel photoModel = new PhotoModel();
                    photoModel.type = PhotoModel.TYPE_CAMERA;
                    list.add(photoModel);
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPhotoLoaded(list);
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * 获取相册列表
     */
    public void getAlbumList(final OnLocalAlbumListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                synchronized (lock) {
                    final List<AlbumModel> list = new ArrayList<>();
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAlbumLoaded(list);
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * 获取相册下的图片
     *
     * @param name
     * @param listener
     */
    public void getAlbumPhoto(final String name, final OnLocalPhotoListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final List<PhotoModel> list = mPhotoDao.getAlbum(name);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPhotoLoaded(list);
                    }
                });
            }
        }.start();
    }

    /**
     * 批量生成缩略图
     *
     * @param list     图片list
     * @param width    指定宽度
     * @param height   指定高度
     * @param listener 本地照片接口
     */
    public void getThumb(final List<PhotoModel> list, final int width, final int height,
                         final OnLocalPhotoListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                synchronized (lock) {
                    for (PhotoModel photoModel : list) {
//                        photoModel.thumbPath = processPhoto(
//                                photoModel.originalPath, width, height);
                    }
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPhotoLoaded(list);
                        }
                    });
                }
            }
        }.start();
    }

//    private String processPhoto(String filePath, int width, int height) {
//        Bitmap bitmap=new Bi
//    }
}
