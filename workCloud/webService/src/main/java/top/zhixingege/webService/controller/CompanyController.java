package top.zhixingege.webService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.zhixingege.common.bean.ResponseVo;
import top.zhixingege.common.entity.Job;
import top.zhixingege.webService.service.CompanyService;

import java.util.List;


@Controller
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @GetMapping("/updateJob/{companyName}")
    @ResponseBody
    public ResponseVo getCompanyByCompanyName(@PathVariable(name = "companyName") String companyName){
        return companyService.updateJobByCompanyName(companyName);
    }
    @GetMapping("/getJob/{companyName}")
    @ResponseBody
    public ResponseVo<List<Job>> getJob(@PathVariable(name = "companyName") String companyName){
        return  companyService.getJobByCompanyName(companyName);
    }
}
