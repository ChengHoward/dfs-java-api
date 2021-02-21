package top.howard.fos;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import top.howard.fos.empty.FileInfo;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import top.howard.fos.empty.ResultMsg;

import static top.howard.fos.FileManagerApi.*;

public class UnifiedFileManager {
    private String readKey = null;
    private String writeKey = null;
    private String protocol = "http";
    private String host = "localhost";
    private int port = 80;

    public UnifiedFileManager(String Key) {
        this.readKey = Key;
        this.writeKey = Key;
    }

    UnifiedFileManager(String readKey, String writeKey) {
        this.readKey = readKey;
        this.writeKey = writeKey;
    }

    public String getReadKey() {
        return readKey;
    }

    public String getWriteKey() {
        return readKey;
    }

    private String getHref(String path) {
        try {
            URL url = new URL(this.protocol, this.host, this.port, path);
            return url.toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    List<FileInfo> getFileList(String index) {
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(this.getHref(GET_LIST_PATH) + this.readKey);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        if (index != null) {
            Part[] parts = {new StringPart("index", index, "utf-8")};
            MultipartRequestEntity entity = new MultipartRequestEntity(parts, postMethod.getParams());
            postMethod.setRequestEntity(entity);
        }
        try {
            client.executeMethod(postMethod);
            String result = new String(postMethod.getResponseBody(), StandardCharsets.UTF_8);
            JSONArray arr = JSONObject.parseArray(result);
            List<FileInfo> res = new ArrayList<FileInfo>();
            for (Object fi : arr) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setId(((Map<String, String>) fi).get("id"));
                fileInfo.setFileName(((Map<String, String>) fi).get("file_name"));
                fileInfo.setFileSize(((Map<String, String>) fi).get("file_size"));
                fileInfo.setSuffix(((Map<String, String>) fi).get("file_suffix"));
                fileInfo.setRel(((Map<String, String>) fi).get("rel"));
                fileInfo.setIndex(((Map<String, String>) fi).get("index"));
                res.add(fileInfo);
            }
            return res;
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FileInfo> getFileList() {
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(this.getHref(GET_LIST_PATH) + this.readKey);
        try {
            client.executeMethod(postMethod);
            String result = new String(postMethod.getResponseBody(), StandardCharsets.UTF_8);
            JSONArray arr = JSONObject.parseArray(result);
            List<FileInfo> res = new ArrayList<FileInfo>();
            for (Object fi : arr) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setId(((Map<String, String>) fi).get("id"));
                fileInfo.setFileName(((Map<String, String>) fi).get("file_name"));
                fileInfo.setFileSize(((Map<String, String>) fi).get("file_size"));
                fileInfo.setSuffix(((Map<String, String>) fi).get("file_suffix"));
                fileInfo.setRel(((Map<String, String>) fi).get("rel"));
                fileInfo.setIndex(((Map<String, String>) fi).get("index"));
                res.add(fileInfo);
            }
            return res;
        } catch (HttpException e) {
        } catch (IOException e) {
        } catch (JSONException e) {
        }
        return null;
    }

    ResultMsg uploadFile(File file, String rel, String index) throws FileNotFoundException {
        HttpClient client = new HttpClient();
        ResultMsg resultMsg = new ResultMsg();
        PostMethod postMethod = new PostMethod(this.getHref(UPLOAD_PATH) + this.writeKey);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        FilePart filePart = new FilePart("file", file);
        filePart.setCharSet("utf-8");
        if (rel == null) rel = "";
        if (index == null) index = "";
        Part[] parts = {new StringPart("index", index, "utf-8"),
                new StringPart("rel", rel, "utf-8"), filePart};
        MultipartRequestEntity entity = new MultipartRequestEntity(parts, postMethod.getParams());
        postMethod.setRequestEntity(entity);
        try {
            client.executeMethod(postMethod);
            String result = new String(postMethod.getResponseBody(), StandardCharsets.UTF_8);
            JSONObject map = JSONObject.parseObject(result);
            resultMsg.setStatus((Boolean) map.get("status"));
            resultMsg.setMsg((String) map.get("msg"));
            if (resultMsg.getStatus()) {
                resultMsg.setFileId((String) ((JSONObject) map.get("data")).get("file_id"));
                resultMsg.setRel((String) ((JSONObject) map.get("data")).get("rel"));
                resultMsg.setIndex((String) ((JSONObject) map.get("data")).get("index"));
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultMsg;
    }

    void downloadFile(String id, Boolean isRel) {
        HttpClient client = new HttpClient();
        HttpMethod getMethod;
//        System.out.println(this.getHref(DOWNLOAD_BY_REL_PATH) + this.readKey + "/" + id);
        if (isRel) {
            getMethod = new GetMethod(this.getHref(DOWNLOAD_BY_REL_PATH) + this.readKey + "/" + id);
            getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        } else {
            getMethod = new GetMethod(this.getHref(DOWNLOAD_BY_ID_PATH) + this.readKey + "/" + id);
            getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        }
        try {
            client.executeMethod(getMethod);
            String dispositionValue = "";
            if (getMethod.getStatusCode() == 200) {
                for (Header header : getMethod.getResponseHeaders()) {
                    if (header.getName().equals("Content-Disposition")) {
                        dispositionValue = header.getValue().toString();
                        break;
                    }
                }
                System.out.println(dispositionValue);
                int index = dispositionValue.indexOf("filename=");
                String filename = dispositionValue.substring(index + 10, dispositionValue.length() - 1);
                this.bytesToFile(getMethod.getResponseBody(), "C:\\Users\\Howard\\Downloads\\" + filename);
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    byte[] getFileBytes(String id, Boolean isRel){
        HttpClient client = new HttpClient();
        HttpMethod getMethod;
        byte[] fileBytes = null;
        if (isRel) {
            getMethod = new GetMethod(this.getHref(DOWNLOAD_BY_REL_PATH) + this.readKey + "/" + id);
            getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        } else {
            getMethod = new GetMethod(this.getHref(DOWNLOAD_BY_ID_PATH) + this.readKey + "/" + id);
            getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        }
        try {
            client.executeMethod(getMethod);
            String dispositionValue = "";
            if (getMethod.getStatusCode() == 200) {
                for (Header header : getMethod.getResponseHeaders()) {
                    if (header.getName().equals("Content-Disposition")) {
                        dispositionValue = header.getValue().toString();
                        break;
                    }
                }
                fileBytes = getMethod.getResponseBody();
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

    HttpMethod getFileHttpMethod(String id, Boolean isRel){
        HttpClient client = new HttpClient();
        HttpMethod getMethod;
        if (isRel) {
            getMethod = new GetMethod(this.getHref(DOWNLOAD_BY_REL_PATH) + this.readKey + "/" + id);
            getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        } else {
            getMethod = new GetMethod(this.getHref(DOWNLOAD_BY_ID_PATH) + this.readKey + "/" + id);
            getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        }
        try {
            client.executeMethod(getMethod);
            String dispositionValue = "";
            if (getMethod.getStatusCode() == 200) {
                for (Header header : getMethod.getResponseHeaders()) {
                    if (header.getName().equals("Content-Disposition")) {
                        dispositionValue = header.getValue().toString();
                        break;
                    }
                }
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getMethod;
    }

    String getViewURL(String id, Boolean isRel) {
        if (isRel)
            return this.getHref(VIEW_BY_REL) + this.readKey + "/" + id;
        else
            return this.getHref(VIEW_BY_FILE_ID) + this.readKey + "/" + id;
    }


    public static byte[] fileToBytes(String filePath) {
        byte[] buffer = null;
        File file = new File(filePath);

        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException ex) {
            } finally {
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return buffer;
    }

    public static void bytesToFile(byte[] buffer, final String filePath) {

        File file = new File(filePath);

        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;

        try {
            output = new FileOutputStream(file);

            bufferedOutput = new BufferedOutputStream(output);

            bufferedOutput.write(buffer);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (null != bufferedOutput) {
                try {
                    bufferedOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
