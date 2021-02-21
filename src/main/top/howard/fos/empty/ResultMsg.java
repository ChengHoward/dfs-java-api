package top.howard.fos.empty;

public class ResultMsg {
    private Boolean status = false;
    private String msg = "";
    private String fileId = null;
    private String rel = null;
    private String index = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ResultMsg{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", fileId='" + fileId + '\'' +
                ", rel='" + rel + '\'' +
                ", index='" + index + '\'' +
                '}';
    }
}
