# Inimigo

`Inimigo` é uma classe abstrata que herda de `Personagem` e serve como base para todas as entidades hostis do jogo.

## Propriedades Adicionais

Além das propriedades herdadas de `Personagem`, `Inimigo` adiciona:

- **`vida`**: A quantidade de dano que o inimigo pode sofrer antes de ser destruído.
- **`lootTable`**: Uma `LootTable` que define quais itens o inimigo pode dropar ao ser derrotado.
- **`faseReferencia`**: Uma referência à `Fase` atual, necessária para que o inimigo possa interagir com o ambiente (como acessar a `ProjetilPool` ou encontrar o `Hero`).

## Sistema de Estados

A classe `Inimigo` introduz um sistema de máquina de estados simples para controlar o comportamento (IA).

- **`Estado`**: Uma classe interna abstrata que define a interface para todos os estados (ex: `incrementarTempo`, `getProximoEstado`).
- **`processarEstado(...)`**: Um método que avança o estado atual, verifica se ele foi concluído e, em caso afirmativo, passa para o próximo estado na sequência.
- **Implementações de Estado**: A classe já fornece implementações básicas como `Esperar` (pausa por um tempo) e `IrPara` (move o inimigo para uma coordenada específica). Inimigos mais complexos definem seus próprios estados customizados (ex: `AtaqueEmLequeMirado` na `FadaComum3`).

## Métodos Notáveis

| Método | Retorno | Descrição |
|---|---|---|
| `takeDamage(double damage)` | `void` | Reduz a vida do inimigo e o desativa se a vida chegar a zero. |
| `getAnguloEmDirecaoAoHeroi()` | `double` | Um método de utilidade que calcula o ângulo (em graus) do inimigo em direção à posição atual do herói. |
| `initialize(Fase fase)` | `void` | Define a referência da fase, crucial para o funcionamento do inimigo após a desserialização. |
| `isStrafing()` | `abstract boolean` | Método abstrato que força as subclasses a definir a lógica que determina se a animação de "strafing" (movimento lateral) deve ser usada. |