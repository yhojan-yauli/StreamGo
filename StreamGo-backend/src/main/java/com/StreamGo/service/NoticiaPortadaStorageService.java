package com.StreamGo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class NoticiaPortadaStorageService {

    private static final long MAX_FILE_SIZE = 2L * 1024L * 1024L;
    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Path uploadRoot;

    public NoticiaPortadaStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        validarTamano(archivo);
        String contentType = validarContentType(archivo);
        String extension = obtenerExtensionValidada(archivo, contentType);
        byte[] bytes = obtenerBytes(archivo);
        validarFirma(bytes, contentType);

        Path noticiasDir = uploadRoot.resolve("noticias").normalize();
        validarDirectorioDestino(noticiasDir);

        String nombreArchivo = UUID.randomUUID() + "." + extension;
        Path destino = noticiasDir.resolve(nombreArchivo).normalize();

        try {
            Files.createDirectories(noticiasDir);
            Files.write(destino, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo guardar la portada de la noticia", ex);
        }

        return "/uploads/noticias/" + nombreArchivo;
    }

    private void validarTamano(MultipartFile archivo) {
        if (archivo.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("La portada no puede superar 2MB");
        }
    }

    private String validarContentType(MultipartFile archivo) {
        String contentType = archivo.getContentType();
        if (contentType == null || !CONTENT_TYPES_PERMITIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new RuntimeException("La portada debe ser una imagen jpeg, png o webp");
        }

        return contentType.toLowerCase(Locale.ROOT);
    }

    private String obtenerExtensionValidada(MultipartFile archivo, String contentType) {
        String extension = obtenerExtensionOriginal(archivo.getOriginalFilename());
        String extensionPorContentType = extensionPorContentType(contentType);

        if (extension == null) {
            return extensionPorContentType;
        }

        boolean extensionValida = ("image/jpeg".equals(contentType) && ("jpg".equals(extension) || "jpeg".equals(extension)))
                || extensionPorContentType.equals(extension);

        if (!extensionValida) {
            throw new RuntimeException("La extension de la portada no coincide con su tipo");
        }

        return extension;
    }

    private String obtenerExtensionOriginal(String nombreOriginal) {
        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            return null;
        }

        int punto = nombreOriginal.lastIndexOf('.');
        if (punto < 0 || punto == nombreOriginal.length() - 1) {
            return null;
        }

        return nombreOriginal.substring(punto + 1).toLowerCase(Locale.ROOT);
    }

    private String extensionPorContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new RuntimeException("Tipo de portada no soportado");
        };
    }

    private byte[] obtenerBytes(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo leer la portada de la noticia", ex);
        }
    }

    private void validarFirma(byte[] bytes, String contentType) {
        boolean firmaValida = switch (contentType) {
            case "image/jpeg" -> esJpeg(bytes);
            case "image/png" -> esPng(bytes);
            case "image/webp" -> esWebp(bytes);
            default -> false;
        };

        if (!firmaValida) {
            throw new RuntimeException("El archivo de portada no es una imagen valida");
        }
    }

    private boolean esJpeg(byte[] bytes) {
        return bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF;
    }

    private boolean esPng(byte[] bytes) {
        byte[] firmaPng = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A
        };

        if (bytes.length < firmaPng.length) {
            return false;
        }

        for (int i = 0; i < firmaPng.length; i++) {
            if (bytes[i] != firmaPng[i]) {
                return false;
            }
        }

        return true;
    }

    private boolean esWebp(byte[] bytes) {
        if (bytes.length < 12) {
            return false;
        }

        String riff = new String(bytes, 0, 4, StandardCharsets.US_ASCII);
        String webp = new String(bytes, 8, 4, StandardCharsets.US_ASCII);
        return "RIFF".equals(riff) && "WEBP".equals(webp);
    }

    private void validarDirectorioDestino(Path noticiasDir) {
        if (!noticiasDir.startsWith(uploadRoot)) {
            throw new RuntimeException("Directorio de portadas no valido");
        }
    }
}
