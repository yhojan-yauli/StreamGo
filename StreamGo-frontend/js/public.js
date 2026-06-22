const publicMovies = [
    {
        titulo: "Avatar 2",
        estreno: "mayo 2026",
        imagen: "https://image.tmdb.org/t/p/w780/kyeqWdyUXW608qlYkRqosgbbJyK.jpg",
        descripcion: "La historia narra una nueva aventura en Pandora, con mundos visuales increíbles y una lucha familiar por sobrevivir.",
        video: "https://www.youtube.com/embed/d9MyW72ELq0"
    },
    {
        titulo: "Michael",
        estreno: "mayo 2026",
        imagen: "https://image.tmdb.org/t/p/w780/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
        descripcion: "Una historia inspirada en la vida de Michael Jackson, sus escenarios, su música y su camino a la fama.",
        video: "https://www.youtube.com/embed/dQw4w9WgXcQ"
    },
    {
        titulo: "Moana 2",
        estreno: "mayo 2026",
        imagen: "https://image.tmdb.org/t/p/w780/4YZpsylmjHbqeWzjKpUEF8gcLNW.jpg",
        descripcion: "Una nueva aventura animada llena de emoción, música, viaje y fantasía por el océano.",
        video: "https://www.youtube.com/embed/hDZ7y8RP5HE"
    }
];

function cargarListaPublica(listaPersonalizada = null) {
    const lista = document.getElementById("listaPublica");
    if (!lista) return;

    const peliculas = listaPersonalizada || publicMovies;

    lista.innerHTML = "";

    peliculas.forEach((movie, index) => {
        lista.innerHTML += `
            <article class="public-item">
                <img src="${movie.imagen}" alt="${movie.titulo}">

                <div>
                    <small>Header</small>
                    <h3>${movie.titulo}</h3>
                    <small>Estreno: ${movie.estreno}</small>
                    <p>${movie.descripcion}</p>
                </div>

                <button onclick="verPublico(${index})">ver</button>
            </article>
        `;
    });
}

function verPublico(index) {
    const movie = publicMovies[index];

    localStorage.setItem("publicMovie", JSON.stringify(movie));

    window.location.href = "reproducir-publico.html";
}

function cargarReproductorPublico() {
    const frame = document.getElementById("publicVideoFrame");
    if (!frame) return;

    const movie = JSON.parse(localStorage.getItem("publicMovie"));

    if (movie && movie.video) {
        frame.src = movie.video;
    }
}

function buscarPublico() {
    const input = document.getElementById("buscarPublico");
    if (!input) return;

    input.addEventListener("input", () => {
        const texto = input.value.toLowerCase();

        const filtrados = publicMovies.filter(movie =>
            movie.titulo.toLowerCase().includes(texto)
        );

        cargarListaPublica(filtrados);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    cargarListaPublica();
    cargarReproductorPublico();
    buscarPublico();
});