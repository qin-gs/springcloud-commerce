package com.example.commerce.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.commerce.handler.MyBlockHandler;
import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 用代码实现限流
 */
@Slf4j
@RestController
@RequestMapping("code")
public class FlowRuleCodeController {

    /**
     * 初始化流量控制规则
     */
    @PostConstruct
    public void init() {
        List<FlowRule> flowRules = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        // 设置流量控制规则，限流阈值类型
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 流量控制方法
        flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        // 设置受保护的资源
        flowRule.setResource("flowRuleCode");
        // 设置受保护的资源的阈值 (qps，每秒一次)
        flowRule.setCount(1);

        flowRules.add(flowRule);

        // 加载配置好的规则
        FlowRuleManager.loadRules(flowRules);
    }

    /**
     * 采用硬编码限流规则；
     * 声明成一个 资源；
     * 抛出限流异常时，指定调用的方法 (只使用 blockHandler 需要声明在同一个类中，blockHandlerClass 方法必须是静态的)
     */
    @GetMapping("flow-rule")
    @SentinelResource(value = "flowRuleCode", blockHandler = "blockHandler", blockHandlerClass = MyBlockHandler.class)
    public CommonResponse<String> flowRuleCode() {
        log.info("限流测试正常返回");
        return new CommonResponse<>(0, "限流测试正常返回", "");
    }

}
