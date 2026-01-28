// Mostrar notificaciones discretas
function mostrarNotificacion(mensaje, tipo) {
  const notificacion = document.createElement('div');
  notificacion.className = `notificacion ${tipo}`;
  notificacion.textContent = mensaje;
  
  document.body.appendChild(notificacion);
  
  // Mostrar con animación
  setTimeout(() => notificacion.classList.add('mostrar'), 100);
  
  // Ocultar después de 3 segundos
  setTimeout(() => {
    notificacion.classList.remove('mostrar');
    setTimeout(() => notificacion.remove(), 300);
  }, 3000);
}

// Verificar mensajes en la URL
const urlParams = new URLSearchParams(window.location.search);
const mensaje = urlParams.get('mensaje');
const error = urlParams.get('error');

if (mensaje === 'guardado') {
  mostrarNotificacion('Proveedor guardado exitosamente', 'exito');
  window.history.replaceState({}, document.title, window.location.pathname);
}

if (error) {
  mostrarNotificacion('Error al guardar el proveedor', 'error');
  window.history.replaceState({}, document.title, window.location.pathname);
}

// Confirmación antes de guardar
document.getElementById('proveedorForm').addEventListener('submit', function(e) {
  e.preventDefault();
  
  const confirmar = confirm('¿Deseas guardar este proveedor?');
  
  if (confirmar) {
    this.submit();
  }
});