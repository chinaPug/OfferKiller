package top.zhixingege.spider.controller;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.zhixingege.common.bean.CodeMsg;
import top.zhixingege.common.bean.ResponseVo;
import top.zhixingege.common.entity.Company;

import top.zhixingege.spider.service.SpiderService;

import java.util.List;

@RestController
@RequestMapping("/spider")
@Api(value = "爬虫接口", tags = {"爬虫接口"})
@Slf4j
public class SpiderController {
    @Autowired
    private SpiderService spiderService;
    @PostMapping("/updateJobByCompany")
    @ApiOperation("根据企业更新数据")
    public boolean updateJobByCompany(Company company){
        log.info("{}公司职位更新处理", company.getCompanyName());
        return spiderService.updateJobByCompany(company);
    }
}
