package com.example.commerce;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * 生成数据库文档
 */
@SpringBootTest(classes = AuthorityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class DatabaseDocTest {

    @Autowired
    private ApplicationContext context;

    public ProcessConfig processConfig() {
        // 忽略数据库中的某些表名，前缀，后缀
        List<String> ignoreTableName = Collections.singletonList("undo_log");
        List<String> ignorePrefix = List.of("a", "b");
        List<String> ignoreSuffix = List.of("a", "b");

        return ProcessConfig.builder()
                .designatedTableName(Collections.emptyList())
                .designatedTablePrefix(Collections.emptyList())
                .designatedTablePrefix(Collections.emptyList())
                .ignoreTableName(ignoreTableName)
                .ignoreTablePrefix(ignorePrefix)
                .ignoreTableSuffix(ignoreSuffix)
                .build();
    }

    @Test
    public void buildDoc() {
        DataSource dataSource = context.getBean(DataSource.class);

        // 生成文件配置
        EngineConfig engineConfig = EngineConfig.builder().fileOutputDir("./")
                .openOutputDir(true)
                .fileType(EngineFileType.HTML)
                .produceType(EngineTemplateType.freemarker)
                .build();

        // 生成文档
        Configuration configuration = Configuration.builder()
                .version("1.0.0")
                .description("数据库文档")
                .dataSource(dataSource)
                .engineConfig(engineConfig)
                .produceConfig(processConfig())
                .build();
        new DocumentationExecute(configuration).execute();
    }
}
