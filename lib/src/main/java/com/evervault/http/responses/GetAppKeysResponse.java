package com.evervault.http.responses;

public class GetAppKeysResponse {
    private String appUuid;
    private String teamUuid;
    private String key;
    private String ecdhKey;
    private String ecdhP256Key;
    private String ecdhP256KeyUncompressed;

    public GetAppKeysResponse(String appUuid, String teamUuid, String key, String ecdhKey, String ecdhP256Key, String ecdhP256KeyUncompressed) {
        this.appUuid = appUuid;
        this.teamUuid = teamUuid;
        this.key = key;
        this.ecdhKey = ecdhKey;
        this.ecdhP256Key = ecdhP256Key;
        this.ecdhP256KeyUncompressed = ecdhP256KeyUncompressed;
    }

    public String getAppUuid() {
        return appUuid;
    }

    public void setAppUuid(String appUuid) {
        this.appUuid = appUuid;
    }

    public String getTeamUuid() {
        return teamUuid;
    }

    public void setTeamUuid(String teamUuid) {
        this.teamUuid = teamUuid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEcdhKey() {
        return ecdhKey;
    }

    public void setEcdhKey(String ecdhKey) {
        this.ecdhKey = ecdhKey;
    }

    public String getEcdhP256Key() {
        return ecdhP256Key;
    }

    public void setEcdhP256Key(String ecdhP256Key) {
        this.ecdhP256Key = ecdhP256Key;
    }

    public String getEcdhP256KeyUncompressed() {
        return ecdhP256KeyUncompressed;
    }

    public void setEcdhP256KeyUncompressed(String ecdhP256KeyUncompressed) {
        this.ecdhP256KeyUncompressed = ecdhP256KeyUncompressed;
    }

    @Override
    public String toString() {
        return "GetAppKeysResponse{" +
                "appUuid='" + appUuid + '\'' +
                ", teamUuid='" + teamUuid + '\'' +
                ", key='" + key + '\'' +
                ", ecdhKey='" + ecdhKey + '\'' +
                ", ecdhP256Key='" + ecdhP256Key + '\'' +
                ", ecdhP256KeyUncompressed='" + ecdhP256KeyUncompressed + '\'' +
                '}';
    }
}
