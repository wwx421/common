package com.example.wwx.common.entity;

import java.io.Serializable;

/**
 * Created by wwx on 2018/11/13.
 * 照片实体类
 */

public class PhotoModel implements Serializable {

    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PHOTO = 2;
    private static final long serialVersionUID = 1L;

    public String originalPath;
    public String thumbPath;
    public boolean isChecked;
    public int type = TYPE_PHOTO;

    public PhotoModel() {

    }

    public PhotoModel(String originalPath) {
        this.originalPath = originalPath;
    }

    public PhotoModel(String originalPath, boolean isChecked) {
        this.originalPath = originalPath;
        this.isChecked = isChecked;
    }

    /**
     * 重写equals()必须重写hasCode()
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof PhotoModel)) {
            return false;
        }

        PhotoModel photoModel = (PhotoModel) obj;
        if (originalPath == null) {
            if (photoModel.originalPath != null) {
                return false;
            }
        } else if (!originalPath.equals(photoModel.originalPath)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((originalPath == null) ? 0 : originalPath.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PhotoModel{type=" + type + ",thumbPath=" + thumbPath + '\'' +
                ",originalPath=" + originalPath + '\'' + "}";
    }
}
