package com.xinlan.imageeditlibrary.editimage.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;

import com.xinlan.imageeditlibrary.editimage.homing.PictureHoming;

import java.io.FileDescriptor;
import java.io.IOException;

public class PictureUtils {

    private static final Matrix M = new Matrix();

    private PictureUtils() {

    }

    public static void center(RectF win, RectF frame) {
        frame.offset(win.centerX() - frame.centerX(), win.centerY() - frame.centerY());
    }

    public static void fitCenter(RectF win, RectF frame) {
        fitCenter(win, frame, 0);
    }

    public static void fitCenter(RectF win, RectF frame, float padding) {
        fitCenter(win, frame, padding, padding, padding, padding);
    }

    public static void fitCenter(RectF win, RectF frame, float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
        if (win.isEmpty() || frame.isEmpty()) {
            return;
        }

        if (win.width() < paddingLeft + paddingRight) {
            paddingLeft = paddingRight = 0;
            // 忽略Padding 值
        }

        if (win.height() < paddingTop + paddingBottom) {
            paddingTop = paddingBottom = 0;
            // 忽略Padding 值
        }

        float w = win.width() - paddingLeft - paddingRight;
        float h = win.height() - paddingTop - paddingBottom;

        float scale = Math.min(w / frame.width(), h / frame.height());

        // 缩放FIT
        frame.set(0, 0, frame.width() * scale, frame.height() * scale);

        // 中心对齐
        frame.offset(
                win.centerX() + (paddingLeft - paddingRight) / 2 - frame.centerX(),
                win.centerY() + (paddingTop - paddingBottom) / 2 - frame.centerY()
        );
    }

    public static PictureHoming fitHoming(RectF win, RectF frame) {
        PictureHoming dHoming = new PictureHoming(0, 0, 1, 0);

        if (frame.contains(win)) {
            // 不需要Fit
            return dHoming;
        }

        // 宽高都小于Win，才有必要放大
        if (frame.width() < win.width() && frame.height() < win.height()) {
            dHoming.scale = Math.min(win.width() / frame.width(), win.height() / frame.height());
        }

        RectF rect = new RectF();
        M.setScale(dHoming.scale, dHoming.scale, frame.centerX(), frame.centerY());
        M.mapRect(rect, frame);

        if (rect.width() < win.width()) {
            dHoming.x += win.centerX() - rect.centerX();
        } else {
            if (rect.left > win.left) {
                dHoming.x += win.left - rect.left;
            } else if (rect.right < win.right) {
                dHoming.x += win.right - rect.right;
            }
        }

        if (rect.height() < win.height()) {
            dHoming.y += win.centerY() - rect.centerY();
        } else {
            if (rect.top > win.top) {
                dHoming.y += win.top - rect.top;
            } else if (rect.bottom < win.bottom) {
                dHoming.y += win.bottom - rect.bottom;
            }
        }

        return dHoming;
    }

    public static PictureHoming fitHoming(RectF win, RectF frame, float centerX, float centerY) {
        PictureHoming dHoming = new PictureHoming(0, 0, 1, 0);

        if (frame.contains(win)) {
            // 不需要Fit
            return dHoming;
        }

        // 宽高都小于Win，才有必要放大
        if (frame.width() < win.width() && frame.height() < win.height()) {
            dHoming.scale = Math.min(win.width() / frame.width(), win.height() / frame.height());
        }

        RectF rect = new RectF();
        M.setScale(dHoming.scale, dHoming.scale, centerX, centerY);
        M.mapRect(rect, frame);

        if (rect.width() < win.width()) {
            dHoming.x += win.centerX() - rect.centerX();
        } else {
            if (rect.left > win.left) {
                dHoming.x += win.left - rect.left;
            } else if (rect.right < win.right) {
                dHoming.x += win.right - rect.right;
            }
        }

        if (rect.height() < win.height()) {
            dHoming.y += win.centerY() - rect.centerY();
        } else {
            if (rect.top > win.top) {
                dHoming.y += win.top - rect.top;
            } else if (rect.bottom < win.bottom) {
                dHoming.y += win.bottom - rect.bottom;
            }
        }

        return dHoming;
    }


    public static PictureHoming fitHoming(RectF win, RectF frame, boolean isJustInner) {
        PictureHoming dHoming = new PictureHoming(0, 0, 1, 0);

        if (frame.contains(win) && !isJustInner) {
            // 不需要Fit
            return dHoming;
        }

        // 宽高都小于Win，才有必要放大
        if (isJustInner || frame.width() < win.width() && frame.height() < win.height()) {
            dHoming.scale = Math.min(win.width() / frame.width(), win.height() / frame.height());
        }

        RectF rect = new RectF();
        M.setScale(dHoming.scale, dHoming.scale, frame.centerX(), frame.centerY());
        M.mapRect(rect, frame);

        if (rect.width() < win.width()) {
            dHoming.x += win.centerX() - rect.centerX();
        } else {
            if (rect.left > win.left) {
                dHoming.x += win.left - rect.left;
            } else if (rect.right < win.right) {
                dHoming.x += win.right - rect.right;
            }
        }

        if (rect.height() < win.height()) {
            dHoming.y += win.centerY() - rect.centerY();
        } else {
            if (rect.top > win.top) {
                dHoming.y += win.top - rect.top;
            } else if (rect.bottom < win.bottom) {
                dHoming.y += win.bottom - rect.bottom;
            }
        }

        return dHoming;
    }

    public static PictureHoming fillHoming(RectF win, RectF frame) {
        PictureHoming dHoming = new PictureHoming(0, 0, 1, 0);
        if (frame.contains(win)) {
            // 不需要Fill
            return dHoming;
        }

        if (frame.width() < win.width() || frame.height() < win.height()) {
            dHoming.scale = Math.max(win.width() / frame.width(), win.height() / frame.height());
        }

        RectF rect = new RectF();
        M.setScale(dHoming.scale, dHoming.scale, frame.centerX(), frame.centerY());
        M.mapRect(rect, frame);

        if (rect.left > win.left) {
            dHoming.x += win.left - rect.left;
        } else if (rect.right < win.right) {
            dHoming.x += win.right - rect.right;
        }

        if (rect.top > win.top) {
            dHoming.y += win.top - rect.top;
        } else if (rect.bottom < win.bottom) {
            dHoming.y += win.bottom - rect.bottom;
        }

        return dHoming;
    }

    public static PictureHoming fillHoming(RectF win, RectF frame, float pivotX, float pivotY) {
        PictureHoming dHoming = new PictureHoming(0, 0, 1, 0);
        if (frame.contains(win)) {
            // 不需要Fill
            return dHoming;
        }

        if (frame.width() < win.width() || frame.height() < win.height()) {
            dHoming.scale = Math.max(win.width() / frame.width(), win.height() / frame.height());
        }

        RectF rect = new RectF();
        M.setScale(dHoming.scale, dHoming.scale, pivotX, pivotY);
        M.mapRect(rect, frame);

        if (rect.left > win.left) {
            dHoming.x += win.left - rect.left;
        } else if (rect.right < win.right) {
            dHoming.x += win.right - rect.right;
        }

        if (rect.top > win.top) {
            dHoming.y += win.top - rect.top;
        } else if (rect.bottom < win.bottom) {
            dHoming.y += win.bottom - rect.bottom;
        }

        return dHoming;
    }

    public static PictureHoming fill(RectF win, RectF frame) {
        PictureHoming dHoming = new PictureHoming(0, 0, 1, 0);

        if (win.equals(frame)) {
            return dHoming;
        }

        // 第一次时缩放到裁剪区域内
        dHoming.scale = Math.max(win.width() / frame.width(), win.height() / frame.height());

        RectF rect = new RectF();
        M.setScale(dHoming.scale, dHoming.scale, frame.centerX(), frame.centerY());
        M.mapRect(rect, frame);

        dHoming.x += win.centerX() - rect.centerX();
        dHoming.y += win.centerY() - rect.centerY();

        return dHoming;
    }

    public static int inSampleSize(int rawSampleSize) {
        int raw = rawSampleSize, ans = 1;
        while (raw > 1) {
            ans <<= 1;
            raw >>= 1;
        }

        if (ans != rawSampleSize) {
            ans <<= 1;
        }

        return ans;
    }

    public static void rectFill(RectF win, RectF frame) {
        if (win.equals(frame)) {
            return;
        }

        float scale = Math.max(win.width() / frame.width(), win.height() / frame.height());

        M.setScale(scale, scale, frame.centerX(), frame.centerY());
        M.mapRect(frame);

        if (frame.left > win.left) {
            frame.left = win.left;
        } else if (frame.right < win.right) {
            frame.right = win.right;
        }

        if (frame.top > win.top) {
            frame.top = win.top;
        } else if (frame.bottom < win.bottom) {
            frame.bottom = win.bottom;
        }
    }

    /**
     * 获取图片的角度，对于相机拍出来的图片，需要我们手动修正
     */
    public static int getBitmapDegree(Context context, Uri uri) {
        int degree = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return degree;
        }
        try (AssetFileDescriptor assetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r")) {
            if (assetFileDescriptor != null) {
                FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
                ExifInterface exifInterface = new ExifInterface(fileDescriptor);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        } catch (IOException ignored) {
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
            try {
                Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return bmp;
            } catch (Throwable ignored) {
            }
        }
        return bitmap;
    }
}
