package com.example.lenovo.phone;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhengzhihua on 2018/11/19.11:07
 * Update 2018/11/19 11:07
 * Describe
 */

public class FileUtil {
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "aaaimage";
    private static String imgPath;

    /**
     * 初始化保存路径
     *
     * @return
     */
    private static String initPath() {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdir();
            }
        }
        return storagePath;
    }

    public static void saveFile(String paths,String name){
        String path = initPath();
        imgPath = path + "/" + name + ".jpg";
        File file=new File(imgPath);
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fis = new FileInputStream(paths);
            FileOutputStream uos = new FileOutputStream(file);
            int n=1024;
            byte a[]= new byte[n];
            try {
                while ((fis.read(a, 0, n) != -1) && (a.length > 0)) {
                    uos.write(a);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
                uos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    /**
     * @return 保存到sd卡的图片路径
     */
    public static String getImgPath() {
        return imgPath;
    }
}
