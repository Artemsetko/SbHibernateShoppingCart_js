package ua.artem.sbshoppingcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Artem on 24.08.2018
 */
@Controller
public class TopController {
    @GetMapping(value = "/top")
    public String homepage() {
        return "topproduct";
    }
}
