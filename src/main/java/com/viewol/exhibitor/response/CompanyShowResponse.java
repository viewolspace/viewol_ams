package com.viewol.exhibitor.response;

import com.viewol.common.BaseResponse;
import com.viewol.exhibitor.vo.CompanyShowVO;

/**
 * describe:
 *
 * @author: shi_lei@suixingpay.com
 * @date: 2019/07/14 21:52:21:52
 * @version: V1.0
 * @review:
 */
public class CompanyShowResponse extends BaseResponse {
    private CompanyShowVO data;

    public CompanyShowVO getData() {
        return data;
    }

    public void setData(CompanyShowVO data) {
        this.data = data;
    }
}
