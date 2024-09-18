package io.dataease.home;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {

    private static final String INDEX_PAGE = "index.html";
    private static final String PANEL_PAGE = "panel.html";

    @SaIgnore
    @GetMapping("/")
    public String index() {
        return INDEX_PAGE;
    }

    @SaIgnore
    @GetMapping("/panel")
    public String panel() {
        return PANEL_PAGE;
    }


}
