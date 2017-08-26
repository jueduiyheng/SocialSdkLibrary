package com.march.socialsdk.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.march.socialsdk.SocialSdk;
import com.march.socialsdk.model.ShareMediaObj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * CreateAt : 2017/5/22
 * Describe : ShareMediaObj 辅助类
 *
 * @author chendong
 */
public class ShareObjHelper {

    /**
     * 检测缩略图文件是不是存在
     *
     * @param obj ShareMediaObj
     * @return 是否可用
     */
    public static boolean checkThumbImagePathValid(ShareMediaObj obj) {
        if (OtherHelper.isEmpty(obj.getThumbImagePath()))
            return false;
        String thumbImagePath = obj.getThumbImagePath();
        // 文件是不是存在
        if (FileHelper.isExist(thumbImagePath))
            return true;
        String localPath = FileHelper.getShareUrlMapLocalPath(thumbImagePath);
        // 映射后文件是否存在
        if (FileHelper.isExist(localPath)) {
            obj.setThumbImagePath(localPath);
            return true;
        }
        return false;
    }

    /**
     * 下载 ShareMediaObj 中的 thumbImage
     *
     * @param obj ShareMediaObj
     * @throws IOException io
     */
    public static void prepareThumbImagePath(ShareMediaObj obj) throws IOException {
        if (checkThumbImagePathValid(obj))
            return;
        if (!FileHelper.isHttpPath(obj.getThumbImagePath()))
            throw new IllegalArgumentException("本地文件不存在，又不是网络路径");
        String thumbImagePath = obj.getThumbImagePath();
        String localPath = FileHelper.getShareUrlMapLocalPath(thumbImagePath);
        FileHelper.downloadFileSync(thumbImagePath, localPath);
        obj.setThumbImagePath(localPath);
    }

    public static String resImageToLocalPath(Context context, int resId) {
        String fileName = OtherHelper.getMD5(resId + "") + FileHelper.POINT_PNG;
        File saveFile = new File(SocialSdk.getConfig().getShareCacheDirPath(), fileName);
        if (saveFile.exists())
            return saveFile.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(saveFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            BitmapHelper.recyclerBitmaps(bitmap);
        }
        return saveFile.getAbsolutePath();
    }
}
