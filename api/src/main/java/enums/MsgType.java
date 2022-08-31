package enums;


public enum MsgType {
    REGISTER("register"),
    UPLOAD("upload"),
    DOWNLOAD("download");
    private final String type;
    MsgType(String type) {
        this.type=type;
    }

    public String getType() {
        return type;
    }
}
