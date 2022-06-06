package com.example.commerce.controller;

import com.example.commerce.service.communication.AuthorityFeignClient;
import com.example.commerce.service.communication.UseFeignRaw;
import com.example.commerce.service.communication.UseRibbonService;
import com.example.commerce.service.communication.UseRestTemplateService;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微服务通信
 */
@RestController
@RequestMapping("/communication")
public class CommunicationController {

    private final AuthorityFeignClient authorityFeignClient;
    private final UseRestTemplateService service;
    private final UseRibbonService useRibbonService;
    private final UseFeignRaw useFeignRaw;

    public CommunicationController(UseRestTemplateService service, UseRibbonService useRibbonService,
                                   AuthorityFeignClient authorityFeignClient, UseFeignRaw useFeignRaw) {
        this.service = service;
        this.useRibbonService = useRibbonService;
        this.authorityFeignClient = authorityFeignClient;
        this.useFeignRaw = useFeignRaw;
    }

    @PostMapping("rest-template")
    public JwtToken getJwtTokenByRestTemplate(@RequestBody UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        return service.getJwtTokenByRestTemplate(usernameAndPassword);
    }

    @PostMapping("load-balance")
    public JwtToken getJwtTokenLoadBalance(@RequestBody UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        return service.getJwtTokenLoadBalance(usernameAndPassword);
    }

    @PostMapping("ribbon")
    public JwtToken getJwtTokenByRibbon(@RequestBody UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        return useRibbonService.getJwtTokenByRibbon(usernameAndPassword);
    }

    @PostMapping("ribbon-raw")
    public JwtToken getJwtTokenByRibbonRaw(@RequestBody UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        return useRibbonService.getJwtTokenByRibbonRaw(usernameAndPassword);
    }

    @PostMapping("open-feign")
    public JwtToken getJwtTokenByOpenFeign(@RequestBody UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        return authorityFeignClient.getTokenByFeign(usernameAndPassword);
    }

    @PostMapping("feign-raw")
    public JwtToken feignRaw(@RequestBody UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        return useFeignRaw.feignRaw(usernameAndPassword);
    }
}
