package top.zhixingege.spider.service;

import top.zhixingege.common.bean.ResponseVo;
import top.zhixingege.common.entity.Company;
import top.zhixingege.common.entity.Job;

import java.util.List;

public interface SpiderService {
    //更新操作

    boolean updateJobByCompany(Company company);
}
