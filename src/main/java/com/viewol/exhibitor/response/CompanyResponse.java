package com.viewol.exhibitor.response;

import com.viewol.common.BaseResponse;
import com.viewol.exhibitor.vo.ExhibitorVO;

public class CompanyResponse extends BaseResponse {
    private ExhibitorVO data;

    public ExhibitorVO getData() {
        return data;
    }

    public void setData(ExhibitorVO data) {
        this.data = data;
    }
}
