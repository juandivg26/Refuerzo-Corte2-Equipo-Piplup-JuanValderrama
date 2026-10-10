# Changelog

Generado automaticamente con `scripts/generar-changelog.sh` a partir de los commits (Conventional Commits).

## Sin publicar

### Funcionalidades
- PlanificadorRuta y EjecutorRuta con reasignacion, cadena de custodia y notificacion de waypoints (Green)
- cadena de validacion, decorator de telemetria y adapter de la API hidrica por capas (Empoleon reto 03)
- streams de la red multi-embalse con eficiencia por zona, ruta multi-etapa y Collector propio (Empoleon reto 01)

### Refactorizaciones
- dividir los Main de Empoleon en metodos de maximo 15 lineas (estandar Enterprise)
- reemplazar System.out por Logger y canal inyectado en observadores (java:S106)
- constantes para los nombres de zona en el Main de ejercicio1 (java:S1192)
- reescribir comentario de ObservadorMision que Sonar leia como TODO (java:S1135)

### Pruebas
- auditoria de arquitectura por capas con ArchUnit (direccion de dependencias, dominio puro, inyeccion por constructor) (Empoleon reto 14)
- pruebas TDD del planificador y ejecutor de rutas multi-etapa con los 5 flujos alternos e integracion de capas (Red)
- Mockito verifica que la cadena se detiene en el primer fallo, que el decorator no altera el drone y que el adapter convierte valores limite
- pruebas del AnalizadorRedHidrica y del Collector en stream paralelo

### Documentacion
- evidencia de los retos 02, 04, 12, 13 y 14 de Empoleon y CHANGELOG actualizado
- README de Empoleon con estructura por capas y evidencia de los retos 01, 02 y 03
- CHANGELOG generado desde los commits
- resultado final de SonarQube en Prinplup (reto 14 completo)
- issues de SonarQube resueltos y justificacion de exclusiones (Prinplup reto 14)
- sprint v2 en Jira con Story Points y DoD, sistema de diseño con Fitts/Hick y prompts de mocks (Prinplup retos 08, 09, 11)
- RF/RNF con MoSCoW y tension AP-07/AP-08, plantilla DOSW AP-07 y fuentes PlantUML de C4 y CU (Prinplup retos 05, 06, 07, 10)

### Build
- quality gate Enterprise de JaCoCo (85% lineas y 75% ramas del proyecto, 80% por clase) con pruebas de las ramas pendientes (Empoleon reto 13)
- hook commit-msg que valida Conventional Commits y script que genera el CHANGELOG (Empoleon reto 02)
- activar el nivel Empoleon en el pom (version 3.0.0-SNAPSHOT)
- excluir copias de ejercicio12 del calculo de duplicacion y los Main de la cobertura en Sonar

### Mantenimiento
- copiar el modelo por capas de ejercicio3 a ejercicio12 (TDD de rutas multi-etapa)

## v2.0.0

### Funcionalidades
- AsignadorAutomatico de ejercicio12 implementado hasta pasar las pruebas TDD (Green)
- Main del reto 03 integra fabrica, estrategia y observadores con 15 drones
- alertas por Observer al centro de control y tecnico cuando un drone entra en FALLO
- AsignadorAutomatico con Strategy (MayorBateria y ZonaCercana) y ValidadorMision v2
- jerarquia DroneSuperficial, DroneSemisumergido y DroneBuceador con FabricaDrones (Factory Method)
- streams avanzados de la flota v2 con groupingBy, partitioningBy y averagingInt (Prinplup reto 01)

### Pruebas
- cubrir observadores, ramas del validador y alertas de mision sin drone para pasar el quality gate
- pruebas TDD del AsignadorAutomatico con Mockito - happy path, critica sin drones, FALLO, flota vacia, umbral de bateria (Red)
- Mockito verifica que cada observador se notifica una sola vez ante drone en FALLO
- estrategias de seleccion y descarte de drones ocupados
- FabricaDrones crea cada tipo con su capacidad y limite del buceador
- pruebas de ConsultorFlota v2

### Documentacion
- estructura de carpetas de Prinplup/src en el README
- evidencia de los retos 01, 03, 04, 12 y 13 de Prinplup

### Build
- quality gate de JaCoCo que falla el build con menos de 80% de lineas por clase o 70% de ramas
- activar nivel Prinplup en el pom y agregar Mockito

### Mantenimiento
- preparar release v2.0.0 - version del pom y documentacion de GitFlow
- copiar modelo v2 a ejercicio12 (TDD AsignadorAutomatico)

## v1.0.0

### Funcionalidades
- validar zona de destino contra las 5 zonas fijas del MVP (Green)
- implementar reglas de ValidadorMision via TDD - bateria, punto llegada, disponibilidad, zona (Green + Refactor)
- implementar reto 04 con SRP y DIP (RegistradorMisiones, ValidadorMision, NotificadorOperador y RepositorioMisiones)
- implementar Builder para Mision con validaciones y actualizar README

### Pruebas
- pruebas de zona fija, mision nula, bateria y mision entregada en ValidadorMision (Red)
- pruebas TDD para ValidadorMision - reto 12 (Red)

### Documentacion
- flujos alternos AP-01, colores de estado y Nielsen, y cobertura actualizada (retos 07, 08, 12, 13)
- documentar TDD y cobertura de ejercicio12
- agregar plantilla DOSW del RF AP-01 (reto 07)
- agregar RF, RNF y clasificacion MoSCoW del reto 06
- agregar evidencia del reto 05 en README-Piplup
- agregar diagrama de clases del reto 04 (SRP y DIP)
- agregar evidencia del reto 02 (GitFlow y PR)

### Build
- agregar sonar-maven-plugin para el analisis del reto 14

### Mantenimiento
- copiar modelo base a ejercicio12 (TDD ValidadorMision)

