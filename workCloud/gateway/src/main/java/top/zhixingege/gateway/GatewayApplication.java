package top.zhixingege.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.ComponentScan;
import top.zhixingege.gateway.loadbalancer.MyLoadBalancerConfiguration;

@SpringBootApplication(scanBasePackages = {
		"top.zhixingege.security",
		"top.zhixingege.common",
		"top.zhixingege.gateway"
})
// 开启负载均衡
// 这种是所有资源都统一配置
//@LoadBalancerClients(defaultConfiguration = MyLoadBalancerConfiguration.class)
// 这种方式是指定资源
@LoadBalancerClients({
		@LoadBalancerClient(value = "WEB-SERVICE", configuration = MyLoadBalancerConfiguration.class)
		,@LoadBalancerClient(value = "SPIDER-SERVICE", configuration = MyLoadBalancerConfiguration.class)
})
//如果别的没启动类的有mapper，则需要在这@MapperScan()
@MapperScan("top.zhixingege.common.dao")
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
