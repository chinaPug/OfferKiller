package top.zhixingege.gateway.loadbalancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

// 自定义轮询
public class MyRoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private static final Logger log = LoggerFactory.getLogger(MyRoundRobinLoadBalancer.class);

    final AtomicInteger position;

    final String serviceId;

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     *                                            {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId                           id of the service for which to choose an instance
     */
    public MyRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                    String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     *                                            {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId                           id of the service for which to choose an instance
     * @param seedPosition                        Round Robin element position marker
     */
    public MyRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                    String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }

    @SuppressWarnings("rawtypes")
    @Override
    // see original
    // https://github.com/Netflix/ocelli/blob/master/ocelli-core/
    // src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        // Ignore the sign bit, this allows pos to loop sequentially from 0 to
        // Integer.MAX_VALUE
        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;

        // TODO 原来的算法
//        int i = pos % instances.size();
        // instances是指某个资源有几个服务，也就是集群数。pos是记录某个资源被访问次数
//        ServiceInstance instance = instances.get(i);

        // TODO 自定义算法，比如指定服务器多处理一次循环
        int i = pos % (instances.size() + 1);
        ServiceInstance instance = null;
        if (i == instances.size()) {
            for (ServiceInstance serviceInstance : instances) {
                if (serviceInstance.getHost().equals("192.168.1.6") && "9906".equals(serviceInstance.getPort())) {
                    instance = serviceInstance;
                }
            }
            if (null == instance) {
                instance = instances.get(i - 1);
            }
        } else {
            // instances是指某个资源有几个服务，也就是集群数。pos是记录某个资源被访问次数
            instance = instances.get(i);
        }

        return new DefaultResponse(instance);
    }
}

