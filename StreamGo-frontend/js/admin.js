async function cargarUsuariosAdmin() {
    const lista = document.getElementById("listaUsuarios");
    if (!lista) return;

    try {
        const usuarios = await apiFetch("/admin/clientes");

        lista.innerHTML = "";

        usuarios.forEach(usuario => {
            lista.innerHTML += `
                <div class="fila-usuario">
                    <span class="avatar-mini">A</span>
                    <span>${usuario.nombre || "Usuario ########"}</span>
                    <span>${usuario.email || "usuario@gmail.com"}</span>
                    <span>Cliente</span>
                    <button class="btn-editar">Editar</button>
                    <span class="${usuario.tieneSuscripcion ? "estado-si" : "estado-no"}">
                        ${usuario.tieneSuscripcion ? "s" : "n"}
                    </span>
                </div>
            `;
        });

    } catch (error) {
        console.error("Error usuarios:", error);
    }
}

async function agregarContenidoAdmin() {
    const titulo = document.getElementById("titulo").value;
    const fechaEstreno = document.getElementById("fechaEstreno").value;
    const categoria = document.getElementById("categoria").value;
    const descripcion = document.getElementById("descripcion").value;
    const imagenUrl = document.getElementById("imagenUrl").value;
    const videoUrl = document.getElementById("videoUrl").value;

    try {
        await apiFetch("/admin/contenidos", {
            method: "POST",
            body: JSON.stringify({
                titulo,
                descripcion,
                categoria,
                tipoContenido: "PELICULA",
                imagenUrl,
                bannerUrl: imagenUrl,
                videoUrl,
                fechaEstreno,
                duracionMinutos: 120,
                gratuito: false,
                recomendado: true,
                tendencia: false,
                estado: "ACTIVO"
            })
        });

        alert("Contenido agregado");
        location.reload();

    } catch (error) {
        alert("Error al agregar contenido");
        console.error(error);
    }
}

async function cargarContenidoAdmin() {
    const lista = document.getElementById("listaContenidoAdmin");
    if (!lista) return;

    try {
        const contenidos = await apiFetch("/admin/contenidos");

        lista.innerHTML = "";

        contenidos.forEach(item => {
            lista.innerHTML += `
                <div class="item-contenido-admin">
                    <img src="${item.imagenUrl || "../../assets/imagenes/background.png"}">
                    <span>${item.titulo}</span>
                    <button>Editar</button>
                    <span class="info-icon">i</span>
                </div>
            `;
        });

    } catch (error) {
        console.error("Error contenido:", error);
    }
}

async function cargarPeticionesAdmin() {
    const lista = document.getElementById("rankingPeticiones");
    if (!lista) return;

    try {
        const peticiones = await apiFetch("/admin/peticiones/ranking");

        lista.innerHTML = "";

        peticiones.forEach(item => {
            lista.innerHTML += `
                <div class="fila-peticion-admin">
                    <div class="img-peticion-demo"></div>
                    <span>${item.titulo}</span>
                    <div class="barra-voto">
                        <div style="width:${Math.min(item.totalVotos * 10, 100)}%"></div>
                    </div>
                    <span>%</span>
                    <button>Publicar</button>
                </div>
            `;
        });

    } catch (error) {
        console.error("Error peticiones:", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    cargarUsuariosAdmin();
    cargarContenidoAdmin();
    cargarPeticionesAdmin();
});