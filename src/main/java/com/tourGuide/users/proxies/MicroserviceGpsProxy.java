package com.tourGuide.users.proxies;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tourGuide.users.domain.ClosestAttraction;
import com.tourGuide.users.domain.dto.AttractionDto;
import com.tourGuide.users.domain.dto.VisitedLocationDto;

@FeignClient(value = "microservice-gps", url = "localhost:9002/gps")
public interface MicroserviceGpsProxy {

    @GetMapping("/getClosestAttractions/{userName}")
    public List<ClosestAttraction> getClosestAttractions(
            @PathVariable("userName") String userName);

    @GetMapping("/getUserInstantLocation/{userId}")
    public VisitedLocationDto getUserInstantLocation(
            @PathVariable("userId") UUID userId);

    @GetMapping("/getAllAttractions")
    public List<AttractionDto> getAllAttractions();

}
