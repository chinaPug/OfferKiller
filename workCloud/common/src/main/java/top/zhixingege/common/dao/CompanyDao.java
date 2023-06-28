package top.zhixingege.common.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.zhixingege.common.entity.Company;

@Mapper
@Repository

public interface CompanyDao {
    Company getCompanyByName(String companyName);
    boolean updateCompany(Company company);
}
