package com.example.lenovo.phone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zhengzhihua on 2018/11/19.17:35
 * Update 2018/11/19 17:35
 * Describe
 */

public class BitmapUtil {

    /**
     * 压缩图片，避免OOM异常
     */
    public static Bitmap decodeSampledBitmapFromFilePath(String imagePath,String newImagepath,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap=BitmapFactory.decodeFile(imagePath,options);
        return ZipImage(bitmap,newImagepath);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap ZipImage(Bitmap bitmap,String path){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        byte[] b = baos.toByteArray();
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            while ((options > 10) && ((b.length/1024)>50)){
                baos.reset();//重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                b = baos.toByteArray();
                options = options - 10;//每次都减少10
                Log.d("aaaaaaa", "ZipImage: "+baos.toByteArray().length);
                Log.d("aaaaaaa", "ZipImage: "+ baos.size());
            }
            while ((options > 0 && options <= 10 )&& (b.length/1024)>50) {
                baos.reset();//重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                b = baos.toByteArray();
                options -= 1;//每次都减少10
                Log.d("aaaaaaa", "ZipImage: "+baos.toByteArray().length);
            }

        try {
            File file=new File(path);
            OutputStream fo=new FileOutputStream(file);
            baos.writeTo(fo);
            baos.reset();
            baos.close();
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
