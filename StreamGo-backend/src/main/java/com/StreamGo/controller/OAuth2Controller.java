package com.StreamGo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;

@Controller
public class OAuth2Controller {

    @GetMapping("/auth/google-init")
    public void initGoogleAuth(@RequestParam("action") String action, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().setAttribute("oauth2_action", action);

        // 💡 Forzamos a Google a que siempre muestre la ventana de selección de cuenta y consentimiento
        response.sendRedirect("/oauth2/authorization/google?prompt=select_account");
    }

}
