package top.zhixingege.spider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.zhixingege.common.bean.CodeMsg;
import top.zhixingege.common.bean.ResponseVo;
import top.zhixingege.spider.component.factor.Spider;
import top.zhixingege.spider.component.factor.SpiderFactor;

import top.zhixingege.common.entity.Company;
import top.zhixingege.common.entity.Job;
import top.zhixingege.spider.service.SpiderService;

import java.util.List;
import java.util.Objects;

@Service
public class SpiderServiceImpl implements SpiderService {
    @Autowired
    private SpiderFactor spiderFactor;


    @Override
    public boolean updateJobByCompany(Company company) {
        boolean res=false;
        try {
            Spider spider=spiderFactor.createSpider(company);
            res=spider.updateJob(company);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
