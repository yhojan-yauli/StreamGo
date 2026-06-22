const categorias = [
    "Acción",
    "Aventura",
    "Comedia",
    "Romantico",
    "Terror",
    "Animes"
];

const contenidosDemo = {
    recomendados: {
        "Acción": [
            {
                titulo: "Avatar 2",
                imagen: "https://image.tmdb.org/t/p/w780/kyeqWdyUXW608qlYkRqosgbbJyK.jpg",
                video: "https://www.youtube.com/embed/d9MyW72ELq0",
                descripcion: "Jake Sully vive con su nueva familia formada en el planeta de Pandora."
            },
            {
                titulo: "The Hobbit",
                imagen: "https://image.tmdb.org/t/p/w780/5i0JH5NgLJSv9f6QRdEgn8VmT4i.jpg",
                video: "https://www.youtube.com/embed/SDnYMbYB-nU",
                descripcion: "Bilbo Baggins inicia una aventura inesperada junto a un grupo de enanos."
            },
            {
                titulo: "Mortal combat",
                imagen: "https://image.tmdb.org/t/p/w780/9yBVqNruk6Ykrwc32qrK2TIE5xw.jpg",
                video: "https://www.youtube.com/embed/NYH2sLid0Zc",
                descripcion: "Guerreros elegidos se enfrentan en un torneo que decidirá el destino del mundo."
            },
            {
                titulo: "X-Men",
                imagen: "https://image.tmdb.org/t/p/w780/2k9tBql5GYH328Krj66tDT9LtFZ.jpg",
                video: "https://www.youtube.com/embed/PfBVIHgQbYk",
                descripcion: "Un grupo de mutantes lucha por proteger a la humanidad y sobrevivir al rechazo."
            },
            {
                titulo: "Spider Man",
                imagen: "https://image.tmdb.org/t/p/w780/14QbnygCuTO0vl7CAFmPf1fgZfV.jpg",
                video: "https://www.youtube.com/embed/JfVOs4VSpmA",
                descripcion: "Peter Parker enfrenta nuevos peligros mientras protege a quienes ama."
            }
        ],
        "Aventura": [
            {
                titulo: "Moana 2",
                imagen: "https://image.tmdb.org/t/p/w780/4YZpsylmjHbqeWzjKpUEF8gcLNW.jpg",
                video: "https://www.youtube.com/embed/hDZ7y8RP5HE",
                descripcion: "Moana inicia una nueva aventura por el océano para descubrir nuevos caminos."
            },
            {
                titulo: "Avatar",
                imagen: "https://image.tmdb.org/t/p/w780/kyeqWdyUXW608qlYkRqosgbbJyK.jpg",
                video: "https://www.youtube.com/embed/5PSNL1qE6VY",
                descripcion: "Un exmarine llega a Pandora y descubre un mundo lleno de vida y conflicto."
            },
            {
                titulo: "Ver Michael (2026)",
                imagen: "https://image.tmdb.org/t/p/w780/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una historia musical inspirada en los grandes escenarios y la fama."
            },
            {
                titulo: "X-Men",
                imagen: "https://image.tmdb.org/t/p/w780/2k9tBql5GYH328Krj66tDT9LtFZ.jpg",
                video: "https://www.youtube.com/embed/PfBVIHgQbYk",
                descripcion: "Los mutantes enfrentan amenazas que ponen en riesgo su existencia."
            },
            {
                titulo: "Avatar 2",
                imagen: "https://image.tmdb.org/t/p/w780/kyeqWdyUXW608qlYkRqosgbbJyK.jpg",
                video: "https://www.youtube.com/embed/d9MyW72ELq0",
                descripcion: "La familia Sully debe proteger su hogar y adaptarse a nuevas tribus."
            }
        ],
        "Comedia": [
            {
                titulo: "No se aceptan devoluciones",
                imagen: "https://images.unsplash.com/photo-1527224857830-43a7acc85260?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una comedia familiar con momentos emotivos y situaciones inesperadas."
            },
            {
                titulo: "Stand Up",
                imagen: "https://images.unsplash.com/photo-1527224857830-43a7acc85260?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Un especial de comedia lleno de risas y momentos improvisados."
            },
            {
                titulo: "Comedia Central",
                imagen: "https://images.unsplash.com/photo-1603190287605-e6ade32fa852?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Historias ligeras y divertidas para pasar un buen momento."
            },
            {
                titulo: "Show Comedy",
                imagen: "https://images.unsplash.com/photo-1527224857830-43a7acc85260?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Un show de humor con escenas rápidas y personajes curiosos."
            },
            {
                titulo: "Devoluciones",
                imagen: "https://images.unsplash.com/photo-1603190287605-e6ade32fa852?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una historia cómica sobre errores, familia y segundas oportunidades."
            }
        ],
        "Romantico": [
            {
                titulo: "Love in Bloom",
                imagen: "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una historia romántica sobre nuevos comienzos y decisiones difíciles."
            },
            {
                titulo: "After",
                imagen: "https://images.unsplash.com/photo-1522673607200-164d1b6ce486?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Dos personas descubren que el amor también puede cambiar sus planes."
            },
            {
                titulo: "Titanic",
                imagen: "https://image.tmdb.org/t/p/w780/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg",
                video: "https://www.youtube.com/embed/kVrqfYjkTdQ",
                descripcion: "Un romance inolvidable ocurre durante el viaje del Titanic."
            },
            {
                titulo: "Love",
                imagen: "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una historia sobre encuentros, distancia y sentimientos inesperados."
            },
            {
                titulo: "Romance",
                imagen: "https://images.unsplash.com/photo-1516585427167-9f4af9627e6c?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una película romántica con momentos suaves y emocionales."
            }
        ],
        "Terror": [
            {
                titulo: "Fear Street",
                imagen: "https://images.unsplash.com/photo-1509248961158-e54f6934749c?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Un oscuro secreto amenaza a un grupo de jóvenes en su ciudad."
            },
            {
                titulo: "Until Dawn",
                imagen: "https://images.unsplash.com/photo-1505635552518-3448ff116af3?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una noche de miedo se convierte en una lucha por sobrevivir."
            },
            {
                titulo: "Silent Hill (2026)",
                imagen: "https://images.unsplash.com/photo-1516410529446-2c777cb7366d?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Un pueblo cubierto de niebla guarda secretos difíciles de enfrentar."
            },
            {
                titulo: "Fear Street 2",
                imagen: "https://images.unsplash.com/photo-1509248961158-e54f6934749c?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "El terror continúa con nuevas víctimas y un misterio sin resolver."
            },
            {
                titulo: "Terror",
                imagen: "https://images.unsplash.com/photo-1509248961158-e54f6934749c?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una película oscura con suspenso, miedo y giros inesperados."
            }
        ],
        "Animes": [
            {
                titulo: "Anime 1",
                imagen: "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una aventura animada llena de acción y personajes únicos."
            },
            {
                titulo: "Anime 2",
                imagen: "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Una historia de fantasía y emoción con estilo anime."
            },
            {
                titulo: "Anime Central",
                imagen: "https://images.unsplash.com/photo-1578632292335-df3abbb0d586?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Un grupo de jóvenes descubre poderes y nuevos enemigos."
            },
            {
                titulo: "Anime 3",
                imagen: "https://images.unsplash.com/photo-1601850494422-3cf14624b0b3?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Acción, amistad y aventura en un mundo animado."
            },
            {
                titulo: "Anime 4",
                imagen: "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=1200",
                video: "https://www.youtube.com/embed/dQw4w9WgXcQ",
                descripcion: "Un anime con batallas, viajes y crecimiento personal."
            }
        ]
    },
    tendencias: {}
};

contenidosDemo.tendencias = JSON.parse(JSON.stringify(contenidosDemo.recomendados));

let categoriaActual = "Acción";
let tipoActual = "recomendados";
let indiceCentro = 2;
let autoCarousel;

/* INICIO / CARRUSEL */

function iniciarInicioCliente() {
    if (!document.getElementById("carouselU")) return;

    pintarCategorias();
    pintarCarousel();
    iniciarAutoCarousel();

    document.getElementById("btnRecomendados").addEventListener("click", () => {
        tipoActual = "recomendados";
        indiceCentro = 2;
        cambiarTab();
        pintarCarousel();
        iniciarAutoCarousel();
    });

    document.getElementById("btnTendencias").addEventListener("click", () => {
        tipoActual = "tendencias";
        indiceCentro = 2;
        cambiarTab();
        pintarCarousel();
        iniciarAutoCarousel();
    });

    document.getElementById("buscarContenido").addEventListener("input", buscarEnCarousel);
    document.getElementById("carouselU").addEventListener("wheel", moverConScroll);
}

function pintarCategorias() {
    const contenedor = document.getElementById("categoriasMenu");
    if (!contenedor) return;

    contenedor.innerHTML = "";

    categorias.forEach(categoria => {
        const span = document.createElement("span");
        span.textContent = categoria;

        if (categoria === categoriaActual) {
            span.classList.add("categoria-activa");
        }

        span.addEventListener("click", () => {
            categoriaActual = categoria;
            indiceCentro = 2;

            document.getElementById("tituloPagina").textContent = "Categorias";

            pintarCategorias();
            pintarCarousel();
            iniciarAutoCarousel();
        });

        contenedor.appendChild(span);
    });
}

function cambiarTab() {
    document.getElementById("btnRecomendados").classList.toggle("tab-activo", tipoActual === "recomendados");
    document.getElementById("btnTendencias").classList.toggle("tab-activo", tipoActual === "tendencias");
}

function pintarCarousel(listaPersonalizada = null) {
    const carousel = document.getElementById("carouselU");
    const titulo = document.getElementById("tituloCardCentro");

    if (!carousel || !titulo) return;

    const lista = listaPersonalizada || contenidosDemo[tipoActual][categoriaActual];

    carousel.innerHTML = "";

    if (!lista || lista.length === 0) {
        titulo.textContent = "Sin contenido";
        return;
    }

    const visibles = obtenerCincoElementos(lista);

    visibles.forEach((item, index) => {
        const card = document.createElement("div");
        card.className = `carousel-card pos-${index}`;

        card.innerHTML = `
            <img src="${item.imagen}" alt="${item.titulo}">
        `;

        card.addEventListener("click", () => {
            localStorage.setItem("contenidoSeleccionado", JSON.stringify(item));
            window.location.href = "reproducir.html";
        });

        carousel.appendChild(card);
    });

    titulo.textContent = visibles[2]?.titulo || "";
}

function obtenerCincoElementos(lista) {
    const resultado = [];

    for (let i = -2; i <= 2; i++) {
        let index = indiceCentro + i;

        if (index < 0) index = lista.length + index;
        if (index >= lista.length) index = index - lista.length;

        resultado.push(lista[index]);
    }

    return resultado;
}

function moverConScroll(event) {
    event.preventDefault();

    const lista = contenidosDemo[tipoActual][categoriaActual];

    if (event.deltaY > 0) {
        indiceCentro++;
    } else {
        indiceCentro--;
    }

    if (indiceCentro >= lista.length) indiceCentro = 0;
    if (indiceCentro < 0) indiceCentro = lista.length - 1;

    pintarCarousel();
    iniciarAutoCarousel();
}

function buscarEnCarousel() {
    const texto = document.getElementById("buscarContenido").value.toLowerCase();
    const lista = contenidosDemo[tipoActual][categoriaActual];

    if (!texto) {
        pintarCarousel();
        return;
    }

    const filtrados = lista.filter(item =>
        item.titulo.toLowerCase().includes(texto)
    );

    indiceCentro = 2;
    pintarCarousel(filtrados);
}

function contenidoAleatorio() {
    const lista = contenidosDemo[tipoActual][categoriaActual];

    indiceCentro = Math.floor(Math.random() * lista.length);

    pintarCarousel();
    iniciarAutoCarousel();
}

function iniciarAutoCarousel() {
    clearInterval(autoCarousel);

    autoCarousel = setInterval(() => {
        const lista = contenidosDemo[tipoActual][categoriaActual];

        indiceCentro++;

        if (indiceCentro >= lista.length) {
            indiceCentro = 0;
        }

        pintarCarousel();
    }, 3000);
}

/* PETICIONES */

function agregarDeseado(boton) {
    boton.textContent = "✓";
    boton.classList.add("marcado");
}

/* REPRODUCTOR */

function cargarReproductor() {
    const titulo = document.getElementById("tituloRepro");
    const descripcion = document.getElementById("descripcionRepro");
    const frame = document.getElementById("videoFrame");

    if (!titulo || !descripcion || !frame) return;

    const contenido = JSON.parse(localStorage.getItem("contenidoSeleccionado"));

    if (!contenido) return;

    titulo.textContent = contenido.titulo;
    descripcion.textContent = contenido.descripcion || "Contenido disponible en StreamGO.";
    frame.src = contenido.video || "https://www.youtube.com/embed/dQw4w9WgXcQ";
}

/* NOTICIAS */

function abrirTrailer(url) {
    window.open(url, "_blank");
}

function copiarLink() {
    navigator.clipboard.writeText(window.location.href);
    alert("Link copiado");
}

function compartirPost() {
    const url = window.location.href;

    if (navigator.share) {
        navigator.share({
            title: "StreamGO Noticias",
            text: "Mira esta noticia en StreamGO",
            url: url
        });
    } else {
        navigator.clipboard.writeText(url);
        alert("Link copiado para compartir");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    iniciarInicioCliente();
    cargarReproductor();
});

const peticionesCliente = [
    {
        id: 1,
        titulo: "Avatar 2",
        estreno: "mayo 2026",
        imagen: "https://image.tmdb.org/t/p/w780/kyeqWdyUXW608qlYkRqosgbbJyK.jpg",
        descripcion: "Película solicitada por la comunidad para ser agregada próximamente."
    },
    {
        id: 2,
        titulo: "Michael",
        estreno: "mayo 2026",
        imagen: "https://image.tmdb.org/t/p/w780/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
        descripcion: "Historia basada en la vida de Michael Jackson y su carrera musical."
    },
    {
        id: 3,
        titulo: "Moana 2",
        estreno: "mayo 2026",
        imagen: "https://image.tmdb.org/t/p/w780/4YZpsylmjHbqeWzjKpUEF8gcLNW.jpg",
        descripcion: "Nueva aventura animada solicitada por los usuarios."
    }
];

function cargarPeticionesCliente() {

    const contenedor = document.getElementById("listaPeticionesCliente");

    if (!contenedor) return;

    const deseados =
        JSON.parse(localStorage.getItem("deseadosPeticiones")) || [];

    contenedor.innerHTML = "";

    peticionesCliente.forEach(item => {

        const marcado = deseados.includes(item.id);

        contenedor.innerHTML += `
            <div class="peticion-item">

                <img src="${item.imagen}" alt="${item.titulo}">

                <div class="peticion-info">
                    <h3>${item.titulo}</h3>
                    <small>Estreno: ${item.estreno}</small>

                    <p>
                        ${item.descripcion}
                    </p>
                </div>

                <button
                    class="btn-deseado ${marcado ? "activo" : ""}"
                    onclick="toggleDeseado(${item.id})">

                    ${marcado ? "✓" : "+"}

                </button>

            </div>
        `;
    });
}

function toggleDeseado(id) {

    let deseados =
        JSON.parse(localStorage.getItem("deseadosPeticiones")) || [];

    if (deseados.includes(id)) {

        deseados = deseados.filter(item => item !== id);

    } else {

        deseados.push(id);
    }

    localStorage.setItem(
        "deseadosPeticiones",
        JSON.stringify(deseados)
    );

    cargarPeticionesCliente();
}

document.addEventListener("DOMContentLoaded", () => {

    cargarPeticionesCliente();

});