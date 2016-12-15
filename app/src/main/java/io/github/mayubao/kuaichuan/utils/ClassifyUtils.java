package io.github.mayubao.kuaichuan.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;

/**
 * Created by mayubao on 2016/12/14.
 * Contact me 345269374@qq.com
 */
public class ClassifyUtils {

    /**
     * 过滤指定类型的FileInfo
     * @param hashMap
     * @param type
     * @return
     */
    public static List<FileInfo> filter(Map<String, FileInfo> hashMap, int type){
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();

        for(Map.Entry<String, FileInfo> entry : hashMap.entrySet()){
            FileInfo fileInfo = entry.getValue();
//            if(FileUtils.isApkFile(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }else if(FileUtils.isJpgFile(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }else if (FileUtils.isMp3File(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }else if (FileUtils.isMp4File(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }

            switch (type){
                case FileInfo.TYPE_APK:{
                    if(FileUtils.isApkFile(fileInfo.getFilePath())){
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
                case FileInfo.TYPE_JPG:{
                    if(FileUtils.isJpgFile(fileInfo.getFilePath())){
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
                case FileInfo.TYPE_MP3:{
                    if (FileUtils.isMp3File(fileInfo.getFilePath())){
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
                case FileInfo.TYPE_MP4:{
                    if (FileUtils.isMp4File(fileInfo.getFilePath())){
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
            }
        }

        return fileInfos;
    }

}
