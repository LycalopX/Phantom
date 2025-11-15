# SoundManager

`SoundManager` é uma classe Singleton responsável por todo o gerenciamento de áudio do jogo, utilizando a biblioteca `TinySound`. Ela pré-carrega todos os efeitos sonoros (SFX) e músicas para evitar atrasos durante o jogo.

## Design (Singleton)

O padrão Singleton garante que exista apenas uma instância do `SoundManager` em todo o jogo. Isso é crucial para o gerenciamento de áudio, pois evita que múltiplos gerenciadores tentem controlar o hardware de som simultaneamente. A instância é acessada através do método estático `getInstance()`.

A inicialização é feita através de `SoundManager.init()`, que deve ser chamado uma vez no início do jogo.

## Métodos Públicos

| Método | Retorno | Descrição |
|---|---|---|
| `init()` | `static void` | Inicializa o `TinySound` e o `SoundManager`. Deve ser chamado antes de qualquer outra operação de som. |
| `shutdown()` | `static void` | Desliga o sistema `TinySound`. |
| `getInstance()` | `static SoundManager` | Retorna a instância única do `SoundManager`. |
| `playSfx(String name, double volume)` | `void` | Toca um efeito sonoro pré-carregado com um volume específico. |
| `playMusic(String name, boolean loop)` | `void` | Para todas as outras músicas e toca uma nova música pré-carregada. |
| `stopAllMusic()` | `void` | Para a execução de todas as músicas. |
| `setSfxVolume(float volume)` | `void` | Define o volume global para todos os efeitos sonoros. |
| `setMusicVolume(float volume)` | `void` | Define o volume global para todas as músicas e ajusta o volume da música atualmente em execução. |