package top.zhixingege.webService.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.zhixingege.common.bean.CodeMsg;
import top.zhixingege.common.bean.ResponseVo;
import top.zhixingege.common.dao.CompanyDao;
import top.zhixingege.common.dao.JobDao;
import top.zhixingege.common.entity.Company;
import top.zhixingege.common.entity.Job;
import top.zhixingege.feign.apis.SpiderServiceApis;
import top.zhixingege.webService.service.CompanyService;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private SpiderServiceApis spiderServiceApis;
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private JobDao  jobDao;
    @CacheEvict(value = "CompanyList",key = "#companyName")
    public ResponseVo updateJobByCompanyName(String companyName){
        Company company=companyDao.getCompanyByName(companyName);
        if(ObjectUtils.isEmpty(company)){
            return ResponseVo.errorByMsg(CodeMsg.COMPANY_NOT_FOUND);
        }
        return spiderServiceApis.updateJobByCompany(company)==true?ResponseVo.successByMsg(null, "岗位更新成功"):ResponseVo.successByMsg(null, "岗位无需更新");
    }
    @Cacheable(value = "CompanyList",key = "#companyName")
    public ResponseVo getJobByCompanyName(String companyName){
        Company company=companyDao.getCompanyByName(companyName);
        if(ObjectUtils.isEmpty(company)){
            return ResponseVo.errorByMsg(CodeMsg.COMPANY_NOT_FOUND);
        }
        return   ResponseVo.success(jobDao.getJobListByCompanyId(company.getCompanyId()));
    };
}
