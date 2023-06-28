package top.zhixingege.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ApiModel("企业实体类")

public class Company implements Serializable {
    private static final Long serialVersionUID=1L;
    @ApiModelProperty("一致性校验码")
    private transient String MD5;
    @ApiModelProperty("企业ID")
    private Integer companyId;
    @ApiModelProperty("企业名")
    private String companyName;
    @ApiModelProperty("爬虫工厂反射名")
    transient private String reflectName;
    @ApiModelProperty("企业爬虫网址")
    private String companyUrl;
    @ApiModelProperty("上次收录企业的岗位数")
    private Integer lastJobNums;

    public Company(String companyName, String reflectName, String companyUrl) {
        this.companyName = companyName;
        this.reflectName = reflectName;
        this.companyUrl = companyUrl;
    }
}
