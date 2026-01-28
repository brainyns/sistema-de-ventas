package sistemaventa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sistemaventa.model.Administrador;
import sistemaventa.service.AdministradorService;

@Controller
@RequestMapping("/admin")
public class AdministradorController {
    
    @Autowired
    private AdministradorService administradorService;

    // Listar administradores
    @GetMapping("/lista")
    public String listarAdministradores(Model model) {
        model.addAttribute("admins", administradorService.findAll());
        model.addAttribute("admin", new Administrador());
        return "lista_admin";
    }

    // Guardar o actualizar admin
    @PostMapping("/guardar")
    public String guardarAdmin(@ModelAttribute Administrador admin) {
        administradorService.save(admin);
        return "redirect:/admin/lista";
    }

    // Editar admin
    @GetMapping("/editar")
    public String editarAdmin(@RequestParam("id") Integer id, Model model) {
        model.addAttribute("admins", administradorService.findAll());
        Administrador admin = administradorService.findById(id);
        model.addAttribute("admin", admin);
        return "lista_admin";
    }

    // Eliminar admin
    @GetMapping("/eliminar")
    public String eliminarAdmin(@RequestParam("id") Integer id) {
        administradorService.deleteById(id);
        return "redirect:/admin/lista";
    }

    // Página del login
    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        // Si ya hay sesión iniciada, evitar mostrar el login otra vez
        if (session.getAttribute("adminLogueado") != null) {
            return "redirect:/admin/index";
        }

        // Crear el objeto que necesita el formulario (th:object="${admin}")
        model.addAttribute("admin", new Administrador());
        return "login"; // nombre del archivo login.html
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute Administrador adminForm,
                                HttpSession session,
                                Model model) {

        Administrador admin = administradorService.findByUsuarioAndContrasena(
                adminForm.getUsuario(), adminForm.getContrasena()
        );

        if (admin != null) {
            // Guardar admin en la sesión
            session.setAttribute("adminLogueado", admin);
            return "redirect:/admin/index";
        } else {
            model.addAttribute("error", "❌ Usuario o contraseña incorrectos");
            model.addAttribute("admin", new Administrador());
            return "login";
        }
    }

    // Menú principal del admin
    @GetMapping("/index")
    public String mostrarMenuAdmin(HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("adminUsuario", admin.getUsuario());
        return "index"; // tu vista principal del admin
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}