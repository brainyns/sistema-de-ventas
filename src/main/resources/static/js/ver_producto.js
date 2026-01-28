// Configuración de paginación
const ITEMS_PER_PAGE = 60;
let currentPage = 1;
let allRows = [];
let filteredRows = [];

// Inicializar cuando carga la página
document.addEventListener('DOMContentLoaded', function() {
    initPagination();
    updateStats();
});

// Inicializar sistema de paginación
function initPagination() {
    const tableBody = document.getElementById('tableBody');
    if (!tableBody) return;

    // Obtener todas las filas
    allRows = Array.from(tableBody.querySelectorAll('.producto-row'));
    filteredRows = [...allRows];

    if (allRows.length === 0) return;

    // Mostrar primera página
    showPage(1);
    
    // Crear controles de paginación
    createPagination();
}

// Mostrar página específica
function showPage(page) {
    currentPage = page;
    
    const start = (page - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;
    
    // Ocultar todas las filas
    allRows.forEach(row => row.style.display = 'none');
    
    // Mostrar solo las filas de la página actual
    const pageRows = filteredRows.slice(start, end);
    pageRows.forEach(row => row.style.display = '');
    
    // Actualizar controles de paginación
    updatePaginationControls();
    
    // Scroll al inicio de la tabla
    document.querySelector('.tabla-productos').scrollIntoView({ 
        behavior: 'smooth', 
        block: 'start' 
    });
}

// Crear controles de paginación
function createPagination() {
    const container = document.getElementById('paginationContainer');
    if (!container) return;

    const totalPages = Math.ceil(filteredRows.length / ITEMS_PER_PAGE);
    
    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let html = '';
    
    // Botón anterior
    html += `<button class="pagination-btn prev-next" onclick="changePage(${currentPage - 1})" ${currentPage === 1 ? 'disabled' : ''}>
                <i class="fa-solid fa-chevron-left"></i> Anterior
             </button>`;
    
    // Páginas numeradas
    const pages = getPageNumbers(currentPage, totalPages);
    
    pages.forEach(page => {
        if (page === '...') {
            html += `<span class="pagination-btn" disabled>...</span>`;
        } else {
            html += `<button class="pagination-btn ${page === currentPage ? 'active' : ''}" 
                            onclick="changePage(${page})">
                        ${page}
                     </button>`;
        }
    });
    
    // Botón siguiente
    html += `<button class="pagination-btn prev-next" onclick="changePage(${currentPage + 1})" ${currentPage === totalPages ? 'disabled' : ''}>
                Siguiente <i class="fa-solid fa-chevron-right"></i>
             </button>`;
    
    container.innerHTML = html;
}

// Obtener números de página a mostrar
function getPageNumbers(current, total) {
    const pages = [];
    const maxVisible = 12; // Máximo de botones visibles como en la imagen
    
    if (total <= maxVisible) {
        // Mostrar todas las páginas si son pocas
        for (let i = 1; i <= total; i++) {
            pages.push(i);
        }
    } else {
        // Mostrar páginas con elipsis
        if (current <= 6) {
            // Inicio: 1 2 3 4 5 6 7 8 9 10 11 ... última
            for (let i = 1; i <= 11; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(total);
        } else if (current >= total - 5) {
            // Final: 1 ... antepenúltima-10 ... última
            pages.push(1);
            pages.push('...');
            for (let i = total - 10; i <= total; i++) {
                pages.push(i);
            }
        } else {
            // Medio: 1 ... current-4 current-3 ... current ... current+3 current+4 ... última
            pages.push(1);
            pages.push('...');
            for (let i = current - 4; i <= current + 4; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(total);
        }
    }
    
    return pages;
}

// Actualizar controles de paginación
function updatePaginationControls() {
    createPagination();
}

// Cambiar de página
function changePage(page) {
    const totalPages = Math.ceil(filteredRows.length / ITEMS_PER_PAGE);
    
    if (page < 1 || page > totalPages) return;
    
    showPage(page);
}

// Actualizar estadísticas
function updateStats() {
    const badges = document.querySelectorAll('.badge');
    
    let total = badges.length;
    let bajo = 0;
    let medio = 0;
    let normal = 0;
    
    badges.forEach(badge => {
        const stock = parseInt(badge.getAttribute('data-stock'));
        const min = parseInt(badge.getAttribute('data-min'));
        const max = parseInt(badge.getAttribute('data-max'));
        
        if (stock <= min) {
            bajo++;
        } else if (stock < max * 0.3) {
            medio++;
        } else {
            normal++;
        }
    });
    
    // Actualizar valores en las cards
    document.getElementById('totalProductos').textContent = total;
    document.getElementById('stockBajo').textContent = bajo;
    document.getElementById('stockMedio').textContent = medio;
    document.getElementById('stockNormal').textContent = normal;
}