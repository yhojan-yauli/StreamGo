package com.StreamGo.security;

import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.UsuarioRepository;
import com.StreamGo.service.AuthService;
import com.StreamGo.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 💡 LEER LA ACCIÓN GUARDADA EN LA SESIÓN DEL SERVIDOR
        String action = (String) request.getSession().getAttribute("oauth2_action");
        boolean esFlujoRegistro = "register".equals(action);

        // Limpiamos la sesión para que no se quede guardada permanentemente
        request.getSession().removeAttribute("oauth2_action");

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        String targetUrl;

        if (esFlujoRegistro) {
            // === FLUJO DE REGISTRO ===
            if (usuarioOptional.isPresent()) {
                targetUrl = "http://localhost:4200/register?error=ya_existe";
            } else {
                // Registramos al usuario en MySQL con tu método tradicional
                authService.registerFromGoogle(email, name);

                // ❌ NO GENERAMOS TOKEN AQUÍ.
                // 💡 Redirigimos directo al Login de Angular con un parámetro de éxito
                targetUrl = "http://localhost:4200/login?registro=exitoso";
            }
        } else {
            // === FLUJO DE LOGIN ===
            // (Este bloque se queda exactamente igual como ya lo tienes)
            if (usuarioOptional.isEmpty()) {
                targetUrl = "http://localhost:4200/login?error=usuario_no_registrado";
            } else {
                Usuario usuario = usuarioOptional.get();
                usuario.setUltimoAcceso(java.time.LocalDateTime.now());
                usuarioRepository.save(usuario);

                String token = jwtService.generateTokenFromOAuth2(usuario.getEmail(), usuario.getRol().name());
                targetUrl = UriComponentsBuilder.fromUriString("http://localhost:4200/oauth2/redirect")
                        .queryParam("token", token)
                        .build().toUriString();
            }
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
