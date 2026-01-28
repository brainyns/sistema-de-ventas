document.addEventListener('DOMContentLoaded', function() {
    // Auto-cerrar alertas después de 5 segundos
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(alerta => {
            alerta.style.animation = 'fadeOut 0.5s ease';
            setTimeout(() => alerta.remove(), 500);
        });
    }, 5000);
    
    // Inicializar eventos
    inicializarEventos();
});

// Inicializar todos los eventos
function inicializarEventos() {
    // Eventos de eliminación
    document.querySelectorAll('a.delete').forEach(boton => {
        boton.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.getAttribute('data-id');
            const usuario = this.getAttribute('data-usuario');
            if (id && usuario) confirmarEliminacion(id, usuario);
        });
    });
    
    // Validación en tiempo real
    const form = document.getElementById('formAdmin');
    if (form) {
        form.querySelectorAll('input').forEach(input => {
            input.addEventListener('input', () => {
                input.classList.remove('invalid');
                const error = document.getElementById('error' + input.id.charAt(0).toUpperCase() + input.id.slice(1));
                if (error) error.style.display = 'none';
            });
        });
    }
    
    // Cerrar modal con ESC o click fuera
    const modal = document.getElementById('modalConfirmacion');
    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) cerrarModal();
        });
    }
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && modal?.classList.contains('active')) cerrarModal();
    });
}

// Abrir modal de confirmación
function confirmarEliminacion(id, usuario) {
    const modal = document.getElementById('modalConfirmacion');
    const mensaje = document.getElementById('mensajeModal');
    const btnConfirmar = document.getElementById('btnConfirmar');
    
    // ✅ CORREGIDO: Agregar backticks para template literal
    mensaje.innerHTML = `¿Estás seguro de eliminar al administrador <strong>"${usuario}"</strong>?<br><br>Esta acción no se puede deshacer.`;
    
    btnConfirmar.onclick = () => {
        btnConfirmar.innerHTML = '<span class="spinner"></span> Eliminando...';
        btnConfirmar.classList.add('loading');
        // ✅ CORREGIDO: Agregar backticks para template literal
        setTimeout(() => window.location.href = `/admin/eliminar?id=${id}`, 500);
    };
    
    modal.classList.add('active');
}

// Cerrar modal
function cerrarModal() {
    const modal = document.getElementById('modalConfirmacion');
    const btnConfirmar = document.getElementById('btnConfirmar');
    modal.classList.remove('active');
    btnConfirmar.innerHTML = '<i class="fas fa-trash-alt"></i> Eliminar';
    btnConfirmar.classList.remove('loading');
}

// Validar formulario
function validarFormulario(form) {
    let esValido = true;
    const validaciones = {
        usuario: { min: 3, max: 50, msg: 'El usuario debe tener entre 3 y 50 caracteres' },
        email: { regex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, msg: 'Ingresa un email válido' },
        contrasena: { min: 6, max: 100, msg: 'La contraseña debe tener entre 6 y 100 caracteres' }
    };
    
    Object.keys(validaciones).forEach(id => {
        // ✅ CORREGIDO: Agregar backticks para template literal
        const campo = form.querySelector(`#${id}`);
        const error = document.getElementById('error' + id.charAt(0).toUpperCase() + id.slice(1));
        const val = validaciones[id];
        const valor = campo.value.trim();
        
        let invalido = false;
        if (val.min && (valor.length < val.min || valor.length > val.max)) invalido = true;
        if (val.regex && !val.regex.test(campo.value)) invalido = true;
        
        if (invalido) {
            error.textContent = val.msg;
            error.style.display = 'flex';
            campo.classList.add('invalid');
            esValido = false;
        } else {
            error.style.display = 'none';
            campo.classList.remove('invalid');
        }
    });
    
    if (esValido) {
        const btn = form.querySelector('button[type="submit"]');
        btn.innerHTML = '<span class="spinner"></span> Guardando...';
        btn.classList.add('loading');
    } else {
        mostrarAlerta('Por favor corrige los errores del formulario', 'error');
    }
    
    return esValido;
}

// Limpiar formulario
function limpiarFormulario() {
    const form = document.getElementById('formAdmin');
    if (!form) return;
    
    form.reset();
    form.querySelectorAll('input').forEach(input => input.classList.remove('invalid'));
    form.querySelectorAll('.error').forEach(error => error.style.display = 'none');
    
    const idInput = form.querySelector('input[name="idAdministrador"]');
    if (idInput?.value) window.location.href = '/admin/lista';
}

// Mostrar alerta
function mostrarAlerta(mensaje, tipo = 'success') {
    const iconos = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };
    const alerta = document.createElement('div');
    // ✅ CORREGIDO: Agregar backticks para template literal
    alerta.className = `alert alert-${tipo}`;
    alerta.innerHTML = `
        <span class="alert-icon">${iconos[tipo]}</span>
        <span>${mensaje}</span>
        <button class="alert-close" onclick="this.parentElement.remove()">×</button>
    `;
    
    const container = document.querySelector('.container');
    container.insertBefore(alerta, container.querySelector('h2'));
    
    setTimeout(() => {
        alerta.style.animation = 'fadeOut 0.5s ease';
        setTimeout(() => alerta.remove(), 500);
    }, 5000);
}