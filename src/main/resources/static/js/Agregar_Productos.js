document.addEventListener('DOMContentLoaded', function() {
    const imageInput = document.getElementById('imageInput');
    const uploadArea = document.getElementById('uploadArea');
    const uploadContent = document.getElementById('uploadContent');
    const imagePreview = document.getElementById('imagePreview');
    const previewImg = document.getElementById('previewImg');
    const changeImageBtn = document.getElementById('changeImage');

    // Click en el área de upload
    uploadArea.addEventListener('click', function(e) {
        if (e.target.id !== 'changeImage' && !e.target.closest('.btn-change-image')) {
            imageInput.click();
        }
    });

    // Cambiar imagen
    if (changeImageBtn) {
        changeImageBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            imageInput.click();
        });
    }

    // Cuando se selecciona un archivo
    imageInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        
        if (file) {
            // Validar tipo de archivo
            const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
            if (!validTypes.includes(file.type)) {
                alert('Por favor selecciona una imagen válida (JPG, PNG o WEBP)');
                imageInput.value = '';
                return;
            }

            // Validar tamaño (5MB)
            const maxSize = 5 * 1024 * 1024; // 5MB en bytes
            if (file.size > maxSize) {
                alert('La imagen es demasiado grande. El tamaño máximo es 5MB');
                imageInput.value = '';
                return;
            }

            // Mostrar preview
            const reader = new FileReader();
            
            reader.onload = function(event) {
                previewImg.src = event.target.result;
                uploadContent.style.display = 'none';
                imagePreview.style.display = 'block';
            };
            
            reader.readAsDataURL(file);
        }
    });

    // Drag and Drop (opcional, pero útil)
    uploadArea.addEventListener('dragover', function(e) {
        e.preventDefault();
        uploadArea.style.borderColor = '#3b82f6';
        uploadArea.style.backgroundColor = '#eff6ff';
    });

    uploadArea.addEventListener('dragleave', function(e) {
        e.preventDefault();
        uploadArea.style.borderColor = '#cbd5e0';
        uploadArea.style.backgroundColor = '#f8fafc';
    });

    uploadArea.addEventListener('drop', function(e) {
        e.preventDefault();
        uploadArea.style.borderColor = '#cbd5e0';
        uploadArea.style.backgroundColor = '#f8fafc';
        
        const file = e.dataTransfer.files[0];
        if (file && file.type.startsWith('image/')) {
            // Crear un nuevo objeto DataTransfer para asignar el archivo al input
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(file);
            imageInput.files = dataTransfer.files;
            
            // Disparar el evento change
            const event = new Event('change', { bubbles: true });
            imageInput.dispatchEvent(event);
        }
    });

    // Validación del formulario antes de enviar
    const form = document.getElementById('formProducto');
    if (form) {
        form.addEventListener('submit', function(e) {
            const cantidadStock = parseInt(document.querySelector('input[name="cantidadStock"]').value);
            const stockMinimo = parseInt(document.querySelector('input[name="stockMinimo"]').value);
            const stockMaximo = parseInt(document.querySelector('input[name="stockMaximo"]').value);

            // Validar que stock mínimo sea menor que stock máximo
            if (stockMinimo >= stockMaximo) {
                e.preventDefault();
                alert('El stock mínimo debe ser menor que el stock máximo');
                return false;
            }

            // Validar que la cantidad en stock no exceda el máximo
            if (cantidadStock > stockMaximo) {
                e.preventDefault();
                alert('La cantidad en stock no puede ser mayor que el stock máximo');
                return false;
            }

            // Mostrar indicador de carga en el botón
            const submitBtn = form.querySelector('.btn-primary');
            const btnText = submitBtn.querySelector('i').nextSibling;
            submitBtn.disabled = true;
            submitBtn.style.opacity = '0.7';
            btnText.textContent = ' Guardando...';
        });
    }

    // Validación en tiempo real de los campos numéricos
    const numeroInputs = document.querySelectorAll('input[type="number"]');
    numeroInputs.forEach(input => {
        input.addEventListener('input', function() {
            // Eliminar valores negativos
            if (this.value < 0) {
                this.value = 0;
            }
        });
    });
});