package com.viewol.billboard.response;

import com.viewol.common.BaseResponse;
import com.viewol.pojo.AdMedia;

import java.util.List;
import java.util.Map;

public class BillBoardResponse extends BaseResponse {
    private AdMedia adMedia;

    private Map<String, List<String>> data;

    public AdMedia getAdMedia() {
        return adMedia;
    }

    public void setAdMedia(AdMedia adMedia) {
        this.adMedia = adMedia;
    }

    public Map<String, List<String>> getData() {
        return data;
    }

    public void setData(Map<String, List<String>> data) {
        this.data = data;
    }
}
