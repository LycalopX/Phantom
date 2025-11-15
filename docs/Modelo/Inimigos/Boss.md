# Boss

`Boss` é uma classe abstrata que herda de `Inimigo`, servindo como base para todos os chefes de fase.

## Funcionalidades Adicionais

A principal adição da classe `Boss` é a introdução de um sistema de **padrões de ataque** mais estruturado, implementado através de classes internas aninhadas.

- **`Ataque`**: Uma classe de `Estado` especializada que gerencia uma sequência de `PadraoAtaque`. Ela controla o `intervaloAtaque` e o `tipoProjetil` a ser usado.
- **`PadraoAtaque`**: Uma classe simples que define um único passo em um ataque, como a rotação e a quantidade de tiros.
- **`AtaqueEmLeque`**: Uma especialização de `Ataque` que implementa a lógica de atirar projéteis em um padrão de leque (spread).
- **`AtaqueEmLequeNaPosicao`**: Uma variação que dispara o leque a partir de uma posição fixa, em vez da posição atual do boss.

## Interação com Bombas

A classe também introduz um booleano `isBombed` para controlar a interação com as bombas do jogador, permitindo que um boss sofra dano de uma bomba apenas uma vez por uso.

## Implementação

Cada classe de chefe concreta (como `Nightbug` ou `Lorelei`) estende `Boss` e define sua própria sequência de estados de movimento e ataque, combinando os estados básicos de `Inimigo` (como `IrPara`) com os estados de ataque mais complexos fornecidos por `Boss`.