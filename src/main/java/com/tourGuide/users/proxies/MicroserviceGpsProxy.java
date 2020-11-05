//package com.tourGuide.users.proxies;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient(value = "tracker", url = "http://localhost:8082/gps")
//public interface MicroserviceGpsProxy {
//
//    @GetMapping("/getLocation")
//    public String getUserLocation(@RequestParam final String userName);
//
//}
