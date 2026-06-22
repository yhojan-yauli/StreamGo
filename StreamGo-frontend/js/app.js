const API_URL = "http://127.0.0.1:8080";

function getToken() {
    return localStorage.getItem("token");
}

function guardarToken(token) {
    localStorage.setItem("token", token);
}

function cerrarSesion() {
    localStorage.removeItem("token");
    window.location.href = "../auth/login.html";
}

async function apiFetch(endpoint, options = {}) {
    const token = getToken();

    const headers = {
        "Content-Type": "application/json",
        ...options.headers
    };

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers
    });

    let data = null;

    try {
        data = await response.json();
    } catch (error) {
        data = null;
    }

    if (!response.ok) {
        console.error("Error API:", {
            status: response.status,
            endpoint,
            data
        });

        throw new Error(data?.mensaje || "Error en la petición");
    }

    return data;
}

async function login() {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!email || !password) {
        alert("Completa el correo y la contraseña");
        return;
    }

    try {
        const data = await apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify({ email, password })
        });

        if (!data.token) {
            alert("El backend no devolvió token");
            return;
        }

        guardarToken(data.token);

        let rol = "";

        try {
            const payload = JSON.parse(atob(data.token.split(".")[1]));
            rol = payload.rol || payload.role || payload.authorities || "";
            console.log("Payload JWT:", payload);
        } catch (error) {
            console.warn("No se pudo leer el rol del token");
        }

        if (String(rol).toUpperCase().includes("ADMIN") || email.toLowerCase().includes("admin")) {
            window.location.href = "../admin/dashboard.html";
        } else {
            window.location.href = "../cliente/inicio.html";
        }

    } catch (error) {
        console.error("Error login:", error);
        alert("No se pudo iniciar sesión");
    }
}

async function registrar() {
    const nombre = document.getElementById("nombre").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!nombre || !email || !password) {
        alert("Completa todos los campos");
        return;
    }

    try {
        const data = await apiFetch("/auth/register", {
            method: "POST",
            body: JSON.stringify({ nombre, email, password })
        });

        alert(data?.mensaje || "Usuario registrado correctamente");
        window.location.href = "login.html";

    } catch (error) {
        console.error("Error registro:", error);
        alert("No se pudo registrar el usuario");
    }
}

async function cargarSidebarAdmin() {
    const contenedor = document.getElementById("sidebar-container");
    if (!contenedor) return;

    const response = await fetch("../../components/sidebar-admin.html");
    const html = await response.text();

    contenedor.innerHTML = html;

    const paginaActual = window.location.pathname.split("/").pop();

    contenedor.querySelectorAll("a").forEach(link => {
        const href = link.getAttribute("href");

        if (href && href.includes(paginaActual)) {
            link.classList.add("active");
        }
    });
}

async function cargarNavbarCliente() {
    const contenedor = document.getElementById("navbar-container");
    if (!contenedor) return;

    const response = await fetch("../../components/navbar-cliente.html");
    const html = await response.text();

    contenedor.innerHTML = html;

    const paginaActual = window.location.pathname.split("/").pop();

    contenedor.querySelectorAll("a").forEach(link => {
        const href = link.getAttribute("href");

        if (href && href.includes(paginaActual)) {
            link.classList.add("active");
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    cargarSidebarAdmin();
    cargarNavbarCliente();
});