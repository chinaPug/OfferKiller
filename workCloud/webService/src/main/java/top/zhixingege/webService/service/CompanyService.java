package top.zhixingege.webService.service;

import top.zhixingege.common.bean.ResponseVo;

public interface CompanyService {
    ResponseVo updateJobByCompanyName(String companyName);
    ResponseVo getJobByCompanyName(String companyName);
}
