# Ingenieros vs Zombies: Simulación Multihilo

Este proyecto es una implementación de un juego (Ingenieros vs. Zombis) desarrollado en Java, diseñado como un caso de estudio práctico para la asignatura de **Programación Distribuida y Concurrente**.

El objetivo principal no es la jugabilidad, sino **demostrar una arquitectura multihilo funcional** donde cada entidad del juego (defensores, enemigos, generadores) opera como un hilo independiente, y cómo gestionar de forma segura la comunicación y el estado compartido entre ellos.

## Tecnologías Clave

* **Lenguaje:** Java 25
* **Framework de UI:** JavaFX
* **Núcleo de Concurrencia:**
    * `java.lang.Thread` (Patrón "Hilo por Entidad")
    * `java.util.concurrent.locks.ReentrantLock` (para Exclusión Mutua)
    * `java.util.concurrent.CopyOnWriteArrayList` (para listas concurrentes de lectura intensiva)
    * `javafx.application.Platform.runLater` (para sincronización segura Backend-Frontend)

-----

## Arquitectura de Hilos y Concurrencia

El diseño del proyecto se centra en resolver los desafíos clásicos de la concurrencia. La arquitectura se puede clasificar como **MIMD (Multiple Instruction, Multiple Data)**, donde cada hilo (entidad) es un agente autónomo que ejecuta sus propias instrucciones (`run()`) sobre sus propios datos (posición, vida).

### 1\. El Modelo: "Un Hilo por Entidad"

El núcleo del backend se basa en el patrón **Hilo por Entidad**:

* **`EntidadActiva` (Clase Abstracta):** Es la clase base que `extends Thread`. Define el contrato para todas las entidades activas del juego.
* **`Ingeniero` (hilo):** Cada ingeniero (`IngenieroSistemas`, `IngenieroCivil`, etc.) es un hilo independiente. Su método `run()` contiene su propia IA: un bucle infinito que busca objetivos (lee el estado del mapa) y ejecuta acciones (atacar, usar habilidad).
* **`Zombie` (hilo):** Cada zombi (`ZombiComun`, `ZombiBruto`, etc.) es un hilo independiente. Su método `run()` contiene su lógica de movimiento, actualizando su propia posición a intervalos regulares (`Thread.sleep()`).
* **`GestorDeOleadas` (hilo):** Es un hilo "spawner" o productor. Su única tarea es dormir (`Thread.sleep()`) durante un tiempo y luego despertar para "producir" (instanciar e iniciar) nuevos hilos `Zombie` y añadirlos al estado compartido.
* **`Juego` (hilo):** El bucle principal del juego (`mainLoop`) se ejecuta en su propio hilo, actuando como un orquestador y un monitor que envía actualizaciones de estado a la UI.

### 2\. El Desafío: Gestión del Estado Compartido

Con docenas de hilos corriendo simultáneamente, el principal desafío es evitar **Condiciones de Carrera** (Race Conditions) al acceder a los recursos compartidos. Este proyecto gestiona dos recursos críticos de formas diferentes:

#### Recurso 1: `GestorDeRecursos` (Exclusión Mutua)

* **Problema:** La "Energía" es un recurso transaccional. ¿Qué pasa si dos ingenieros intentan usar energía (`gastarEnergia()`) exactamente al mismo tiempo? Podrían ambos leer `energiaActual = 100`, ambos restar `75` y el resultado final sería `25` en lugar de fallar uno de los dos.
* **Solución:** `java.util.concurrent.locks.ReentrantLock`.
* **Explicación:** La clase `GestorDeRecursos` actúa como un **Monitor**. Los métodos `gastarEnergia()` y `agregarEnergia()` usan `recursoLock.lock()` y `recursoLock.unlock()` en un bloque `try-finally`. Esto garantiza la **exclusión mutua**: solo un hilo a la vez puede estar dentro de esa sección crítica, asegurando que las operaciones de "verificar y luego gastar" sean **atómicas**.

#### Recurso 2: `Mapa` y las Listas de Entidades (Lectura Intensiva)

* **Problema:** Las listas `ingenierosActivos` y `zombiesActivos` son el recurso más disputado. Tenemos un patrón de acceso **R \>\> W (Lecturas mucho más frecuentes que Escrituras)**:
    * **Lectura (Alta Frecuencia):** Docenas de hilos `Ingeniero` iteran sobre `zombiesActivos` en cada ciclo de su IA para buscar objetivos. El hilo de la UI (`GameCanvas`) itera sobre *ambas* listas 60 veces por segundo para dibujar.
    * **Escritura (Baja Frecuencia):** El hilo `GestorDeOleadas` añade zombis ocasionalmente. Un zombi (hilo) se elimina a sí mismo cuando muere.
* **Solución:** `java.util.concurrent.CopyOnWriteArrayList`.
* **Explicación:** Usar un `ReentrantLock` aquí sería un desastre de rendimiento, ya que bloquearía a todos los ingenieros y a la UI cada vez que un solo zombi aparece. `CopyOnWriteArrayList` es la solución ideal:
    * Las **Lecturas** (iteraciones) son virtualmente gratuitas, no requieren bloqueo y operan sobre una instantánea inmutable de la lista.
    * Las **Escrituras** (`add()`, `remove()`) son costosas (copian todo el array subyacente), pero como son infrecuentes, es un costo que asumimos a cambio de lecturas concurrentes ultrarrápidas.

### 3\. El Puente: Sincronización Backend-Frontend

* **Problema:** El Backend es multihilo (MIMD), pero el Frontend de JavaFX es **estrictamente monohilo** (el *JavaFX Application Thread*). Si un hilo del backend (como el `mainLoop` de `Juego`) intenta actualizar un `Label` (`statsDisplay.actualizarStats(...)`), la aplicación crasheará con una `IllegalStateException`.
* **Solución:** `Platform.runLater()`.
* **Explicación:** La clase `MainApp` (frontend) se pasa a sí misma al constructor de `Juego` (backend). El backend usa esta referencia para enviar actualizaciones de UI. El método `mainApp.actualizarUI(Runnable tarea)` envuelve la tarea (ej. `label.setText(...)`) en `Platform.runLater()`. Esto actúa como una cola segura: el hilo del backend *produce* una tarea de UI y `Platform.runLater` se asegura de que solo el hilo de JavaFX la *consuma*, garantizando la seguridad de los hilos.

-----

## Métricas de Desempeño (Análisis Teórico)

Basado en los conceptos de la **Unidad 3**:

* **Ley de Amdahl:** El *Speed-Up* de este proyecto está limitado por sus componentes secuenciales. El *bottleneck* (cuello de botella) principal es el hilo de renderizado de JavaFX (`AnimationTimer`), que debe dibujar *todas* las entidades secuencialmente en cada frame. No importa cuántos núcleos tengamos, el renderizado sigue siendo una tarea monohilo.
* **Ley de Gustafson:** Sin embargo, la arquitectura escala bien con el *tamaño del problema*. Gracias al patrón "Hilo por Entidad" y `CopyOnWriteArrayList`, podemos manejar un problema más grande (más zombis, más ingenieros) de manera eficiente si tenemos más núcleos. Duplicar los núcleos nos permite duplicar la cantidad de entidades (hilos) que procesan su IA en paralelo.

## Estructura del Proyecto

* `src/game`: Contiene `Juego.java`, el orquestador principal del backend.
* `src/entities`: Clases base (`EntidadActiva`) y concretas (`IngenieroSistemas`, `ZombiComun`) que definen a los hilos-actores.
* `src/managers`: Los recursos compartidos y *thread-safe* (`Mapa`, `GestorDeRecursos`) y los hilos-servicio (`GestorDeOleadas`).
* `src/ui`: Todo el código del frontend (JavaFX), incluyendo el punto de entrada `MainApp.java`, las vistas y el lienzo (`GameCanvas`).

## Cómo Ejecutar el Proyecto

Este proyecto requiere Java 17+ y el SDK de JavaFX 17+ (o superior).

1.  Clonar el repositorio.
2.  Abrir el proyecto en IntelliJ IDEA.
3.  **Configurar el SDK de JavaFX:**
    * Ir a `File` \> `Project Structure...` \> `Libraries`.
    * Hacer clic en `+` \> `Java` y seleccionar la carpeta `lib` de tu SDK de JavaFX.
4.  **Configurar las VM Options (¡Crucial\!):**
    * Ir a `Run` \> `Edit Configurations...`.
    * Asegurarse de que el `Main class` sea `ui.MainApp`.
    * En el campo **`VM options`**, añadir la siguiente línea (ajustando la ruta a tu SDK):
      ```
      --module-path /ruta/a/tu/javafx-sdk-25/lib --add-modules javafx.controls,javafx.graphics
      ```
5.  Ejecutar `ui.MainApp`.
