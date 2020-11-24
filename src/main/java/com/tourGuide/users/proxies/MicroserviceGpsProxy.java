package com.tourGuide.users.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.dto.VisitedLocationDto;

@FeignClient(value = "microservice-gps", url = "localhost:9002/gps")
public interface MicroserviceGpsProxy {

    @GetMapping("/getClosestAttractions/{userName}")
    public List<ClosestAttraction> getClosestAttractions(
            @PathVariable("userName") String userName);

    @GetMapping("/getUserInstantLocation/{userName}")
    public VisitedLocationDto getUserInstantLocation(
            @PathVariable("userName") String userName);

}
