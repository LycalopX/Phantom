# Classe `CarregadorDeDefinicoes`

**Pacote:** `Auxiliar.Projeteis.Definicoes`

## Descrição

Implementa um Singleton que adota uma abordagem data-driven para gerenciar projéteis. Ele lê e analisa o arquivo `definicoes_projeteis.json` uma vez no início do jogo, carregando as propriedades de todos os tipos de projéteis inimigos.

## Métodos Principais

### `getInstance()`
*   **@brief** Retorna a instância única (Singleton) do carregador.

### `getDefinicaoProjetil(String id)`
*   **@brief** Retorna um objeto `DefinicaoProjetil` que contém todas as propriedades de um projétil (coordenadas do sprite, tamanho, tipo de hitbox, etc.) com base em seu ID.

### `getSpritesheetPath(String id)`
*   **@brief** Retorna o caminho do arquivo de imagem para um spritesheet com base em seu ID.
