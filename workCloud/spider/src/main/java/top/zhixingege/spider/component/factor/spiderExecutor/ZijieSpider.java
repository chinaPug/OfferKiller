package top.zhixingege.spider.component.factor.spiderExecutor;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import top.zhixingege.spider.component.factor.Spider;
import top.zhixingege.common.entity.Company;

import java.io.IOException;

@Component
public class ZijieSpider implements Spider {
    public static final String CompanyName="字节";
    @Override
    public Document get(String url) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public boolean updateJob(Company company) throws IOException, InterruptedException {
        return false;
    }

    @Override
    public String getSpiderName() {
        return this.CompanyName;
    }
}
