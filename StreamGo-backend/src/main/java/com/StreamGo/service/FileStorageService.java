package com.StreamGo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class FileStorageService {

    private static final String URL_PREFIX = "/uploads/";

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.server.url:http://localhost:8081}")
    private String fileServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String sanitizarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) return "sin-titulo";
        String sanitized = titulo.trim()
                .toLowerCase()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return sanitized.isEmpty() ? "sin-titulo" : sanitized;
    }

    public String guardarImagen(MultipartFile file, String nombreBase, String prefijo) {
        if (file == null || file.isEmpty()) return null;
        String extension = obtenerExtension(file.getOriginalFilename());
        String nombre = String.format("%s-%s%s", nombreBase, prefijo, extension);
        return guardar(file, "images", nombre);
    }

    public String guardarVideo(MultipartFile file, String nombreBase) {
        if (file == null || file.isEmpty()) return null;
        String extension = obtenerExtension(file.getOriginalFilename());
        String nombre = String.format("%s-video%s", nombreBase, extension);
        return guardar(file, "videos", nombre);
    }

    public void eliminarArchivo(String url) {
        if (url == null || url.isBlank() || !url.startsWith(URL_PREFIX)) return;
        
        try {
            String deleteUrl = fileServerUrl + "/api/files/delete?url=" + url;
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("ngrok-skip-browser-warning", "true");
            
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            restTemplate.exchange(deleteUrl, org.springframework.http.HttpMethod.DELETE, requestEntity, Void.class);
            
            log.debug("Archivo eliminado en servidor remoto: {}", url);
        } catch (Exception e) {
            log.warn("No se pudo eliminar archivo {}: {}", url, e.getMessage());
        }
    }

    private String guardar(MultipartFile file, String subDir, String nombre) {
        try {
            // Preparar la solicitud multipart
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // Convertir MultipartFile a ByteArrayResource
            byte[] fileBytes = file.getBytes();
            ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return nombre;
                }
            };
            
            body.add("file", fileResource);
            body.add("subDir", subDir);
            body.add("fileName", nombre);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            // HEADER CRUCIAL PARA NGROK
            headers.add("ngrok-skip-browser-warning", "true");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Enviar al servidor de archivos
            String uploadUrl = fileServerUrl + "/api/files/upload";
            
            log.info("Enviando archivo a: {}", uploadUrl);
            
            ResponseEntity<FileUploadResponse> response = restTemplate.postForEntity(
                    uploadUrl,
                    requestEntity,
                    FileUploadResponse.class
            );

            if (response.getBody() != null && response.getBody().getUrl() != null) {
                // Reemplazar la URL local con la URL pública (ngrok)
                String publicUrl = response.getBody().getUrl()
                        .replace("http://localhost:8081", fileServerUrl);
                log.debug("Archivo guardado en servidor remoto: {}", publicUrl);
                return publicUrl;
            }

            throw new RuntimeException("Error al guardar archivo en servidor remoto");
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo: " + nombre, e);
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) return "";
        int idx = nombreOriginal.lastIndexOf('.');
        return nombreOriginal.substring(idx).toLowerCase();
    }

    // Clase para deserializar la respuesta
    public static class FileUploadResponse {
        private String url;
        private String localPath;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getLocalPath() { return localPath; }
        public void setLocalPath(String localPath) { this.localPath = localPath; }
    }
}
