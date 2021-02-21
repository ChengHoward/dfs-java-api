package top.howard.fos;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        UnifiedFileManager unifiedFileManager = new UnifiedFileManager("4ce30859-3670-4713-b5fe-c54b58cd7cfb", "9cb380c1-9599-45b7-a799-eebcf381727f");
        //System.out.println(unifiedFileManager.getFileList());
        //文件上传
        //System.out.println(unifiedFileManager.uploadFile(new File("C:\\Users\\xxx\\Pictures\\5fa12c832727a_5fa12ca69d595.jpg"),"994c8c95-0b91-418b-9fb6-3ffe81e068dd","glsj"));
        //文件下载
        //uni fiedFileManager.downloadFile("994c8c95-0b91-418b-9fb6-3ffe81e068dd",true);
    }
}

