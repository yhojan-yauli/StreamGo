package com.StreamGo.security;

import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.UsuarioRepository;
import com.StreamGo.service.AuthService; // Inyectamos tu servicio
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
    private final AuthService authService; // <- Inyectado

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Capturamos el parámetro "state" enviado desde Angular
        String action = request.getParameter("state");

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        String targetUrl;

        if ("register".equals(action)) {
            // === FLUJO DE REGISTRO ===
            if (usuarioOptional.isPresent()) {
                // Si ya existe, lo mandamos al register con error de duplicado
                targetUrl = "http://localhost:4200/register?error=ya_existe";
            } else {
                // Si no existe, lo creamos usando tu nuevo método en AuthService
                Usuario nuevoUsuario = authService.registerFromGoogle(email, name);
                String token = jwtService.generateTokenFromOAuth2(nuevoUsuario.getEmail(), nuevoUsuario.getRol().name());

                targetUrl = UriComponentsBuilder.fromUriString("http://localhost:4200/oauth2/redirect")
                        .queryParam("token", token)
                        .build().toUriString();
            }
        } else {
            // === FLUJO DE LOGIN (Tus reglas estrictas) ===
            if (usuarioOptional.isEmpty()) {
                // No existe -> Error y rebote
                targetUrl = "http://localhost:4200/login?error=usuario_no_registrado";
            } else {
                // Sí existe -> Login exitoso
                Usuario usuario = usuarioOptional.get();

                // Actualizas el último acceso como en tu login clásico
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