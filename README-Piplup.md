# AquaPort — Nivel Piplup (MVP)

Reto 1 — Streams & Lambdas: `ConsultorFlota`
- [x] 5 consultas implementadas solo con Streams
- Evidencia:

  **Ubicación del código:** `Piplup/src/main/java/com/eci/aquaport/ejercicio1/`
  (`DroneAcuatico.java`, `ConsultorFlota.java`, `Main.java`)

  **Paso a paso de la implementación:**
  1. Se creó el record `DroneAcuatico(id, modelo, bateria, disponible, zona)` tal cual lo pide el enunciado, incluyendo la flota de ejemplo de 4 drones dentro de `Main`.
  2. Se creó la clase `ConsultorFlota` con un método por cada una de las 5 consultas pedidas, cada uno recibiendo `List<DroneAcuatico>` como parámetro:
     - `dronesDisponiblesOrdenadosPorBateria`: `filter` (disponible) + `filter` (batería ≥35%) + `sorted` (descendente) + `toList`.
     - `idsDronesDisponibles`: `filter` (disponible) + `map` (a `id`) + `toList`.
     - `existeDroneDisponibleConBateriaSuficiente`: `anyMatch` con ambas condiciones.
     - `contarDronesDisponibles`: `filter` (disponible) + `count`.
     - `droneConMayorBateria`: `max` con `Comparator.comparingInt`, retorna `Optional<DroneAcuatico>`.
  3. Se escribió `Main` con la flota de ejemplo del enunciado y se imprimieron los resultados de las 5 consultas.
  4. Se compiló y ejecutó para verificar:
     ```
     mvn clean compile
     java -cp target\classes com.eci.aquaport.ejercicio1.Main
     ```

  **Salida obtenida (evidencia de ejecución):**
  ```
  Disponibles con batería >= 35% ordenados: [AR-01 (92), AR-04 (73), AR-02 (45)]
  IDs disponibles: [AR-01, AR-02, AR-04]
  ¿Existe disponible con batería suficiente?: true
  Cantidad de disponibles: 3
  Drone con mayor batería: Optional[AR-01 (92)]
  ```

Reto 2 — GitHub y GitFlow
- [x] Rama develop y feature creadas, PR mergeado
- Evidencia:

  **Paso a paso:**
  1. Se creó el repositorio `Refuerzo-Corte2-Equipo-Piplup-JuanValderrama`.
  2. Se configuró el `.gitignore` para Java/Maven, excluyendo `target/`, `*.class`, `.idea/`.
  3. Se creó la rama `develop` a partir del commit inicial y se subió con `git push -u origin develop`.
  4. Desde `develop` se creó la rama `feature/streams-consultor-flota`.
  5. Se hicieron commits en esa rama, cada uno con una sola responsabilidad:
     - `feat: implementar ConsultorFlota con 5 consultas Stream (reto 01)`
     - `refactor: mover pom.xml y READMEs a la raiz, organizar Piplup por ejercicio1`
     - `docs: agregar evidencia del reto 01 en README-Piplup`
  6. Se abrió el Pull Request de `feature/streams-consultor-flota` hacia `develop` y se hizo el merge.

  **Pull Request:** https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/1

Reto 3 — Patrones de Diseño: `Mision.Builder`
- [x] Builder implementado con validación en build()
- Evidencia:

  **Ubicación del código:** `Piplup/src/main/java/com/eci/aquaport/ejercicio3/`
  (`DroneAcuatico.java`, `TipoCarga.java`, `EstadoMision.java`, `Mision.java`, `Main.java`)

  **Paso a paso de la implementación:**
  1. Se creó el modelo inmutable `Mision` utilizando el patrón de diseño Builder mediante la clase estática interna `Mision.Builder`.
  2. Se configuraron métodos fluidos para asignar cada atributo (`id`, `drone`, `puntoPartida`, `puntoLlegada`, `tipoCarga`, `estado`). Por defecto, el `estado` se inicializa en `EstadoMision.PENDIENTE`.
  3. En el método `build()`, se agregaron las validaciones de reglas de negocio antes de retornar la instancia de `Mision`:
     - `id` no nulo y no vacío (lanzando `IllegalStateException`).
     - `drone` no nulo y con disponibilidad verificada (`drone.disponible() == true`).
     - `puntoPartida` y `puntoLlegada` no nulos y no vacíos.
     - `tipoCarga` obligatorio.
  4. Se creó la clase `Main` en donde se probaron 4 escenarios: 1 caso exitoso y 3 casos de falla (drone ocupado, punto de llegada vacío e id nulo) usando bloques `try-catch` para verificar el lanzamiento correcto de excepciones.
  5. Se compiló y ejecutó desde la raíz del repositorio usando Maven:
     ```bash
     mvn clean compile
     mvn exec:java "-Dexec.mainClass=com.eci.aquaport.ejercicio3.Main"
     ```

  **Salida obtenida (evidencia de ejecución):**
  ```text
  === PRUEBA DE BUILDER DE MISION (RETO 03) ===

  1. Prueba con Caso Valido:
  Mision creada exitosamente: Mision{id='M-101', drone=AR-01 (Aqua-Ranger 100), puntoPartida='Embalse Norte', puntoLlegada='Laboratorio Hidrico', tipoCarga=MUESTRA_AGUA, estado=PENDIENTE}

  2. Prueba de Error: Drone no disponible
  Excepcion capturada esperada: El drone asignado (AR-03) no esta disponible para la mision.

  3. Prueba de Error: Punto de llegada vacio
  Excepcion capturada esperada: El punto de llegada es obligatorio y no puede estar vacio.

  4. Prueba de Error: ID nulo
  Excepcion capturada esperada: El ID de la mision es obligatorio.

Reto 4 — Principios SOLID (SRP y DIP)
- [ ] RegistradorMisiones, ValidadorMision, NotificadorOperador, RepositorioMisiones
- Evidencia:

Reto 5 — Diagrama de Contexto C4
- [ ] Diagrama en docs/c4-contexto-piplup.png
- Evidencia:

Reto 6 — RF y RNF
- [ ] 3 RF + 3 RNF + MoSCoW
- Evidencia:

Reto 7 — Plantilla DOSW (RF AP-01)
- [ ] Plantilla completa
- Evidencia:

Reto 8 — Manual de Identidad y UX/UI
- [ ] Identidad + mock con IA (3 estados)
- Evidencia:

Reto 9 — Agilismo y Jira
- [ ] Épica, feature, 3 HU, subtareas
- Evidencia:

Reto 10 — Diagrama de Casos de Uso
- [ ] Diagrama en docs/diagrama-cu-piplup.png
- Evidencia:

Reto 11 — Mocks con IA
- [ ] Proceso de 4 pasos documentado
- Evidencia:

Reto 12 — TDD: `ValidadorMision`
- [ ] Pruebas antes que código, ciclo Red-Green-Refactor
- Evidencia:

Reto 13 — JaCoCo
- [ ] Cobertura ≥80% en ValidadorMision
- Evidencia:

Reto 14 — SonarQube
- [ ] 0 bugs, 0 vulnerabilidades, deuda técnica 0
- Evidencia: