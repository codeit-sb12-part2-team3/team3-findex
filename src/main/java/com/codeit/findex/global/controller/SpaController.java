package com.codeit.findex.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * React SPA 라우팅 지원 컨트롤러
 * /api/** 이외의 모든 경로를 index.html로 포워딩하여
 * React Router가 클라이언트 사이드에서 라우팅 처리할 수 있도록 함
 */
@Controller
public class SpaController {

    @RequestMapping(value = {
            "/",
            "/dashboard",
            "/index-management",
            "/data-management",
            "/integrations"
    })
    public String index() {
        return "forward:/index.html";
    }
}
