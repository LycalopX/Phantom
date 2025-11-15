# Classe `Boss`

**Pacote:** `Modelo.Inimigos`

## Descrição

Classe abstrata que serve como base para todos os chefes (Bosses) do jogo. Herda da classe `Inimigo` e adiciona funcionalidades específicas de chefes, como um sistema de estados para ataques e a capacidade de ser "bombardeado" (atingido por bombas do herói).

## Herança

`Boss` herda de `Inimigo`.

## Atributos Principais

*   **`isBombed`**: Um flag booleano que indica se o chefe foi atingido por uma bomba do herói. Isso é usado para controlar a lógica de dano de bomba, geralmente permitindo que um chefe seja atingido por uma bomba apenas uma vez por ativação.

## Métodos Principais

### `Boss(...)`
*   **@brief** Construtor que inicializa o chefe com sua imagem, posição, tabela de loot e vida.

### `setBombed(boolean bombed)`
*   **@brief** Define o estado de "bombardeado" do chefe.

### `isBombed()`
*   **@brief** Retorna `true` se o chefe foi marcado como "bombardeado", `false` caso contrário.

## Classes Internas Abstratas (Estados de Ataque)

A classe `Boss` define uma estrutura para gerenciar diferentes padrões de ataque através de classes internas abstratas que estendem o conceito de `Estado` (herdado de `Inimigo`).

### `Ataque`
*   **@brief** Classe abstrata base para todos os estados de ataque do chefe. Gerencia o intervalo entre ataques, a velocidade dos projéteis e uma lista de padrões de ataque (`PadraoAtaque`).
*   **Atributos:** `intervaloAtaque`, `velocidadeProjetil`, `padroes` (lista de `PadraoAtaque`), `padraoAtual`, `tipoProjetil`.
*   **Métodos:** `incrementarTempo()`, `atirar()` (abstrato).

#### `PadraoAtaque` (Classe interna de `Ataque`)
*   **@brief** Define um padrão de ataque específico, incluindo rotação e quantidade de ataques.

### `AtaqueEmLeque`
*   **@brief** Estende `Ataque` para implementar padrões de tiro em leque.
*   **Métodos:** `atirarEmLeque()` (implementa a lógica de disparo em leque).

#### `PadraoLeque` (Classe interna de `AtaqueEmLeque`)
*   **@brief** Estende `PadraoAtaque` adicionando um atributo `amplitude` para o leque.

### `AtaqueEmLequeNaPosicao`
*   **@brief** Estende `AtaqueEmLeque` para permitir que os ataques em leque sejam disparados de uma posição específica (não necessariamente a do chefe).
*   **Atributos:** `posicaoAtaque`.

## Uso

As subclasses concretas de `Boss` (como `Nightbug`, `Lorelei`, `Keine`, `Reimu`, `Reisen`) implementam os métodos abstratos e utilizam essas classes internas para definir suas sequências de movimento e padrões de ataque complexos.
