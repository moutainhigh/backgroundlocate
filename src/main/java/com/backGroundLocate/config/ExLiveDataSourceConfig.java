package com.backGroundLocate.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.backGroundLocate.mapper.exlive", sqlSessionTemplateRef = "exliveSqlSessionTemplate")
public class ExLiveDataSourceConfig {
    /**
     * 生成数据源.  @Primary 注解声明为默认数据源
     */
    @Bean(name = "exliveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.exlivedatabase")
    public DataSource getMainDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建 SqlSessionFactory
     */
    @Bean(name = "exliveSqlSessionFactory")
    public SqlSessionFactory getMainSqlSessionFactory(@Qualifier("exliveDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/exlive/*.xml"));
        return bean.getObject();
    }

    /**
     * 配置事务管理
     */
    @Bean(name = "exliveTransactionManager")
    public DataSourceTransactionManager getMainTransactionManager(@Qualifier("exliveDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "exliveSqlSessionTemplate")
    public SqlSessionTemplate getMainSqlSessionTemplate(@Qualifier("exliveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
