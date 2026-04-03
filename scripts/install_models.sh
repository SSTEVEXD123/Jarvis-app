#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="${1:-./JarvisApp}"
MODELS_DIR="$ROOT_DIR/models"
TMP_DIR="$MODELS_DIR/.tmp"
mkdir -p "$MODELS_DIR" "$TMP_DIR"

HF_TOKEN="${HF_TOKEN:-}"
CURL_HEADERS=( -H "User-Agent: JarvisApp-Installer/1.1" )
if [[ -n "$HF_TOKEN" ]]; then
  CURL_HEADERS+=( -H "Authorization: Bearer ${HF_TOKEN}" )
fi

# Formato: nombre|archivo_destino|min_bytes|url1,url2,...
MODELS=(
  "tinyllama-1.1b|tinyllama-1.1b.Q4_K_M.gguf|100000000|https://huggingface.co/ggml-org/models/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf,https://hf-mirror.com/ggml-org/models/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"
  "qwen2.5-1.5b|qwen2.5-1.5b-q4_k_m.gguf|100000000|https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf,https://hf-mirror.com/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf"
  "moondream2|moondream2-q4_k_m.gguf|100000000|https://huggingface.co/vikhyatk/moondream2/resolve/main/model-q4_k_m.gguf,https://hf-mirror.com/vikhyatk/moondream2/resolve/main/model-q4_k_m.gguf"
  "stable-diffusion|stable-diffusion-model_index.json|100|https://huggingface.co/stabilityai/sd-turbo/resolve/main/model_index.json,https://hf-mirror.com/stabilityai/sd-turbo/resolve/main/model_index.json"
)

download_from_sources() {
  local name="$1"
  local dest="$2"
  local min_bytes="$3"
  local urls_csv="$4"
  local part="$TMP_DIR/${dest}.part"

  if [[ -f "$dest" ]]; then
    local existing_size
    existing_size=$(wc -c < "$dest" | tr -d ' ')
    if [[ "$existing_size" -ge "$min_bytes" ]]; then
      echo "[skip] $name ya existe ($(numfmt --to=iec "$existing_size"))"
      return 0
    fi
    rm -f "$dest"
  fi

  IFS=',' read -r -a urls <<< "$urls_csv"
  for url in "${urls[@]}"; do
    echo "[download] $name desde: $url"
    if curl -L --fail --retry 6 --retry-all-errors --connect-timeout 20 --max-time 0 -C - "${CURL_HEADERS[@]}" "$url" -o "$part"; then
      local size
      size=$(wc -c < "$part" | tr -d ' ')
      if [[ "$size" -ge "$min_bytes" ]]; then
        mv "$part" "$dest"
        echo "[ok] $name descargado ($(numfmt --to=iec "$size"))"
        return 0
      fi
      echo "[warn] Descarga incompleta para $name ($size bytes)"
    else
      echo "[warn] Falló $name desde $url"
    fi
  done

  rm -f "$part"
  return 1
}

failed=0
for model_entry in "${MODELS[@]}"; do
  IFS='|' read -r name file min_bytes urls <<< "$model_entry"
  if ! download_from_sources "$name" "$MODELS_DIR/$file" "$min_bytes" "$urls"; then
    echo "[error] No se pudo descargar $name"
    failed=1
  fi
  echo "---"
done

if [[ "$failed" -ne 0 ]]; then
  echo "[fatal] Hubo errores descargando modelos."
  echo "Sugerencias: exporta HF_TOKEN, reintenta con red estable o usa espejo permitido."
  exit 1
fi

echo "Modelos disponibles en: $MODELS_DIR"
