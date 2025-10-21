# Classe `SoundManager`

**Pacote:** `Auxiliar`

## Descrição

Implementa um Singleton para gerenciar o carregamento e a reprodução de todos os recursos de áudio do jogo, utilizando a biblioteca `TinySound`.

## Métodos Principais

### `init()`
*   **@brief** Método estático que inicializa o `TinySound` e a instância do `SoundManager`, e pré-carrega todos os arquivos de som e música definidos.

### `getInstance()`
*   **@brief** Retorna a instância única (Singleton) do `SoundManager`.

### `playSfx(String name, double volume)`
*   **@brief** Reproduz um efeito sonoro (SFX) a partir do mapa de sons pré-carregados.

### `playMusic(String name, boolean loop)`
*   **@brief** Para todas as outras músicas e reproduz uma nova música a partir do mapa de músicas pré-carregadas.

### `setSfxVolume(float volume)` / `setMusicVolume(float volume)`
*   **@brief** Ajustam o volume global para efeitos sonoros e músicas, respectivamente.
