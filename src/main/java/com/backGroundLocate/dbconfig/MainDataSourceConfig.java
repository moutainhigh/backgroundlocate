package com.backGroundLocate.dbconfig;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.backGroundLocate.mapper.main", sqlSessionTemplateRef = "mainSqlSessionTemplate")
public class MainDataSourceConfig {

    /**
     * 生成数据源.  @Primary 注解声明为默认数据源
     */
    @Bean(name = "mainDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.maindatabase")
    @Primary
    public DataSource getMainDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建 SqlSessionFactory
     */
    @Bean(name = "mainSqlSessionFactory")
    @Primary
    public SqlSessionFactory getMainSqlSessionFactory(@Qualifier("mainDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/main/*.xml"));
        return bean.getObject();
    }

    /**
     * 配置事务管理
     */
    @Bean(name = "mainTransactionManager")
    @Primary
    public DataSourceTransactionManager getMainTransactionManager(@Qualifier("mainDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "mainSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate getMainSqlSessionTemplate(@Qualifier("mainSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}