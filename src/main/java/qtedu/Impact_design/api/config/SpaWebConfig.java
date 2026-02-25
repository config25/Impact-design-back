package qtedu.Impact_design.api.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaWebConfig {

    @RequestMapping(value = {"/teacher_login", "/admin_login"})
    public String forward() {
        return "forward:/index.html";
    }
}
