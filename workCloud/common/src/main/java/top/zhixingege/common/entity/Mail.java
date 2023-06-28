package top.zhixingege.common.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel("邮件实体类")

public class Mail implements Serializable {
    private static final Long serialVersionUID=1L;
    //邮件发送方唯一标识（发送方生成）
    private String mailUid;
    //邮件发送方名称
    private String fromName;
    //发送邮件地址
    private String fromMail;
    //接收邮件地址（多个邮箱则用逗号","隔开）
    private String toMail;
    //抄送邮件地址（多个邮箱则用逗号","隔开）
    private String ccMail;
    //密送邮件地址（多个邮箱则用逗号","隔开）
    private String bccMail;
    //计划发送时间
    private Date planSendTime;
    //邮件发送时间
    private Date sendTime;
    //邮件发送状态（0未发送，1发送成功，2发送失败）
    private Integer status;
    //发送次数
    private Integer sendNum;
    //邮件主题
    private String mailSubject;
    //邮件内容
    private String mailContent;
}
