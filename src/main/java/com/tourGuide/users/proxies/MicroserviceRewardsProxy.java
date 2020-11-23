package com.tourGuide.users.proxies;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "microservice-rewards", url = "localhost:9003/rewards")
public interface MicroserviceRewardsProxy {

}
