package com.tourGuide.users.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tourGuide.users.domain.dto.UserRewardsDto;

@FeignClient(value = "microservice-rewards", url = "localhost:9003/rewards")
public interface MicroserviceRewardsProxy {

    @GetMapping("/calculateRewards/{userName}")
    UserRewardsDto calculateRewards(
            @PathVariable("userName") final String userName);

}
