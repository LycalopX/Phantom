# ScriptDeFase

`ScriptDeFase` é uma classe abstrata que define o "roteiro" de uma fase. Enquanto a classe `Fase` armazena *o que* está na tela, a `ScriptDeFase` define *quando* e *como* as coisas acontecem. Cada fase do jogo (Fase 1, Fase 2, etc.) tem sua própria subclasse de `ScriptDeFase` (ex: `ScriptFase1`).

## Sistema de Ondas (Waves)

O coração do `ScriptDeFase` é o sistema de `Onda`. Uma fase é composta por uma lista de `Onda`s. Cada `Onda` contém uma sequência de `InimigoSpawn`, que define qual inimigo deve aparecer e após quanto tempo.

- **`Onda`**: Uma classe interna que gerencia um grupo de inimigos. Ela tem um temporizador e spawna o próximo inimigo da sua lista quando o tempo certo chega.
- **`InimigoSpawn`**: Uma pequena classe que associa um `Personagem` (o inimigo) a um tempo de espera.
- **`OndaDeEspera`**: Um tipo especial de onda que não spawna inimigos, apenas espera por um determinado tempo, criando pausas entre os ataques.
- **`OndaDeBoss`**: Um tipo de onda que termina apenas quando o boss contido nela é derrotado.

O método `atualizarInimigos` gerencia a progressão através da lista de ondas, passando para a próxima assim que a atual for finalizada.

## Métodos Abstratos

As subclasses de `ScriptDeFase` devem implementar:

| Método | Descrição |
|---|---|
| `carregarRecursos(Fase fase)` | Carrega os recursos visuais específicos da fase (imagens de fundo, etc.). |
| `relinkarRecursosDosElementos(Fase fase)` | Restaura as referências de imagens `transient` após a desserialização. |
| `inicializarOndas(Fase fase)` | Define e retorna a lista de `Onda`s que compõem o roteiro de inimigos da fase. |

## Outras Responsabilidades

- **Cenário**: Os métodos `atualizarCenario` e `preencherCenarioInicial` permitem que o script controle o spawning de elementos de cenário dinâmicos (como as árvores na Fase 1).
- **Estilo Visual**: Os métodos `getBackgroundOverlayColor` e `getBackgroundGradient` permitem que cada script defina um estilo visual único para sua fase.