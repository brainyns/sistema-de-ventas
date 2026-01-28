// ========== ESTADO ==========
let todosProductosCompra = [];

// ========== MODAL DE PRODUCTOS ==========
function abrirModalProductosCompra() {
    const modal = new bootstrap.Modal(document.getElementById('modalProductosCompra'));
    modal.show();
    
    if (todosProductosCompra.length === 0) {
        cargarProductosCompra();
    } else {
        mostrarProductosCompra(todosProductosCompra);
    }
}

// ========== CARGAR PRODUCTOS ==========
async function cargarProductosCompra() {
    try {
        const response = await fetch('/ventas/listar-productos');
        const productos = await response.json();
        todosProductosCompra = productos;
        mostrarProductosCompra(productos);
    } catch (err) {
        console.error('Error:', err);
        document.querySelector('#tablaProductosCompra tbody').innerHTML = 
            '<tr><td colspan="7" class="text-center text-danger">Error al cargar productos</td></tr>';
    }
}

// ========== MOSTRAR PRODUCTOS EN TABLA ==========
function mostrarProductosCompra(productos) {
    const tbody = document.querySelector('#tablaProductosCompra tbody');
    
    if (productos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No hay productos disponibles</td></tr>';
        return;
    }
    
    tbody.innerHTML = productos.map(p => {
        const stockBadge = p.stock > 10 ? 'bg-success' : (p.stock > 0 ? 'bg-warning' : 'bg-danger');
        const nombreEscapado = p.nombre.replace(/'/g, "\\'");
        
        return `
            <tr>
                <td>
                    ${p.imagen ? 
                        `<img src="${p.imagen}" style="width:48px;height:48px;object-fit:cover;border-radius:4px;">` : 
                        '<span class="text-muted">N/I</span>'
                    }
                </td>
                <td><strong>${p.nombre}</strong></td>
                <td><span class="badge bg-secondary">${p.tipo}</span></td>
                <td>${p.modelo}</td>
                <td class="text-success fw-bold">${formatCurrencycolombian(p.precioConIva)}</td>
                <td><span class="badge ${stockBadge}">${p.stock}</span></td>
                <td>
                    <button class="btn btn-sm btn-primary" type="button" 
                            onclick="seleccionarProductoCompra(${p.id}, '${nombreEscapado}', ${p.precio})">
                        <i class="fas fa-check"></i> Seleccionar
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

// ========== FILTRAR PRODUCTOS ==========
function filtrarProductosCompra() {
    const busqueda = document.getElementById('buscarProductoCompra').value.toLowerCase();
    
    if (busqueda === '') {
        mostrarProductosCompra(todosProductosCompra);
        return;
    }
    
    const productosFiltrados = todosProductosCompra.filter(p => 
        (p.nombre || '').toLowerCase().includes(busqueda) || 
        (p.tipo || '').toLowerCase().includes(busqueda) || 
        (p.modelo || '').toLowerCase().includes(busqueda)
    );
    
    mostrarProductosCompra(productosFiltrados);
}

// ========== SELECCIONAR PRODUCTO ==========
function seleccionarProductoCompra(id, nombre, precioVenta) {
    // Asignar ID y nombre del producto
    document.getElementById('productoId').value = id;
    document.getElementById('productoNombre').value = nombre;
    
    // Calcular precio de compra (60% descuento = 40% del precio de venta)
    const precioCompra = precioVenta * 0.4;
    
    // Actualizar campos
    document.getElementById('precioUnitarioHidden').value = precioCompra.toFixed(2);
    document.getElementById('precioCompraDisplay').value = formatCurrencycolombian(precioCompra);
    
    // Cerrar modal
    const modalInstance = bootstrap.Modal.getInstance(document.getElementById('modalProductosCompra'));
    if (modalInstance) {
        modalInstance.hide();
    }
}

// ========== DESCARGAR PDF ==========
function descargarPDFCompra() {
    window.location.href = '/compras/generar-pdf';
}

// ========== FORMATO DE MONEDA ==========
function formatCurrencycolombian(number, decimales = 2) {
    if (!number && number !== 0) return '$0,00';
    
    const [int, dec] = parseFloat(number).toFixed(decimales).split('.');
    const integerFormatted = int.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    
    return `$${integerFormatted},${dec}`;
}