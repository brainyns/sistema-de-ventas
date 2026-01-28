// Función para mostrar/ocultar contraseña
function togglePassword() {
    const passwordInput = document.getElementById('contrasena');
    const toggleBtn = document.querySelector('.toggle-password');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleBtn.innerHTML = '<span id="eye-icon">👁</span>';
        toggleBtn.setAttribute('aria-label', 'Ocultar contraseña');
    } else {
        passwordInput.type = 'password';
        toggleBtn.innerHTML = '<span id="eye-icon">👁‍🗨</span>';
        toggleBtn.setAttribute('aria-label', 'Mostrar contraseña');
    }
}

// Inicialización cuando carga el DOM
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.login-form');
    const submitBtn = document.querySelector('.btn-login');
    const errorMessage = document.querySelector('.error-message');
    const rememberCheckbox = document.getElementById('remember');
    const usuarioInput = document.getElementById('usuario');
    
    // Variable para prevenir múltiples envíos
    let formSubmitted = false;
    
    // Cargar usuario guardado si existe
    const savedUsername = localStorage.getItem('rememberedUser');
    if (savedUsername && usuarioInput && rememberCheckbox) {
        usuarioInput.value = savedUsername;
        rememberCheckbox.checked = true;
    }
    
    // Manejar envío del formulario
    if (form && submitBtn) {
        form.addEventListener('submit', function(e) {
            // Prevenir múltiples envíos
            if (formSubmitted) {
                e.preventDefault();
                return false;
            }
            
            formSubmitted = true;
            
            // Agregar clase de carga al botón
            submitBtn.classList.add('loading');
            submitBtn.disabled = true;
            
            // Guardar o eliminar usuario recordado
            if (rememberCheckbox && rememberCheckbox.checked && usuarioInput) {
                localStorage.setItem('rememberedUser', usuarioInput.value);
            } else {
                localStorage.removeItem('rememberedUser');
            }
        });
    }
    
    // Auto-ocultar mensaje de error después de 5 segundos
    if (errorMessage) {
        // Enfocar el campo de usuario cuando hay error
        usuarioInput?.focus();
        
        setTimeout(() => {
            errorMessage.style.transition = 'opacity 0.5s ease';
            errorMessage.style.opacity = '0';
            
            setTimeout(() => {
                errorMessage.style.display = 'none';
            }, 500);
        }, 5000);
    }
});