package com.StreamGo.dto.query;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class NoticiaQuery {

    public static final String ESTADO_TODOS = "todos";
    public static final String ESTADO_FIJADAS = "fijadas";
    public static final String ESTADO_NORMALES = "normales";

    public static final String SORT_RECIENTES = "recientes";
    public static final String SORT_REACCIONES = "reacciones";
    public static final String SORT_TITULO = "titulo";

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 50;

    private static final Set<String> ESTADOS_PERMITIDOS = Set.of(
            ESTADO_TODOS,
            ESTADO_FIJADAS,
            ESTADO_NORMALES
    );

    private static final Set<String> SORTS_PERMITIDOS = Set.of(
            SORT_RECIENTES,
            SORT_REACCIONES,
            SORT_TITULO
    );

    private String search;
    private String estado;
    private String sort;
    private int page;
    private int size;

    public static NoticiaQuery of(
            String search,
            String estado,
            String sort,
            Integer page,
            Integer size
    ) {
        String searchNormalizado = normalizarTexto(search);
        String estadoNormalizado = normalizarValor(estado, ESTADO_TODOS);
        String sortNormalizado = normalizarValor(sort, SORT_RECIENTES);
        int pageNormalizada = page == null ? DEFAULT_PAGE : page;
        int sizeNormalizado = size == null ? DEFAULT_SIZE : size;

        validarEstado(estadoNormalizado);
        validarSort(sortNormalizado);
        validarPage(pageNormalizada);
        validarSize(sizeNormalizado);

        return NoticiaQuery.builder()
                .search(searchNormalizado)
                .estado(estadoNormalizado)
                .sort(sortNormalizado)
                .page(pageNormalizada)
                .size(sizeNormalizado)
                .build();
    }

    public int getOffset() {
        return page * size;
    }

    public boolean tieneBusqueda() {
        return search != null && !search.isBlank();
    }

    public boolean esEstadoFijadas() {
        return ESTADO_FIJADAS.equals(estado);
    }

    public boolean esEstadoNormales() {
        return ESTADO_NORMALES.equals(estado);
    }

    private static String normalizarTexto(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return valor.trim();
    }

    private static String normalizarValor(String valor, String valorPorDefecto) {
        if (valor == null || valor.trim().isEmpty()) {
            return valorPorDefecto;
        }

        return valor.trim().toLowerCase();
    }

    private static void validarEstado(String estado) {
        if (!ESTADOS_PERMITIDOS.contains(estado)) {
            throw new RuntimeException("Estado de noticias no valido");
        }
    }

    private static void validarSort(String sort) {
        if (!SORTS_PERMITIDOS.contains(sort)) {
            throw new RuntimeException("Orden de noticias no valido");
        }
    }

    private static void validarPage(int page) {
        if (page < 0) {
            throw new RuntimeException("La pagina no puede ser negativa");
        }
    }

    private static void validarSize(int size) {
        if (size <= 0) {
            throw new RuntimeException("El tamano de pagina debe ser mayor a cero");
        }
        if (size > MAX_SIZE) {
            throw new RuntimeException("El tamano de pagina no puede superar " + MAX_SIZE);
        }
    }
}
