// ==================== INVENTARIO.JS ====================
// Script para filtrado de productos en el reporte de inventario

document.addEventListener('DOMContentLoaded', function() {
    const buscadorInput = document.getElementById('buscadorInput');
    
    if (buscadorInput) {
        buscadorInput.addEventListener('keyup', filtrarProductos);
    }
});

/**
 * Filtra los grupos de productos según el texto ingresado
 * Busca coincidencias en:
 * - Título del tipo de producto (h3)
 * - Nombres de productos individuales
 */
function filtrarProductos() {
    const filtro = this.value.toLowerCase().trim();
    const grupos = document.querySelectorAll('.grupo-producto-container');
    
    // Si el filtro está vacío, mostrar todos los grupos
    if (filtro === '') {
        grupos.forEach(grupo => {
            grupo.style.display = 'block';
        });
        return;
    }
    
    // Filtrar cada grupo
    grupos.forEach(container => {
        const h3Element = container.querySelector('.tipo-titulo');
        const tituloGrupo = h3Element ? h3Element.textContent.toLowerCase() : '';
        
        // Obtener todos los nombres de productos en este grupo
        const nombresProductos = Array.from(container.querySelectorAll('.nombre-producto'))
            .map(el => el.textContent.toLowerCase());
        
        // Verificar coincidencias
        const matchTitulo = tituloGrupo.includes(filtro);
        const matchProducto = nombresProductos.some(nombre => nombre.includes(filtro));
        
        // Mostrar u ocultar el grupo según coincidencias
        container.style.display = (matchTitulo || matchProducto) ? 'block' : 'none';
    });
}

/**
 * Función auxiliar para limpiar el buscador
 * (Opcional - puedes agregar un botón de limpiar si lo deseas)
 */
function limpiarBuscador() {
    const buscadorInput = document.getElementById('buscadorInput');
    if (buscadorInput) {
        buscadorInput.value = '';
        // Trigger del evento para actualizar la vista
        buscadorInput.dispatchEvent(new Event('keyup'));
    }
}

/**
 * Función para resaltar texto coincidente (Opcional - Feature extra)
 * Puedes llamarla si quieres que el texto buscado se resalte
 */
function resaltarTexto(texto, busqueda) {
    if (!busqueda) return texto;
    
    const regex = new RegExp(`(${busqueda})`, 'gi');
    return texto.replace(regex, '<mark>$1</mark>');
}