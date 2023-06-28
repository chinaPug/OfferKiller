package top.zhixingege.spider.taskConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ClassUtils;
import top.zhixingege.spider.component.factor.Spider;
import top.zhixingege.common.dao.CompanyDao;
import top.zhixingege.common.entity.Mail;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class ScheduleTask {
    @Autowired
    private CompanyDao companyDao;

    @Value("${reflect.basePackage}")
    private String BASE_PACKAGE;
    private final String RESOURCE_PATTERN = "/*.class";
    //{秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}

    //0点一次
    @Scheduled(cron = "0/10 * * * * *")
    private void configureTasks() throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        for (Resource resource : resources) {
            //用于读取类信息
            MetadataReader reader = readerfactory.getMetadataReader(resource);
            //扫描到的class
            String classname = reader.getClassMetadata().getClassName();
            Spider spider=(Spider) Class.forName(classname).newInstance();
            spider.updateJob(companyDao.getCompanyByName(spider.getSpiderName()));
        }
        log.info("执行更新岗位库定时任务时间: {}" , LocalDateTime.now());
        //在次执行岗位匹配和发送邮件操作
        Mail mail=new Mail();
        mail.setMailUid(UUID.randomUUID().toString());
        mail.setFromName("zhangzihao");
        mail.setFromMail("1431105872@qq.com");
        mail.setToMail("1431105872@qq.com");
//      mailDTO.setPlanSendTime(parseDateStr("2019-07-26 16:50:52", "yyyy-MM-dd HH:mm:ss"));
        mail.setMailSubject("主题");
        mail.setMailContent("测试正文");
        //kafkaProducer.send(KafkaConstants.Topic.MAIL_SERVICE_NAME, mail);
    }
}