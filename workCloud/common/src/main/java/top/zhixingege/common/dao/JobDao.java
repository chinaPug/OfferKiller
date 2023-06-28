package top.zhixingege.common.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.zhixingege.common.entity.Job;

import java.util.List;

@Mapper
@Repository

public interface JobDao {
    boolean deleteJobByCompanyId(int CompanyId);
    int insertJob(Job job);

    boolean insertJobList(List<Job> jobList);

    List<Job> getJobListByCompanyId(int CompanyId);
}
