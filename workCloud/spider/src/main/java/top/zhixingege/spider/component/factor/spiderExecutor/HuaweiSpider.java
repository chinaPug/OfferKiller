package top.zhixingege.spider.component.factor.spiderExecutor;

import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import top.zhixingege.spider.component.adapter.JsonToJob;
import top.zhixingege.spider.component.factor.Spider;
import top.zhixingege.common.dao.CompanyDao;
import top.zhixingege.common.dao.JobDao;
import top.zhixingege.common.entity.Company;
import top.zhixingege.common.entity.Job;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HuaweiSpider implements Spider {
    public static final String CompanyName="华为";
    @Autowired
    private JobDao initJobDao;
    @Autowired
    private CompanyDao initCompanyDao;
    private static JobDao jobDao;
    private static CompanyDao companyDao;
    final String ROOT="https://career.huawei.com/reccampportal/services/portal/portalpub/getJobDetail/newHr?jobId=";
    final String JOB_REQUIRE="https://career.huawei.com/reccampportal/services/portal/portaluser/findIntentListByJobRequirementId/newHr/zh_CN/{jobRequirementId}/2";

    @Override
    public Document get(String url) throws IOException, InterruptedException {
        Thread.sleep(100);
        return Jsoup.connect(url).ignoreContentType(true).userAgent(Agent).timeout(10000).get();
    }

    @Override
    public boolean updateJob(Company company) throws IOException, InterruptedException {
        Document document=get(company.getCompanyUrl().replace("{lastJobNums}",String.valueOf(company.getLastJobNums())));
        Map maps= JSON.parseObject(document.text());
        int totalRows= (int) JSON.parseObject(maps.get("pageVO").toString()).get("totalRows");
        String MD5= DigestUtils.md5DigestAsHex(maps.get("result").toString().getBytes());
        if (company.getLastJobNums()==totalRows&&MD5.equals(company.getMD5())){
                return false;
        }
        company.setMD5(MD5);
        company.setLastJobNums(totalRows);
        companyDao.updateCompany(company);
        jobDao.deleteJobByCompanyId(company.getCompanyId());
        document= get(company.getCompanyUrl().replace("{lastJobNums}",String.valueOf(company.getLastJobNums())));
        maps= JSON.parseObject(document.text());
        List<String> list=JSON.parseArray(JSON.toJSONString(maps.get("result")), String.class);
        for (String item : list){
            Map map=JSON.parseObject(item);
            document=get(ROOT+map.get("jobId"));
            Map fatherMap= JSON.parseObject(document.text());
            Job fatherJob=JsonToJob.HuaweiJobAdapter(company,fatherMap,-1,ROOT+map.get("jobId"));
            int fatherJobId=jobDao.insertJob(fatherJob);
            document=get(JOB_REQUIRE.replace("{jobRequirementId}",String.valueOf(fatherMap.get("jobRequirementId"))));
            List<String> SonsList=JSON.parseArray(document.text(), String.class);
            if(!SonsList.isEmpty()){
                List<Job> jobList=new ArrayList<>();
                for(String son:SonsList){
                    jobList.add(JsonToJob.HuaweiJobAdapter(company,JSON.parseObject(son),fatherJobId,ROOT+map.get("jobId")));
                }
                jobDao.insertJobList(jobList);
            }
        }
        return true;
    }

    public String getSpiderName(){
        return CompanyName;
    }
    @PostConstruct
    public void init(){
        jobDao=this.initJobDao;
        companyDao=this.initCompanyDao;
    }
}
