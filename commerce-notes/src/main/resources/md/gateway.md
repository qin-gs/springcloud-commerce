# SpringCloud gateway 原理



工作原理

<img src="./assets/spring_cloud_gateway_diagram.png" alt="spring_cloud_gateway_diagram" style="zoom:75%;" />

常用过滤器

| 过滤器名称                              | 作用           |
| --------------------------------------- | -------------- |
| AddRequestParameterGatewayFilterFactory | 添加请求参数   |
| AddRequestHeaderGatewayFilterFactory    | 添加请求头参数 |
| AddResponseHeaderGatewayFilterFactory   | 添加响应头参数 |
| StripPrefixGatewayFilterFactory         | 去除前缀       |
| PrefixPathGatewayFilterFactory          | 添加前缀       |





核心配置 `org.springframework.cloud.gateway.config.GatewayAutoConfiguration`

- `org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping`

  查找匹配的 Route 进行处理

- `org.springframework.cloud.gateway.config.GatewayProperties`

  网关配置

- `org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator`

  创建一个根据 `RouteDefinition` 转换的路由定位器

- `org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter`

  初始化路由负载均衡器

- `org.springframework.cloud.gateway.filter.NettyRoutingFilter`





webflux + reactor

1. 请求过来
2. `org.springframework.web.reactive.DispatcherHandler#handler` (`DispatcherServler#handler`)
3. 找到 RoutePredicteHandlerMapping
4. RouteDefinition， Route，返回通过谓词过滤的具体路由，将选出来路由放入 `ServerWebExchange`
5. `invokeHandler` 使用责任链 (`org.springframework.cloud.gateway.handler.FilteringWebHandler.DefaultGatewayFilterChain`)，依次执行过滤器 
6. 其中一个是 `ReactiveLoadBalancerClientFilter` 使用 `ribbon` 进行负载均衡



