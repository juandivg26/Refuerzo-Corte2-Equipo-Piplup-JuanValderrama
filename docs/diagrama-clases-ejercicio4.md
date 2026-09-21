# Diagrama de clases - Reto 04 (SRP y DIP)

```mermaid
classDiagram
    class RepositorioMisiones {
        <<interface>>
        +guardar(Mision) void
        +buscarPorId(String) Optional~Mision~
        +listarTodas() List~Mision~
    }
    class RepositorioMisionesEnMemoria {
        -misiones Map~String, Mision~
    }
    class RegistradorMisiones {
        -repositorio RepositorioMisiones
        +registrar(Mision) void
        +buscarPorId(String) Optional~Mision~
        +listarMisiones() List~Mision~
    }
    class ValidadorMision {
        +validar(Mision) void
    }
    class NotificadorOperador {
        +mostrar(String) void
    }
    class Mision

    RepositorioMisionesEnMemoria ..|> RepositorioMisiones : implementa
    RegistradorMisiones --> RepositorioMisiones : depende de la interfaz (DIP)
    RegistradorMisiones ..> Mision
    ValidadorMision ..> Mision
    RepositorioMisiones ..> Mision
```

- **SRP:** cada clase tiene una sola razon para cambiar (registrar, validar, notificar).
- **DIP:** `RegistradorMisiones` recibe un `RepositorioMisiones` por constructor y no conoce la implementacion concreta.