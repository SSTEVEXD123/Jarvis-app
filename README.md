# JarvisApp (Android)

Asistente personal estilo Jarvis para Android, modular y en español.

## Estructura

```text
/JarvisApp/
  models/
  data/
  pdfs/
  images/
  music/
```

Dentro de Android, esta carpeta se crea automáticamente en `filesDir/JarvisApp`.

## Módulos incluidos

- Chat tipo asistente con prompt interno optimizado en español.
- Instalación de modelos ligeros para llama.cpp + Qwen.
- Soporte de generación de imágenes (Stable Diffusion) y visión (Moondream 2).
- Música con búsqueda YouTube (Invidious API pública), con límite de peticiones por minuto.
- CRUD de archivos locales dentro de `/JarvisApp/`.
- Generación de PDF en `/JarvisApp/pdfs/`.
- Dashboard de RAM y almacenamiento.
- Persistencia SQLite (Room) con historial de chat, prompts, música y PDFs.

## Instalación local

```bash
# 1) Clonar
 git clone <tu-repo>
 cd Jarvis-app

# 2) Instalar dependencias base
 ./scripts/install_android_deps.sh

# 3) Descargar modelos en carpeta JarvisApp/models
 ./scripts/install_models.sh ./JarvisApp

# 4) Compilar APK (si tienes Android SDK listo)
 gradle assembleDebug
```

APK esperado:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions

Workflow: `.github/workflows/android-apk.yml`

Acciones automáticas:
1. Clona repositorio.
2. Prepara Java + Android SDK.
3. Ejecuta scripts de instalación.
4. Descarga modelos en `JarvisApp/models`.
5. Compila APK debug.
6. Publica artefacto instalable.

## Nota técnica de modelos

Los scripts descargan archivos base para:
- llama.cpp (TinyLlama GGUF)
- Qwen GGUF
- Moondream 2 GGUF
- Stable Diffusion metadata

Puedes reemplazar URLs por variantes cuantizadas según tu dispositivo.
