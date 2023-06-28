package top.zhixingege.gateway.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Configuration
public class SentinelConfig implements InitFunc {

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${web.application.name}")
    private String webName;

    @Autowired
    private GatewayProperties gatewayProperties;

    /**
     * 网关规则
     */
    @PostConstruct
    @Override
    public void init() {
        // 限流
        initGatewayRules();
        // 熔断降级
        initDegradeRule();
    }

    // 自定义限流规则
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        // 拿到网关路由，拿到服务id
        List<RouteDefinition> routes = gatewayProperties.getRoutes();
        for (RouteDefinition route : routes) {
            rules.add(new GatewayFlowRule(route.getId())
                    // 限流阈值
                    .setCount(500)
                    // 统计时间窗口，单位是秒，默认是 1 秒
                    .setIntervalSec(1));
        }
        GatewayRuleManager.loadRules(rules);
    }

    // 自定义熔断规则
    private void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        // TODO 这是超时熔断设置
        DegradeRule rule = new DegradeRule();
        rule.setResource(webName);
        // set threshold rt, 1500 ms。
        // 表示同时进来大于等于5个，且请求响应时间都大于1500毫秒，就会触发超时熔断机制
        rule.setCount(500);
        // 最小请求数，默认5个
        rule.setMinRequestAmount(5);
        // 设置降级规则RT, 平均响应时间
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        // 设置时间窗口，发生熔断时，10秒内降级，每秒只能通过上面设置的最小请求量
        rule.setTimeWindow(10);
        rules.add(rule);

        DegradeRuleManager.loadRules(rules);
    }

}
