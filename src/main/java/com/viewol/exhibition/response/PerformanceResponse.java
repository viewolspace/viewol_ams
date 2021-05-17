package com.viewol.exhibition.response;

import com.viewol.common.BaseResponse;
import com.viewol.pojo.Performance;
import com.viewol.pojo.TechReport;

public class PerformanceResponse extends BaseResponse {
    private Performance data;

    public Performance getData() {
        return data;
    }

    public void setData(Performance data) {
        this.data = data;
    }
}
