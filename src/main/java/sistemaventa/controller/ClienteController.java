package sistemaventa.controller;

import java.util.List;

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
import sistemaventa.model.Cliente;
import sistemaventa.service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // --- Entrar al módulo de clientes ---
    @GetMapping("")
    public String inicioModuloClientes(HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");
        if (admin == null) return "redirect:/admin/login";

        model.addAttribute("adminUsuario", admin.getUsuario());
        return "Menu_clientes"; 
    }

    // --- Mostrar formulario para agregar cliente ---
    @GetMapping("/agregar")
    public String mostrarFormularioAgregar(HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");

        //  Solo redirige si NO hay admin logueado
        if (admin == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("adminUsuario", admin.getUsuario());
        model.addAttribute("cliente", new Cliente());
        return "Agregar_cliente";
    }

    // --- Guardar nuevo cliente ---
    @PostMapping("/nuevo")
    public String registrarCliente(@ModelAttribute Cliente cliente, HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");

        if (admin == null) return "redirect:/admin/login";

        try {
            clienteService.guardarCliente(cliente, admin.getIdAdministrador());
            return "redirect:/clientes/ver";
        } catch (Exception e) {
            model.addAttribute("error", " Error al registrar cliente: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("adminUsuario", admin.getUsuario());
            return "Agregar_cliente";
        }
    }

    // --- Ver todos los clientes ---
    @GetMapping("/ver")
    public String verClientes(HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");
        if (admin == null) return "redirect:/admin/login";

        List<Cliente> clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);
        model.addAttribute("adminUsuario", admin.getUsuario());
        return "Ver_clientes";
    }

    // --- Buscar cliente por identificación ---
    @GetMapping("/buscar")
    public String buscarCliente(@RequestParam(value = "identificacion", required = false) Integer identificacion, HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");
        if (admin == null) return "redirect:/admin/login";

        if (identificacion == null) return "Editar_cliente";

        try {
            Cliente cliente = clienteService.buscarClientePorId(identificacion);
            if (cliente == null) {
                model.addAttribute("error", "ID incorrecto. El cliente con identificación " + identificacion + " no existe. Por favor intente nuevamente.");
                model.addAttribute("adminUsuario", admin.getUsuario());
                return "Editar_cliente";
            }

            model.addAttribute("cliente", cliente);
            model.addAttribute("adminUsuario", admin.getUsuario());
            return "Editar_cliente";
        } catch (Exception e) {
            model.addAttribute("error", "Error al buscar cliente: " + e.getMessage() + ". Por favor intente nuevamente.");
            model.addAttribute("adminUsuario", admin.getUsuario());
            return "Editar_cliente";
        }
    }


    @GetMapping("/eliminar")
    public String eliminarCliente(@RequestParam("identificacion") Integer identificacion, HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");
        if (admin == null) return "redirect:/admin/login"; // Seguridad: Debe estar logueado

        try {
            // Llama al servicio para eliminar el cliente por su ID
            clienteService.eliminarCliente(identificacion);
            
            // Éxito: Redirigir a la vista de todos los clientes
            return "redirect:/clientes/ver"; 

        } catch (Exception e) {
            // Error: Recargar la lista de clientes y mostrar el mensaje de error
            List<Cliente> clientes = clienteService.listarClientes();
            model.addAttribute("clientes", clientes);
            model.addAttribute("adminUsuario", admin.getUsuario());
            model.addAttribute("error", " ❌ Error al eliminar el cliente: " + e.getMessage());
            
            // Retorna a la vista de la lista de clientes con el error
            return "Ver_clientes"; 
        }
    }


    // --- Editar cliente ---
    @PostMapping("/editar")
    public String editarCliente(@ModelAttribute Cliente cliente, HttpSession session, Model model) {
        Administrador admin = (Administrador) session.getAttribute("adminLogueado");
        if (admin == null) return "redirect:/admin/login";

        try {
            clienteService.actualizarCliente(cliente, admin.getIdAdministrador());
            return "redirect:/clientes/ver";
        } catch (Exception e) {
            model.addAttribute("error", " Error al actualizar: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("adminUsuario", admin.getUsuario());
            return "Editar_cliente";
        }
    }
}
