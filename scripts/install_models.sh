#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="${1:-./JarvisApp}"
MODELS_DIR="$ROOT_DIR/models"
mkdir -p "$MODELS_DIR"

fetch_model() {
  local name="$1"
  local url="$2"
  local out="$MODELS_DIR/$3"
  if [[ -f "$out" ]]; then
    echo "[skip] $name ya existe"
    return
  fi
  echo "[download] $name"
  curl -L --fail --retry 3 "$url" -o "$out"
}

fetch_model "llama.cpp tinyllama" "https://huggingface.co/ggml-org/models/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf" "tinyllama-1.1b.Q4_K_M.gguf"
fetch_model "Qwen2.5" "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf" "qwen2.5-1.5b-q4_k_m.gguf"
fetch_model "Moondream2" "https://huggingface.co/vikhyatk/moondream2/resolve/main/model-q4_k_m.gguf" "moondream2-q4_k_m.gguf"
fetch_model "Stable Diffusion metadata" "https://huggingface.co/stabilityai/sd-turbo/resolve/main/model_index.json" "stable-diffusion-model_index.json"

echo "Modelos descargados en: $MODELS_DIR"
