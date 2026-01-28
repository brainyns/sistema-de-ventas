// Variables globales
let clienteCargado = null;
let productoActual = null;
let carrito = [];
let todosLosProductos = [];
let montoRecibidoEfectivo = null; // Para almacenar monto recibido en efectivo
const IVA = 0.19;

// ========== FUNCIONES DE CLIENTE ==========

function cargarCliente() {
    const id = document.getElementById('clienteId').value;
    if (!id) {
        alert('Ingrese un ID de cliente');
        return;
    }

    fetch(`/ventas/buscar-cliente?id=${id}`)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                clienteCargado = { 
                    id: parseInt(id), 
                    nombre: data.nombre, 
                    telefono: data.telefono, 
                    email: data.email 
                };
                document.getElementById('nombreCliente').textContent = data.nombre;
                document.getElementById('telefonoCliente').textContent = data.telefono || 'N/A';
                document.getElementById('emailCliente').textContent = data.email || 'N/A';
                document.getElementById('infoCliente').style.display = 'block';
            } else {
                alert(data.mensaje);
            }
        })
        .catch(err => {
            console.error('Error:', err);
            alert('Error al buscar cliente');
        });
}

// ========== FUNCIONES DE PRODUCTOS ==========

function cargarProducto() {
    const id = document.getElementById('productoId').value;
    if (!id) {
        alert('Ingrese un ID de producto');
        return;
    }

    fetch(`/ventas/buscar-producto?id=${id}`)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                productoActual = data;
                document.getElementById('nombreProductoBusqueda').textContent = data.nombre;
                document.getElementById('stockProductoBusqueda').textContent = data.stock;
                document.getElementById('precioProductoBusqueda').textContent = formatNumberColombian(data.precioConIva);
                document.getElementById('cantidadBusqueda').max = data.stock;
                document.getElementById('cantidadBusqueda').value = 1;
                document.getElementById('productoEncontrado').style.display = 'block';
                
                // Limpiar el input de búsqueda
                document.getElementById('productoId').value = '';
            } else {
                alert(data.mensaje);
            }
        })
        .catch(err => {
            console.error('Error:', err);
            alert('Error al buscar producto');
        });
}

function agregarProductoBuscado() {
    if (!productoActual) return;
    
    const cantidad = parseInt(document.getElementById('cantidadBusqueda').value);
    agregarProductoAlCarrito(
        productoActual.id, 
        productoActual.nombre, 
        productoActual.precio, 
        productoActual.precioConIva, 
        cantidad, 
        productoActual.stock
    );
    
    // Ocultar el producto encontrado después de agregar
    document.getElementById('productoEncontrado').style.display = 'none';
    productoActual = null;
}

// ========== MODAL DE PRODUCTOS ==========

function abrirModalProductos() {
    const modal = new bootstrap.Modal(document.getElementById('modalProductos'));
    modal.show();
    
    if (todosLosProductos.length === 0) {
        cargarTodosLosProductos();
    }
}

function cargarTodosLosProductos() {
    fetch('/ventas/listar-productos')
        .then(res => res.json())
        .then(productos => {
            todosLosProductos = productos;
            mostrarProductosEnTabla(productos);
        })
        .catch(err => {
            console.error('Error:', err);
            document.getElementById('tablaProductos').innerHTML = 
                '<tr><td colspan="8" class="text-center text-danger">Error al cargar productos</td></tr>';
        });
}

function mostrarProductosEnTabla(productos) {
    let html = '';
    
    if (productos.length === 0) {
        html = '<tr><td colspan="8" class="text-center text-muted">No hay productos disponibles</td></tr>';
    } else {
        productos.forEach(p => {
            const stockBadge = p.stock > 10 ? 'bg-success' : (p.stock > 0 ? 'bg-warning' : 'bg-danger');
            
            html += `
                <tr>
                    <td>
                        ${p.imagen ? 
                            `<img src="${p.imagen}" class="producto-img" alt="${p.nombre}">` : 
                            `<div class="producto-img-placeholder">${p.nombre.charAt(0)}</div>`
                        }
                    </td>
                    <td><strong>${p.nombre}</strong></td>
                    <td><span class="badge bg-secondary">${p.tipo}</span></td>
                    <td>${p.modelo}</td>
                    <td class="text-success fw-bold">${formatCurrencycolombian(p.precioConIva)}</td>
                    <td><span class="badge badge-stock ${stockBadge}">${p.stock}</span></td>
                    <td>
                        <input type="number" id="cant_modal_${p.id}" class="form-control form-control-sm" 
                               style="width:70px;" value="1" min="1" max="${p.stock}" ${p.stock === 0 ? 'disabled' : ''}>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-primary" 
                                onclick="agregarDesdeModal(${p.id}, '${p.nombre.replace(/'/g, "\\'")}', ${p.precio}, ${p.precioConIva}, ${p.stock})"
                                ${p.stock === 0 ? 'disabled' : ''}>
                            <i class="fas fa-cart-plus"></i> Agregar
                        </button>
                    </td>
                </tr>
            `;
        });
    }
    
    document.getElementById('tablaProductos').innerHTML = html;
}

function filtrarProductos() {
    const busqueda = document.getElementById('buscarProducto').value.toLowerCase();
    const productosFiltrados = todosLosProductos.filter(p => 
        p.nombre.toLowerCase().includes(busqueda) ||
        p.tipo.toLowerCase().includes(busqueda) ||
        p.modelo.toLowerCase().includes(busqueda)
    );
    mostrarProductosEnTabla(productosFiltrados);
}

function agregarDesdeModal(id, nombre, precio, precioConIva, stock) {
    const cantidad = parseInt(document.getElementById(`cant_modal_${id}`).value);
    agregarProductoAlCarrito(id, nombre, precio, precioConIva, cantidad, stock);
    
    // Cerrar modal después de agregar
    bootstrap.Modal.getInstance(document.getElementById('modalProductos')).hide();
}

// ========== GESTIÓN DEL CARRITO ==========

function agregarProductoAlCarrito(id, nombre, precioBase, precioConIva, cantidad, stock) {
    if (cantidad <= 0 || cantidad > stock) {
        alert('Cantidad inválida o excede el stock');
        return;
    }

    const existe = carrito.find(item => item.id === id);
    if (existe) {
        if (existe.cantidad + cantidad > stock) {
            alert('No hay suficiente stock');
            return;
        }
        existe.cantidad += cantidad;
    } else {
        carrito.push({ id, nombre, precioBase, precioConIva, cantidad, stock });
    }

    actualizarCarrito();
}

function actualizarCarrito() {
    let html = '';
    let total = 0;

    if (carrito.length === 0) {
        html = '<p class="text-muted text-center py-3">No hay productos agregados</p>';
    } else {
        carrito.forEach((item, index) => {
            const subtotal = item.precioConIva * item.cantidad;
            total += subtotal;
            html += `
                <div class="carrito-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <strong>${item.nombre}</strong><br>
                            <small class="text-muted">Cantidad: ${item.cantidad} x ${formatCurrencycolombian(item.precioConIva)} = ${formatCurrencycolombian(subtotal)}</small>
                        </div>
                        <button class="btn btn-danger btn-sm" onclick="eliminarDelCarrito(${index})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            `;
        });
    }

    document.getElementById('carrito').innerHTML = html;
    document.getElementById('totalGeneral').textContent = formatNumberColombian(total);
}

function eliminarDelCarrito(index) {
    carrito.splice(index, 1);
    actualizarCarrito();
}

function vaciarCarrito() {
    carrito = [];
    montoRecibidoEfectivo = null;
    actualizarCarrito();
    console.log('Carrito vaciado');
}

// ========== RESUMEN Y CONFIRMACIÓN ==========

function mostrarResumen() {
    if (!clienteCargado) {
        alert('Debe cargar un cliente primero');
        return;
    }
    if (carrito.length === 0) {
        alert('El carrito está vacío');
        return;
    }

    const metodoPago = document.querySelector('input[name="metodoPago"]:checked').value;
    const total = parseColombianNumber(document.getElementById('totalGeneral').textContent);
    let vuelto = null;
    let montoRecibido = null;

    // Si es efectivo, solicitar dinero
    if (metodoPago === 'Efectivo') {
        do {
            const montoStr = prompt(`Total a pagar: $${formatCurrencycolombian(total)}\n\n¿Cuánto dinero recibe?`, '');
            if (montoStr === null) {
                return; // Usuario canceló
            }
            montoRecibido = parseFloat(montoStr);
            if (isNaN(montoRecibido) || montoRecibido < 0) {
                alert('Por favor ingrese un valor válido');
                continue;
            }
            if (montoRecibido < total) {
                alert(`El monto es insuficiente. Total: $${formatCurrencycolombian(total)}`);
                continue;
            }
            break;
        } while (true);
        
        vuelto = montoRecibido - total;
        montoRecibidoEfectivo = montoRecibido;
    }

    let html = `
        <p><strong>Cliente:</strong> ${clienteCargado.nombre} (ID: ${clienteCargado.id})</p>
        <p><strong>Método de Pago:</strong> ${metodoPago}</p>
        <hr>
        <h6>Productos:</h6>
        <table class="table table-sm">
            <thead>
                <tr>
                    <th>Producto</th>
                    <th>Cant.</th>
                    <th>Precio</th>
                    <th>IVA</th>
                    <th>Subtotal</th>
                </tr>
            </thead>
            <tbody>
    `;

    carrito.forEach(item => {
        const subtotal = item.precioConIva * item.cantidad;
        const iva = item.precioBase * IVA;
        html += `
            <tr>
                <td>${item.nombre}</td>
                <td>${item.cantidad}</td>
                <td>${formatCurrencycolombian(item.precioBase)}</td>
                <td>${formatCurrencycolombian(iva)}</td>
                <td class="fw-bold">${formatCurrencycolombian(subtotal)}</td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
        <hr>
        <div class="text-end">
            <h5>TOTAL A PAGAR: <strong class="text-success">${formatCurrencycolombian(total)}</strong></h5>
    `;

    // Mostrar vuelto si existe
    if (vuelto !== null && vuelto > 0) {
        html += `<h5>VUELTO: <strong class="text-info">${formatCurrencycolombian(vuelto)}</strong></h5>`;
    }

    html += `
        </div>
    `;

    document.getElementById('contenidoFactura').innerHTML = html;
    new bootstrap.Modal(document.getElementById('modalResumen')).show();
}

function confirmarVentaFinal() {
    const metodoPago = document.querySelector('input[name="metodoPago"]:checked').value;
    
    const total = parseColombianNumber(document.getElementById('totalGeneral').textContent);

    const productosIds = carrito.map(item => item.id).join(',');
    const cantidades = carrito.map(item => item.cantidad).join(',');

    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/ventas/guardar';

    const campos = {
        clienteId: clienteCargado.id,
        productosIds: productosIds,
        cantidades: cantidades,
        metodoPago: metodoPago,
        totalFinal: total
    };

    // Agregar monto recibido si es efectivo
    if (metodoPago === 'Efectivo' && montoRecibidoEfectivo !== null) {
        campos.montoRecibido = montoRecibidoEfectivo;
    }

    for (const [key, value] of Object.entries(campos)) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = value;
        form.appendChild(input);
    }

    document.body.appendChild(form);
    
    // Cerrar modal antes de enviar
    bootstrap.Modal.getInstance(document.getElementById('modalResumen')).hide();
    
    // Enviar formulario
    form.submit();
}

// ========== DESCARGA DE PDF ==========

function descargarPDF() {
    console.log('Intentando descargar PDF...');
    fetch('/ventas/generar-pdf')
        .then(response => {
            console.log('Response status:', response.status);
            if (!response.ok) {
                throw new Error('Error al generar PDF: ' + response.status);
            }
            return response.blob();
        })
        .then(blob => {
            console.log('PDF descargado, tamaño:', blob.size);
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = 'Factura_' + new Date().getTime() + '.pdf';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            
            alert('✅ Factura descargada exitosamente');
        })
        .catch(err => {
            console.error('Error al descargar PDF:', err);
            alert('❌ Error al generar el PDF: ' + err.message);
        });
}

// ========== EVENT LISTENERS ==========

document.addEventListener('DOMContentLoaded', function() {
    // Permitir buscar producto con Enter
    const inputId = document.getElementById('productoId');
    if (inputId) {
        inputId.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                cargarProducto();
            }
        });
    }
    
    // Auto-descargar PDF cuando se carga la página con mensaje de éxito
    const urlParams = new URLSearchParams(window.location.search);
    const mensaje = urlParams.get('mensaje');
    
    if (mensaje && mensaje.includes('exitosamente')) {
        console.log('Venta exitosa detectada, descargando PDF automáticamente...');
        setTimeout(() => {
            descargarPDF();
        }, 1500);
    }
});

// ========== FUNCIONES DE FORMATO COLOMBIANO ==========

/**
 * Formatea un número según el estándar colombiano
 * Ejemplo: 661640.50 -> $661.640,50
 */
function formatCurrencycolombian(number, decimales = 2) {
    if (!number && number !== 0) return '$0,00';
    
    number = parseFloat(number);
    const parts = number.toFixed(decimales).split('.');
    const integerPart = parts[0];
    const decimalPart = parts[1];
    
    // Separar miles con punto
    const integerFormatted = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    
    return `$${integerFormatted},${decimalPart}`;
}