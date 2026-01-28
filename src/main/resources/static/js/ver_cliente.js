// ==================== CONFIGURACIÓN ====================
const CLIENTES_POR_PAGINA = 60; // Cambia este número según necesites

// ==================== VARIABLES GLOBALES ====================
let todosLosClientes = [];
let clientesFiltrados = [];
let paginaActual = 1;

// ==================== INICIALIZACIÓN ====================
document.addEventListener('DOMContentLoaded', function() {
    cargarClientesDesdeDOM();
    inicializarEventos();
    actualizarVista();
});

// ==================== CARGAR DATOS ====================
function cargarClientesDesdeDOM() {
    const filas = document.querySelectorAll('#tablaClientes tbody tr');
    
    todosLosClientes = Array.from(filas).map(fila => ({
        id: fila.dataset.id || fila.cells[0].textContent,
        nombre: fila.dataset.nombre || fila.cells[1].textContent,
        apellido: fila.dataset.apellido || fila.cells[2].textContent,
        telefono: fila.dataset.telefono || fila.cells[3].textContent,
        email: fila.dataset.email || fila.cells[4].textContent,
        direccion: fila.dataset.direccion || fila.cells[5].textContent,
        idAdmin: fila.dataset.admin || fila.cells[6].textContent
    }));
    
    clientesFiltrados = [...todosLosClientes];
}

// ==================== EVENTOS ====================
function inicializarEventos() {
    const searchInput = document.getElementById('searchInput');
    
    // Buscar al presionar Enter
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            buscarCliente();
        }
    });
}

// ==================== RENDERIZADO ====================
function renderizarTabla() {
    const inicio = (paginaActual - 1) * CLIENTES_POR_PAGINA;
    const fin = inicio + CLIENTES_POR_PAGINA;
    const clientesPagina = clientesFiltrados.slice(inicio, fin);
    
    const tbody = document.getElementById('tablaBody');
    const noResults = document.getElementById('noResults');
    
    // Si no hay resultados
    if (clientesPagina.length === 0) {
        tbody.innerHTML = '';
        noResults.style.display = 'block';
        document.getElementById('resultsInfo').textContent = '';
        document.getElementById('paginacion').innerHTML = '';
        return;
    }
    
    noResults.style.display = 'none';
    
    // Renderizar filas
    tbody.innerHTML = clientesPagina.map(cliente => `
        <tr>
            <td>${cliente.id}</td>
            <td>${cliente.nombre}</td>
            <td>${cliente.apellido}</td>
            <td>${cliente.telefono}</td>
            <td>${cliente.email}</td>
            <td>${cliente.direccion}</td>
            <td>${cliente.idAdmin}</td>
        </tr>
    `).join('');
    
    // Info de resultados
    const total = clientesFiltrados.length;
    document.getElementById('resultsInfo').textContent = 
        `Mostrando ${inicio + 1}-${Math.min(fin, total)} de ${total} cliente${total !== 1 ? 's' : ''}`;
}

function renderizarPaginacion() {
    const totalPaginas = Math.ceil(clientesFiltrados.length / CLIENTES_POR_PAGINA);
    const paginacion = document.getElementById('paginacion');
    
    if (totalPaginas <= 1) {
        paginacion.innerHTML = '';
        return;
    }
    
    let html = '';
    
    // Botón anterior
    html += `
        <button class="pagination-btn" 
                onclick="cambiarPagina(${paginaActual - 1})" 
                ${paginaActual === 1 ? 'disabled' : ''}>
            Anterior
        </button>
    `;
    
    // Números de página con lógica inteligente
    const maxBotones = 7; // Número máximo de botones visibles
    let inicio, fin;
    
    if (totalPaginas <= maxBotones) {
        // Mostrar todas las páginas
        inicio = 1;
        fin = totalPaginas;
    } else {
        // Calcular rango dinámico
        const mitad = Math.floor(maxBotones / 2);
        inicio = Math.max(1, paginaActual - mitad);
        fin = Math.min(totalPaginas, inicio + maxBotones - 1);
        
        if (fin - inicio < maxBotones - 1) {
            inicio = Math.max(1, fin - maxBotones + 1);
        }
    }
    
    // Botón primera página si es necesario
    if (inicio > 1) {
        html += `
            <button class="pagination-btn" onclick="cambiarPagina(1)">
                1
            </button>
        `;
        if (inicio > 2) {
            html += `<span class="pagination-separator">...</span>`;
        }
    }
    
    // Botones de páginas
    for (let i = inicio; i <= fin; i++) {
        html += `
            <button class="pagination-btn ${i === paginaActual ? 'active' : ''}" 
                    onclick="cambiarPagina(${i})">
                ${i}
            </button>
        `;
    }
    
    // Botón última página si es necesario
    if (fin < totalPaginas) {
        if (fin < totalPaginas - 1) {
            html += `<span class="pagination-separator">...</span>`;
        }
        html += `
            <button class="pagination-btn" onclick="cambiarPagina(${totalPaginas})">
                ${totalPaginas}
            </button>
        `;
    }
    
    // Botón siguiente
    html += `
        <button class="pagination-btn" 
                onclick="cambiarPagina(${paginaActual + 1})" 
                ${paginaActual === totalPaginas ? 'disabled' : ''}>
            Siguiente »
        </button>
    `;
    
    paginacion.innerHTML = html;
}

function actualizarVista() {
    renderizarTabla();
    renderizarPaginacion();
}

// ==================== PAGINACIÓN ====================
function cambiarPagina(nuevaPagina) {
    const totalPaginas = Math.ceil(clientesFiltrados.length / CLIENTES_POR_PAGINA);
    
    if (nuevaPagina < 1 || nuevaPagina > totalPaginas) return;
    
    paginaActual = nuevaPagina;
    actualizarVista();
    
    // Scroll suave hacia arriba
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ==================== BÚSQUEDA ====================
function buscarCliente() {
    const busqueda = document.getElementById('searchInput').value.trim().toLowerCase();
    
    if (busqueda === '') {
        clientesFiltrados = [...todosLosClientes];
    } else {
        clientesFiltrados = todosLosClientes.filter(cliente => 
            cliente.id.toLowerCase().includes(busqueda)
        );
    }
    
    paginaActual = 1;
    actualizarVista();
}

function limpiarBusqueda() {
    document.getElementById('searchInput').value = '';
    clientesFiltrados = [...todosLosClientes];
    paginaActual = 1;
    actualizarVista();
}

// ==================== UTILIDADES ====================
function irAPrimeraPagina() {
    cambiarPagina(1);
}

function irAUltimaPagina() {
    const totalPaginas = Math.ceil(clientesFiltrados.length / CLIENTES_POR_PAGINA);
    cambiarPagina(totalPaginas);
}