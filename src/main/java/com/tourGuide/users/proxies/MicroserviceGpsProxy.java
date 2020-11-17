package com.tourGuide.users.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.tourGuide.users.domain.dto.AttractionDto;

@FeignClient(value = "microservice-gps", url = "localhost:9002/gps")
public interface MicroserviceGpsProxy {

    @GetMapping("/getAllAttractions")
    public List<AttractionDto> getAllAttractions();

}
