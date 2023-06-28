package top.zhixingege.spider.component.adapter;

import top.zhixingege.common.entity.Company;
import top.zhixingege.common.entity.Job;

import java.util.Date;
import java.util.Map;

/**
 *     private static final long serialVersionUID=1L;
 *     private transient int jobId;
 *     private String jobName;
 *     private transient int affiliatedCompanyId;
 *     private String affiliatedCompanyName;
 *     private int fatherJobId;
 *     private List<String> jobArea;
 *     private String mainBusiness;
 *     private String jobRequire;
 *     private Date updateDate;
 *     private String url;
 */
public class JsonToJob {
    public static Job HuaweiJobAdapter(Company company, Map map, int fatherJobId, String url){
        String[] areas=(fatherJobId==-1)?map.get("jobArea").toString().split(";"):map.get("LOCDESCS").toString().split(";");
        String jobName=(fatherJobId==-1)?map.get("jobname").toString():map.get("DISPLAYNAME").toString().split(",")[0].substring(6);
        String mainbusiness=(fatherJobId==-1)?map.get("mainBusiness").toString():map.get("MAINBUSINESS").toString();
        String demand=(fatherJobId==-1)?map.get("jobRequire").toString():map.get("DEMAND").toString();
        String jobAreas="";
        for(int i=0;i<areas.length;i++){
            if(areas.length-1!=i){
                jobAreas+=areas[i].split("/")[1]+",";
            }
            else {
                jobAreas+=areas[i].split("/")[1];
            }
        }
        return new Job(jobName,company.getCompanyId(),company.getCompanyName(),fatherJobId,jobAreas,
                mainbusiness,demand,new Date(),url);
    }
}
