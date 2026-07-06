package com.StreamGo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class NoticiaPortadaStorageService {

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final String NOTICIAS_DIR = "noticias";

    private static final Set<String> MIME_TYPES_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Map<String, String> EXTENSIONES_POR_MIME = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final Path uploadRoot;

    public NoticiaPortadaStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir
    ) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String guardarPortada(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        validarTamano(archivo);
        String contentType = validarContentType(archivo);
        validarFirmaArchivo(archivo, contentType);

        String extension = EXTENSIONES_POR_MIME.get(contentType);
        String nombreArchivo = UUID.randomUUID() + extension;
        Path directorioNoticias = uploadRoot.resolve(NOTICIAS_DIR).normalize();
        Path destino = directorioNoticias.resolve(nombreArchivo).normalize();

        if (!destino.startsWith(directorioNoticias)) {
            throw new RuntimeException("Ruta de archivo invalida");
        }

        try {
            Files.createDirectories(directorioNoticias);
            archivo.transferTo(destino);
            return "/uploads/" + NOTICIAS_DIR + "/" + nombreArchivo;
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo guardar la portada de la noticia");
        }
    }

    private void validarTamano(MultipartFile archivo) {
        if (archivo.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("La portada no puede superar 2MB");
        }
    }

    private String validarContentType(MultipartFile archivo) {
        String contentType = archivo.getContentType();

        if (contentType == null) {
            throw new RuntimeException("No se pudo identificar el tipo de archivo");
        }

        String normalizado = contentType.toLowerCase(Locale.ROOT);
        if (!MIME_TYPES_PERMITIDOS.contains(normalizado)) {
            throw new RuntimeException("La portada debe ser una imagen JPG, PNG o WEBP");
        }

        return normalizado;
    }

    private void validarFirmaArchivo(
            MultipartFile archivo,
            String contentType
    ) {
        byte[] header = new byte[12];

        try (InputStream inputStream = archivo.getInputStream()) {
            int bytesLeidos = inputStream.read(header);

            boolean firmaValida = switch (contentType) {
                case "image/jpeg" -> esJpeg(header, bytesLeidos);
                case "image/png" -> esPng(header, bytesLeidos);
                case "image/webp" -> esWebp(header, bytesLeidos);
                default -> false;
            };

            if (!firmaValida) {
                throw new RuntimeException("El archivo no coincide con una imagen valida");
            }
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo validar la portada de la noticia");
        }
    }

    private boolean esJpeg(byte[] header, int bytesLeidos) {
        return bytesLeidos >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF;
    }

    private boolean esPng(byte[] header, int bytesLeidos) {
        return bytesLeidos >= 8
                && (header[0] & 0xFF) == 0x89
                && header[1] == 0x50
                && header[2] == 0x4E
                && header[3] == 0x47
                && header[4] == 0x0D
                && header[5] == 0x0A
                && header[6] == 0x1A
                && header[7] == 0x0A;
    }

    private boolean esWebp(byte[] header, int bytesLeidos) {
        return bytesLeidos >= 12
                && header[0] == 0x52
                && header[1] == 0x49
                && header[2] == 0x46
                && header[3] == 0x46
                && header[8] == 0x57
                && header[9] == 0x45
                && header[10] == 0x42
                && header[11] == 0x50;
    }
}
