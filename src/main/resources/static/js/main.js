let listElements = document.querySelectorAll('.list_button--click');

listElements.forEach(listElement => {
    listElement.addEventListener('click', ()=> {
        listElement.classList.toggle('arrow'); // rota la flecha

        let height = 0;
        let menu = listElement.nextElementSibling;

        if(menu.clientHeight == 0){
            height = menu.scrollHeight; // abre el submenú
        }

        menu.style.height = `${height}px`;
    });
});


    document.addEventListener('DOMContentLoaded', function() {
        // SUBMENÚS DESPLEGABLES
        let listElements = document.querySelectorAll('.list_button--click');

        listElements.forEach(listElement => {
            listElement.addEventListener('click', () => {
                // Toggle arrow rotation
                listElement.classList.toggle('arrow');
                
                // Toggle submenu visibility
                let listShow = listElement.nextElementSibling;
                let height = 0;
                
                if(listShow.clientHeight === 0) {
                    height = listShow.scrollHeight;
                }
                
                listShow.style.height = height + 'px';
            });
        });

        // RESALTAR PÁGINA ACTIVA
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav_link');
        
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if(href && currentPath.includes(href)) {
                link.classList.add('active');
                
                // Si es un link interno, expandir su menú padre
                if(link.classList.contains('nav_link--inside')) {
                    const parentList = link.closest('.list_item--click');
                    if(parentList) {
                        const button = parentList.querySelector('.list_button--click');
                        const submenu = parentList.querySelector('.list_show');
                        
                        button.classList.add('arrow');
                        submenu.style.height = submenu.scrollHeight + 'px';
                    }
                }
            }
        });
    });

    // FUNCIÓN PARA TOGGLE DEL SIDEBAR EN MÓVIL
    function toggleSidebar() {
        const nav = document.querySelector('.nav');
        const overlay = document.querySelector('.sidebar-overlay');
        
        nav.classList.toggle('show');
        overlay.classList.toggle('show');
    }

    // CERRAR SIDEBAR AL HACER CLIC EN UN LINK (SOLO MÓVIL)
    if(window.innerWidth <= 768) {
        document.querySelectorAll('.nav_link').forEach(link => {
            link.addEventListener('click', function() {
                toggleSidebar();
            });
        });
    }

