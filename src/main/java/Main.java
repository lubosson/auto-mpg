import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author luxc
 * @date 2019-09-29
 */
public class Main {

  private static final Logger log = Logger.getAnonymousLogger();
  private static String prefix = null;
  private static List<String> whitelist = new ArrayList<>();

  public static void main(String[] args) {
    Properties properties = new Properties();
    try {
      properties.load(ClassLoader.getSystemResourceAsStream("generator.properties"));
      prefix = properties.getProperty("whitelistPattern");
      String str = properties.getProperty("whitelist");
      if (str != null && str.trim().length() > 0) {
        whitelist.addAll(Arrays.asList(str.split(",")));
      }
    } catch (Exception e) {
      log.info("config file error!");
      return;
    }

    DataSourceConfig dataSource = new DataSourceConfig();

    dataSource.setUrl(properties.getProperty("datasource.url"));
    dataSource.setUsername(properties.getProperty("datasource.username"));
    dataSource.setPassword(properties.getProperty("datasource.password"));
    dataSource.setDriverName(properties.getProperty("datasource.driver"));

    String outputDir = properties.getProperty("output.dir");
    String author = properties.getProperty("author");
    String parentPath = properties.getProperty("parent.package");

    generator(dataSource, author, outputDir, parentPath);
  }

  private static void generator(DataSourceConfig datasource, String author, String outputDir, String parentPath) {
    // 生成器全局配置
    GlobalConfig globalConfig = new GlobalConfig();
    globalConfig.setOpen(false);
    globalConfig.setBaseResultMap(true);
    globalConfig.setBaseColumnList(true);
    globalConfig.setFileOverride(true);
    globalConfig.setAuthor(author);
    globalConfig.setOutputDir(outputDir);
    globalConfig.setActiveRecord(true);
    globalConfig.setSwagger2(true);
    globalConfig.setEnableCache(false);


    // 数据源配置
    DataSourceConfig dataSourceConfig = new DataSourceConfig();
    dataSourceConfig.setDbType(DbType.MYSQL);
    dataSourceConfig.setUrl(datasource.getUrl());
    dataSourceConfig.setDriverName(datasource.getDriverName());
    dataSourceConfig.setUsername(datasource.getUsername());
    dataSourceConfig.setPassword(datasource.getPassword());

    // 生成策略配置
    StrategyConfig strategyConfig = new StrategyConfig();
    strategyConfig.setNaming(NamingStrategy.underline_to_camel);
    strategyConfig.setRestControllerStyle(true);
    strategyConfig.setEntityBuilderModel(true);
    strategyConfig.setEntityLombokModel(true);
    strategyConfig.setSkipView(true);

    // package配置
    PackageConfig packageConfig = new PackageConfig().setParent(parentPath);

    ConfigBuilder configBuilder = new ConfigBuilder(packageConfig, dataSourceConfig, strategyConfig, null, globalConfig);

    List<TableInfo> tableInfoList = configBuilder.getTableInfoList().stream()
            // whitelist
            .filter(tableInfo -> tableInfo.getName().startsWith(prefix)).collect(Collectors.toList());
    configBuilder.setTableInfoList(tableInfoList);

    // 模型类后缀
    tableInfoList.forEach(tableEntity -> tableEntity.setEntityName(tableEntity.getEntityName().concat("DO")));

    // 生成器
    (new AutoGenerator()).setConfig(configBuilder).execute();

  }
}