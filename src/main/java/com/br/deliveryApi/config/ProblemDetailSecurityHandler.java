package com.br.deliveryApi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Responde 401/403 no mesmo formato (RFC 9457) usado pelo restante da API.
 * Esses erros ocorrem no filtro de segurança, antes dos controllers,
 * por isso não passam pelo GlobalExceptionHandler.
 */
@Component
public class ProblemDetailSecurityHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        escrever(request, response, HttpStatus.UNAUTHORIZED, "Não autenticado",
                "Token de acesso ausente, inválido ou expirado");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escrever(request, response, HttpStatus.FORBIDDEN, "Acesso negado",
                "Você não tem permissão para acessar este recurso");
    }

    private void escrever(HttpServletRequest request, HttpServletResponse response,
                          HttpStatus status, String titulo, String detalhe) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = """
                {"type":"about:blank","title":"%s","status":%d,"detail":"%s","instance":"%s","timestamp":"%s"}"""
                .formatted(titulo, status.value(), detalhe, escapar(request.getRequestURI()), Instant.now());
        response.getWriter().write(json);
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
