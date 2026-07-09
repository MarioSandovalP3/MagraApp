# 💪 MagraApp — Análisis de Composición Corporal

App multiplataforma que va **más allá del IMC**. Utiliza el **Método de la Marina de EE.UU.** (ecuaciones de Hodgdon & Beckett) para separar la masa grasa de la masa muscular con precisión real.

> Un fisicoculturista y una persona sedentaria pueden tener el mismo IMC.
> MagraApp resuelve ese problema.

---

## 🎯 ¿Qué hace?

| Función | Descripción |
|---|---|
| **Método de la Marina** | Calcula % de grasa corporal con medidas de cuello, cintura y cadera |
| **Modo Rápido (IMC)** | Cálculo básico con peso y estatura (con aviso de limitaciones) |
| **Masa Grasa vs Magra** | Separa tu peso en grasa y músculo con barra visual |
| **FFMI** | Índice de Masa Libre de Grasa — el verdadero sustituto del IMC para atletas |
| **ICA** | Relación Cintura-Altura — indicador de riesgo cardiovascular |
| **Clasificación ACSM** | Te clasifica como Atleta, Fitness, Aceptable o Sobrepeso |
| **Recomendaciones** | Consejos personalizados según tu objetivo |
| **Historial** | Gráficos de tendencia para rastrear tu progreso |

---

## 📱 Pantallas

### 1. Bienvenida
Elige tu objetivo: 🔥 Perder Grasa · 💪 Ganar Músculo · ⚖️ Mantenerse

### 2. Entrada de Datos
- **Modo Rápido:** Solo peso, estatura y edad
- **Modo Avanzado:** + cuello, cintura (y cadera para mujeres)
- Guía visual de cómo medir cada circunferencia

### 3. Resultados
- Indicador circular animado del % de grasa
- Barra de composición corporal (grasa vs magra)
- Tarjetas de métricas: Masa Grasa, Masa Magra, FFMI, ICA, IMC
- Recomendaciones personalizadas según tu objetivo
- Categoría ACSM con badge de color

### 4. Historial
- Gráficos de tendencia (% grasa, masa magra, peso)
- Listado de mediciones anteriores con comparación

---

## 🧮 Fórmulas Implementadas

### Método de la Marina (Hodgdon & Beckett)

**Hombres:**
```
%GC = 495 / (1.0324 − 0.19077 × log₁₀(cintura − cuello) + 0.15456 × log₁₀(estatura)) − 450
```

**Mujeres:**
```
%GC = 495 / (1.29579 − 0.35004 × log₁₀(cintura + cadera − cuello) + 0.22100 × log₁₀(estatura)) − 450
```

### Métricas Derivadas

| Métrica | Fórmula |
|---|---|
| Masa Grasa | `peso × (%GC / 100)` |
| Masa Magra | `peso − masa grasa` |
| FFMI | `masa magra / estatura² + 6.1 × (1.8 − estatura)` |
| ICA | `cintura / estatura` |
| IMC | `peso / estatura²` |

### Clasificación ACSM

| Categoría | Hombres | Mujeres |
|---|---|---|
| 🏆 Atleta / Esencial | 2% - 5% | 10% - 13% |
| 💪 Fitness | 6% - 13% | 14% - 20% |
| 👍 Aceptable | 14% - 24% | 21% - 31% |
| ⚠️ Sobrepeso | 25%+ | 32%+ |

---

## 🏗️ Arquitectura

```
composeApp/src/commonMain/kotlin/com/example/cmp/
 ┣ 📄 App.kt                          ← Navegación principal
 ┃
 ┣ 📂 data/
 ┃ ┣ 📄 Models.kt                     ← Enums y data classes
 ┃ ┗ 📄 HistoryRepository.kt          ← Almacenamiento del historial
 ┃
 ┣ 📂 domain/
 ┃ ┣ 📄 BodyCompositionCalculator.kt  ← Motor de cálculos (US Navy + IMC)
 ┃ ┗ 📄 GoalRecommendations.kt        ← Recomendaciones personalizadas
 ┃
 ┗ 📂 ui/
   ┣ 📂 theme/
   ┃ ┗ 📄 MagraTheme.kt               ← Tema oscuro premium
   ┣ 📂 components/
   ┃ ┗ 📄 Components.kt               ← Componentes reutilizables
   ┗ 📂 screens/
     ┣ 📄 WelcomeScreen.kt            ← Selección de objetivo
     ┣ 📄 InputScreen.kt              ← Formulario de medidas
     ┣ 📄 ResultsScreen.kt            ← Dashboard de resultados
     ┗ 📄 HistoryScreen.kt            ← Historial y tendencias
```

---

## 🛠️ Stack Tecnológico

| Herramienta | Versión |
|---|---|
| Kotlin | `2.3.0` |
| Compose Multiplatform | `1.10.0` |
| Android Gradle Plugin | `9.0.1` |
| Gradle | `9.1.0` |
| Material 3 | UI con tema oscuro personalizado |

**Plataformas soportadas:** Android · iOS · Desktop (Windows/macOS/Linux) · Web

---

## 🚀 Compilar y Ejecutar

### Android
```bash
./gradlew :androidApp:installDebug
```

### Desktop
```bash
./gradlew :composeApp:run
```

### Web
```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

---

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia Apache 2.0 — consulta el archivo [LICENSE](LICENSE) para más detalles.
