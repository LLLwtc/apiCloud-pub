/**
 * Knife4j 接口文档配置
 * https://doc.xiaominfo.com/knife4j/documentation/get_start.html
 */
//@Configuration
//@EnableSwagger2
//@Profile("dev")
//public class Knife4jConfig {
//
//    @Bean
//    public Docket defaultApi2() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(new ApiInfoBuilder()
//                        .title("project-backend")
//                        .description("project-backend")
//                        .version("1.0")
//                        .build())
//                .select()
//                // 指定 Controller 扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.yupi.project.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//}
//TODO 配置swagger