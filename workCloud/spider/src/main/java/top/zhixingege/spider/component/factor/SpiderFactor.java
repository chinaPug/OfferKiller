package top.zhixingege.spider.component.factor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.zhixingege.common.entity.Company;

@Component
public class SpiderFactor {
    @Value("${reflect.basePackage}")
    private String reflectBasePackage;
    public  Spider createSpider(Company company){
        if(company!=null && company.getReflectName()!=null){
            try{
                return (Spider) Class.forName(reflectBasePackage+company.getReflectName()+"Spider").newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

}
