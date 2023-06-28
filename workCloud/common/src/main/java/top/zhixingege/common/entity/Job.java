package top.zhixingege.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel("岗位实体类")

public class Job implements Serializable {
    private static final long serialVersionUID=1L;
    @ApiModelProperty("岗位ID")
    private transient int jobId;
    @ApiModelProperty("岗位名")
    private String jobName;
    @ApiModelProperty("所属企业ID")
    private transient Integer affiliatedCompanyId;
    @ApiModelProperty("所属企业名")
    private String affiliatedCompanyName;
    @ApiModelProperty("父岗位")
    private Integer fatherJobId;
    @ApiModelProperty("岗位地点")
    private String jobArea;
    @ApiModelProperty("岗位地点列表")
    @TableField(exist = false)
    private List<String> jobAreaList;
    @ApiModelProperty("主要业务")
    private String mainBusiness;
    @ApiModelProperty("岗位要求")
    private String jobRequire;
    @ApiModelProperty("入库时间")
    private Date updateDate;
    @ApiModelProperty("岗位网址")
    private String url;

    public Job(String jobName, int affiliatedCompanyId,String affiliatedCompanyName, int fatherJobId, String jobArea, String mainBusiness, String jobRequire, Date updateDate, String url) {
        this.jobName = jobName;
        this.affiliatedCompanyName = affiliatedCompanyName;
        this.affiliatedCompanyId=affiliatedCompanyId;
        this.fatherJobId = fatherJobId;
        this.jobArea = jobArea;
        this.mainBusiness = mainBusiness;
        this.jobRequire = jobRequire;
        this.updateDate = updateDate;
        this.url = url;
        this.jobAreaList= Arrays.asList(jobArea.split(","));
    }

    public void setJobArea(String jobArea) {
        this.jobArea = jobArea;
        this.jobAreaList= Arrays.asList(jobArea.split(","));
    }


    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", jobName='" + jobName + '\'' +
                ", affiliatedCompanyId=" + affiliatedCompanyId +
                ", affiliatedCompanyName='" + affiliatedCompanyName + '\'' +
                ", fatherJobId=" + fatherJobId +
                ", jobArea=" + jobArea +
                ", mainBusiness='" + mainBusiness + '\'' +
                ", jobRequire='" + jobRequire + '\'' +
                ", updateDate=" + updateDate +
                ", url='" + url + '\'' +
                '}';
    }
}
