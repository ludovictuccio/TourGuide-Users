package com.tourGuide.users.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tourGuide.users.domain.UserReward;
import com.tourGuide.users.domain.dto.UserRewardsDto;

@FeignClient(value = "microservice-rewards", url = "${proxy.rewards}")
public interface MicroserviceRewardsProxy {

    @PostMapping("/calculateRewards")
    public List<UserReward> calculateRewards(
            @RequestBody final UserRewardsDto user);

}
