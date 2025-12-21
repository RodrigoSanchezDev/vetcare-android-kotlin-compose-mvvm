#!/usr/bin/env python3
"""
fetch_demo_assets.py - Descargador automático de assets para VetCare Demo

Descarga imágenes desde Wikimedia Commons (API sin key) para usar como
assets de demostración en la app VetCare Android.

Uso:
    python3 tools/fetch_demo_assets.py

Autor: VetCare Dev Team
"""

import os
import sys
import time
import urllib.request
import urllib.parse
import json
from pathlib import Path
from datetime import datetime

# ============================================================
# CONFIGURACIÓN
# ============================================================

# Directorio de salida para assets
SCRIPT_DIR = Path(__file__).parent
PROJECT_ROOT = SCRIPT_DIR.parent
DRAWABLE_DIR = PROJECT_ROOT / "app" / "src" / "main" / "res" / "drawable"
README_PATH = PROJECT_ROOT / "README_ASSETS.md"

# Wikimedia Commons API
WIKIMEDIA_API = "https://commons.wikimedia.org/w/api.php"

# Assets a descargar con sus queries de búsqueda
# Formato: (nombre_archivo, query_principal, [queries_fallback])
ASSETS_TO_DOWNLOAD = [
    # Mascotas
    ("pet_max.jpg", "West Highland White Terrier dog portrait", ["white terrier dog", "small white dog portrait"]),
    ("pet_luna.jpg", "Siamese cat portrait", ["siamese cat face", "cream cat portrait"]),
    ("pet_rocky.jpg", "French Bulldog portrait", ["bulldog dog portrait", "french bulldog face"]),
    ("pet_michi.jpg", "tabby cat portrait", ["domestic cat portrait", "gray cat face"]),

    # Dashboard hero
    ("dashboard_hero_dog.jpg", "dog portrait photography", ["golden retriever portrait", "happy dog photo"]),

    # Veterinarios
    ("vet_pedro_gonzalez.jpg", "veterinarian with dog", ["vet examining dog", "animal doctor portrait"]),
    ("vet_maria_rodriguez.jpg", "female veterinarian portrait", ["woman vet with cat", "female animal doctor"]),
    ("vet_carlos_martinez.jpg", "male veterinarian portrait", ["man vet with animal", "veterinary professional"]),
]

# Extensiones válidas para Android drawable
VALID_EXTENSIONS = {'.jpg', '.jpeg', '.png', '.webp'}

# User-Agent para requests
USER_AGENT = "VetCareDemo/1.0 (Educational project; https://github.com/vetcare) Python/3"

# ============================================================
# FUNCIONES DE UTILIDAD
# ============================================================

def print_header():
    """Imprime encabezado del script"""
    print("=" * 60)
    print("🐾 VetCare Demo Assets Downloader")
    print("=" * 60)
    print(f"📁 Destino: {DRAWABLE_DIR}")
    print(f"📅 Fecha: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 60)
    print()


def make_request(url, params=None):
    """Realiza una request HTTP y retorna el contenido"""
    if params:
        url = f"{url}?{urllib.parse.urlencode(params)}"

    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})

    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            return response.read()
    except Exception as e:
        print(f"   ⚠️  Error en request: {e}")
        return None


def search_wikimedia_image(query, prefer_jpg=True):
    """
    Busca una imagen en Wikimedia Commons.

    Returns:
        dict con keys: url, title, artist, license, source_url
        o None si no encuentra nada
    """
    params = {
        "action": "query",
        "format": "json",
        "generator": "search",
        "gsrnamespace": "6",  # File namespace
        "gsrsearch": f"filetype:bitmap {query}",
        "gsrlimit": "20",
        "prop": "imageinfo",
        "iiprop": "url|extmetadata|mime",
        "iiurlwidth": "1200",  # Thumbnail width
    }

    response = make_request(WIKIMEDIA_API, params)
    if not response:
        return None

    try:
        data = json.loads(response.decode('utf-8'))
    except json.JSONDecodeError:
        return None

    pages = data.get("query", {}).get("pages", {})
    if not pages:
        return None

    # Filtrar y ordenar resultados
    candidates = []
    for page_id, page_data in pages.items():
        imageinfo = page_data.get("imageinfo", [{}])[0]
        mime = imageinfo.get("mime", "")
        thumb_url = imageinfo.get("thumburl") or imageinfo.get("url")

        if not thumb_url:
            continue

        # Verificar extensión válida
        ext = Path(urllib.parse.urlparse(thumb_url).path).suffix.lower()
        if ext not in VALID_EXTENSIONS:
            continue

        # Extraer metadata
        metadata = imageinfo.get("extmetadata", {})

        candidate = {
            "url": thumb_url,
            "original_url": imageinfo.get("descriptionurl", thumb_url),
            "title": page_data.get("title", "Unknown"),
            "artist": extract_text(metadata.get("Artist", {})),
            "license": extract_text(metadata.get("LicenseShortName", {})),
            "license_url": extract_text(metadata.get("LicenseUrl", {})),
            "attribution": extract_text(metadata.get("Attribution", {})),
            "mime": mime,
            "ext": ext,
            "is_jpg": ext in {'.jpg', '.jpeg'},
        }
        candidates.append(candidate)

    if not candidates:
        return None

    # Preferir JPG si se solicita
    if prefer_jpg:
        jpg_candidates = [c for c in candidates if c["is_jpg"]]
        if jpg_candidates:
            return jpg_candidates[0]

    return candidates[0]


def extract_text(metadata_field):
    """Extrae texto de un campo de metadata de Wikimedia"""
    if isinstance(metadata_field, dict):
        value = metadata_field.get("value", "")
        # Limpiar HTML básico
        if "<" in value:
            import re
            value = re.sub(r'<[^>]+>', '', value)
        return value.strip()
    return str(metadata_field) if metadata_field else ""


def download_image(url, dest_path):
    """Descarga una imagen a la ruta especificada"""
    try:
        req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
        with urllib.request.urlopen(req, timeout=60) as response:
            content = response.read()

        # Crear directorio si no existe
        dest_path.parent.mkdir(parents=True, exist_ok=True)

        with open(dest_path, 'wb') as f:
            f.write(content)

        return True
    except Exception as e:
        print(f"   ❌ Error descargando: {e}")
        return False


def format_file_size(size_bytes):
    """Formatea tamaño de archivo"""
    if size_bytes < 1024:
        return f"{size_bytes} B"
    elif size_bytes < 1024 * 1024:
        return f"{size_bytes / 1024:.1f} KB"
    else:
        return f"{size_bytes / (1024 * 1024):.1f} MB"


# ============================================================
# FUNCIÓN PRINCIPAL DE DESCARGA
# ============================================================

def download_asset(filename, primary_query, fallback_queries):
    """
    Intenta descargar un asset, probando queries de fallback si es necesario.

    Returns:
        dict con info del asset descargado, o None si falló
    """
    dest_path = DRAWABLE_DIR / filename
    prefer_jpg = filename.endswith('.jpg')

    print(f"📥 {filename}")
    print(f"   🔍 Buscando: \"{primary_query}\"")

    # Intentar query principal
    result = search_wikimedia_image(primary_query, prefer_jpg=prefer_jpg)

    # Intentar fallbacks si no hay resultado
    if not result and fallback_queries:
        for fallback in fallback_queries:
            print(f"   🔄 Fallback: \"{fallback}\"")
            result = search_wikimedia_image(fallback, prefer_jpg=prefer_jpg)
            if result:
                break

    if not result:
        print(f"   ❌ No se encontró imagen válida")
        return None

    # Verificar si necesitamos cambiar extensión
    source_ext = result["ext"]
    target_ext = Path(filename).suffix.lower()

    actual_filename = filename
    if source_ext != target_ext:
        if source_ext in VALID_EXTENSIONS:
            # Cambiar nombre si es necesario
            actual_filename = Path(filename).stem + source_ext
            print(f"   ⚠️  Formato {source_ext} (no {target_ext}), guardando como: {actual_filename}")
            dest_path = DRAWABLE_DIR / actual_filename

    # Descargar
    print(f"   ⬇️  Descargando desde: {result['url'][:80]}...")

    if download_image(result["url"], dest_path):
        file_size = dest_path.stat().st_size
        print(f"   ✅ Guardado: {dest_path.name} ({format_file_size(file_size)})")

        return {
            "local_file": actual_filename,
            "original_name": filename,
            "source_url": result["original_url"],
            "download_url": result["url"],
            "title": result["title"],
            "artist": result["artist"] or "No disponible",
            "license": result["license"] or "No disponible",
            "license_url": result["license_url"],
            "attribution": result["attribution"],
            "file_size": file_size,
        }

    return None


# ============================================================
# ACTUALIZACIÓN DE README
# ============================================================

def update_readme(downloaded_assets):
    """Actualiza README_ASSETS.md con información de los assets descargados"""

    if not downloaded_assets:
        return

    # Leer contenido actual
    try:
        with open(README_PATH, 'r', encoding='utf-8') as f:
            content = f.read()
    except FileNotFoundError:
        content = "# Assets de Imágenes - VetCare\n\n"

    # Verificar si ya existe la sección
    section_marker = "## Assets Descargados Automáticamente"
    if section_marker in content:
        # Remover sección existente
        parts = content.split(section_marker)
        content = parts[0].rstrip()

    # Crear nueva sección
    new_section = f"""

---

{section_marker}

> ⚠️ **Generado automáticamente** por `tools/fetch_demo_assets.py` el {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}

### Tabla de Atribución

| Archivo Local | Fuente (Wikimedia) | Autor/Crédito | Licencia |
|---------------|-------------------|---------------|----------|
"""

    for asset in downloaded_assets:
        local = asset["local_file"]
        source = f"[Ver fuente]({asset['source_url']})" if asset["source_url"] else "N/A"
        artist = asset["artist"][:50] + "..." if len(asset["artist"]) > 50 else asset["artist"]
        license_info = asset["license"]
        if asset["license_url"]:
            license_info = f"[{license_info}]({asset['license_url']})"

        new_section += f"| `{local}` | {source} | {artist} | {license_info} |\n"

    new_section += """
### Notas sobre Licencias

Las imágenes descargadas desde Wikimedia Commons están bajo diversas licencias libres.
Revisa cada licencia individual para cumplir con los requisitos de atribución.

Para uso comercial, verifica que la licencia específica lo permita.
"""

    # Escribir archivo actualizado
    with open(README_PATH, 'w', encoding='utf-8') as f:
        f.write(content + new_section)

    print(f"\n📝 README_ASSETS.md actualizado con tabla de atribución")


# ============================================================
# MAIN
# ============================================================

def main():
    print_header()

    # Verificar/crear directorio drawable
    if not DRAWABLE_DIR.exists():
        print(f"📁 Creando directorio: {DRAWABLE_DIR}")
        DRAWABLE_DIR.mkdir(parents=True, exist_ok=True)

    downloaded = []
    failed = []
    skipped = []

    print("🚀 Iniciando descarga de assets...\n")

    for filename, primary_query, fallbacks in ASSETS_TO_DOWNLOAD:
        dest_path = DRAWABLE_DIR / filename

        # Verificar si ya existe
        if dest_path.exists():
            print(f"⏭️  {filename} ya existe, omitiendo...")
            skipped.append(filename)
            continue

        result = download_asset(filename, primary_query, fallbacks)

        if result:
            downloaded.append(result)
        else:
            failed.append(filename)

        # Pequeña pausa para no sobrecargar el servidor
        time.sleep(1)
        print()

    # Resumen
    print("=" * 60)
    print("📊 RESUMEN")
    print("=" * 60)
    print(f"✅ Descargados: {len(downloaded)}")
    print(f"⏭️  Omitidos (ya existían): {len(skipped)}")
    print(f"❌ Fallidos: {len(failed)}")

    if downloaded:
        print("\n📁 Archivos descargados:")
        for asset in downloaded:
            print(f"   - {asset['local_file']} ({format_file_size(asset['file_size'])})")

    if failed:
        print("\n⚠️  Archivos que fallaron:")
        for f in failed:
            print(f"   - {f}")

    # Actualizar README
    if downloaded:
        update_readme(downloaded)

    print("\n" + "=" * 60)
    print("🎉 ¡Proceso completado!")
    print("=" * 60)
    print("\n💡 Próximos pasos:")
    print("   1. Abre Android Studio")
    print("   2. Click derecho en 'res' → 'Reload from Disk' (o Sync Project)")
    print("   3. Build → Rebuild Project")
    print("   4. Verifica que R.drawable.* funcione correctamente")
    print()

    return 0 if not failed else 1


if __name__ == "__main__":
    sys.exit(main())

