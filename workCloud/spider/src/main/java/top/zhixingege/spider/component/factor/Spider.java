package top.zhixingege.spider.component.factor;

import org.jsoup.nodes.Document;
import top.zhixingege.common.entity.Company;

import java.io.IOException;

public interface Spider {
    String Agent="Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)";


    Document get(String url) throws IOException, InterruptedException;

    boolean updateJob(Company company) throws IOException, InterruptedException;

    String getSpiderName();
}
