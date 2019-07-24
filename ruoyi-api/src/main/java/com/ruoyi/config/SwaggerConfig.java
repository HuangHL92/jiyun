package com.ruoyi.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import com.ruoyi.common.config.Global;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2Controller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Swagger2的接口配置
 * 
 * @author ruoyi
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig
{

    private static final String DEFAULT_PATH = "/help";

    /**
     * swagger扫描的包路径
     */
    @Value("${api.basepackage}")
    private String basepackage;

    /**
     * 创建API
     */
    @Bean
    public Docket createRestApi()
    {

        //添加head参数start
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name("x-access-token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        //添加head参数end


        return new Docket(DocumentationType.SWAGGER_2)
                // 详细定制
                .apiInfo(apiInfo())
                .select()
                // 指定当前包路径
                .apis(RequestHandlerSelectors.basePackage(basepackage))
                // 扫描所有
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    /**
     * 添加摘要信息
     */
    private ApiInfo apiInfo()
    {

        // 用ApiInfoBuilder进行定制
        return new ApiInfoBuilder()
                .title("吉运业务接口平台")
                .description("描述：移动、大屏端接口定义和实现")
                .contact(new Contact(Global.getName(), null, null))
                .version("版本号:" + Global.getVersion())
                .build();
    }



//    /**
//     * SwaggerUI资源访问
//     *
//     * @param servletContext
//     * @param order
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public SimpleUrlHandlerMapping swaggerUrlHandlerMapping(ServletContext servletContext,
//                                                            @Value("${swagger.mapping.order:10}") int order) throws Exception {
//        SimpleUrlHandlerMapping urlHandlerMapping = new SimpleUrlHandlerMapping();
//        Map<String, ResourceHttpRequestHandler> urlMap = new HashMap<>();
//        {
//            PathResourceResolver pathResourceResolver = new PathResourceResolver();
//            pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/webjars/"));
//            pathResourceResolver.setUrlPathHelper(new UrlPathHelper());
//
//            ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
//            resourceHttpRequestHandler.setLocations(Arrays.asList(new ClassPathResource("META-INF/resources/webjars/")));
//            resourceHttpRequestHandler.setResourceResolvers(Arrays.asList(pathResourceResolver));
//            resourceHttpRequestHandler.setServletContext(servletContext);
//            resourceHttpRequestHandler.afterPropertiesSet();
//            //设置新的路径
//            urlMap.put(DEFAULT_PATH + "/webjars/**", resourceHttpRequestHandler);
//        }
//        {
//            PathResourceResolver pathResourceResolver = new PathResourceResolver();
//            pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/"));
//            pathResourceResolver.setUrlPathHelper(new UrlPathHelper());
//
//            ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
//            resourceHttpRequestHandler.setLocations(Arrays.asList(new ClassPathResource("META-INF/resources/")));
//            resourceHttpRequestHandler.setResourceResolvers(Arrays.asList(pathResourceResolver));
//            resourceHttpRequestHandler.setServletContext(servletContext);
//            resourceHttpRequestHandler.afterPropertiesSet();
//            //设置新的路径
//            urlMap.put(DEFAULT_PATH + "/**", resourceHttpRequestHandler);
//        }
//        urlHandlerMapping.setUrlMap(urlMap);
//        //调整DispatcherServlet关于SimpleUrlHandlerMapping的排序
//        urlHandlerMapping.setOrder(order);
//        return urlHandlerMapping;
//    }
//
//
//    /**
//     * SwaggerUI接口访问
//     */
//    @Controller
//    @ApiIgnore
//    @RequestMapping(DEFAULT_PATH)
//    public static class SwaggerResourceController implements InitializingBean {
//
//        @Autowired
//        private ApiResourceController apiResourceController;
//
//        @Autowired
//        private Environment environment;
//
//        @Autowired
//        private DocumentationCache documentationCache;
//
//        @Autowired
//        private ServiceModelToSwagger2Mapper mapper;
//
//        @Autowired
//        private JsonSerializer jsonSerializer;
//
//        private Swagger2Controller swagger2Controller;
//
//        @Override
//        public void afterPropertiesSet() {
//            swagger2Controller = new Swagger2Controller(environment, documentationCache, mapper, jsonSerializer);
//        }
//
//        /**
//         * 首页
//         *
//         * @return
//         */
//        @RequestMapping
//        public ModelAndView index() {
//            ModelAndView modelAndView = new ModelAndView("redirect:" + DEFAULT_PATH + "/swagger-ui.html");
//            return modelAndView;
//        }
//
//        @RequestMapping("/swagger-resources/configuration/security")
//        @ResponseBody
//        public ResponseEntity<SecurityConfiguration> securityConfiguration() {
//            return apiResourceController.securityConfiguration();
//        }
//
//        @RequestMapping("/swagger-resources/configuration/ui")
//        @ResponseBody
//        public ResponseEntity<UiConfiguration> uiConfiguration() {
//            return apiResourceController.uiConfiguration();
//        }
//
//        @RequestMapping("/swagger-resources")
//        @ResponseBody
//        public ResponseEntity<List<SwaggerResource>> swaggerResources() {
//            return apiResourceController.swaggerResources();
//        }
//
//        @RequestMapping(value = "/v2/api-docs", method = RequestMethod.GET, produces = {"application/json", "application/hal+json"})
//        @ResponseBody
//        public ResponseEntity<Json> getDocumentation(
//                @RequestParam(value = "group", required = false) String swaggerGroup,
//                HttpServletRequest servletRequest) {
//            return swagger2Controller.getDocumentation(swaggerGroup, servletRequest);
//        }
//    }


}
