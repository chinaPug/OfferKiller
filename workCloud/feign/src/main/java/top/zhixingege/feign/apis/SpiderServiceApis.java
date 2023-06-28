package top.zhixingege.feign.apis;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.zhixingege.common.bean.ResponseVo;
import top.zhixingege.common.entity.Company;
import top.zhixingege.feign.config.FeignConfig;

@FeignClient(name = "${spider.application.name}", contextId = "SpiderServiceApis"
        , configuration = FeignConfig.class)
public interface SpiderServiceApis {
    @PostMapping(value = "/spider/updateJobByCompany", consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean updateJobByCompany(@SpringQueryMap Company company);
}
