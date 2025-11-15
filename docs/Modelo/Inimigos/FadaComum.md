# Fada Comum (Padrão de Inimigos)

Não existe uma classe `FadaComum` única. Em vez disso, o projeto usa várias classes (`FadaComum1`, `FadaComum2`, etc.) que herdam diretamente de `Inimigo`. Cada uma implementa um comportamento de IA (movimento e ataque) ligeiramente diferente, mas seguem um padrão geral.

## FadaComum1

Esta é a fada mais simples.

- **Movimento**: Entra na tela, move-se para uma posição `targetY` enquanto oscila horizontalmente (usando uma função seno), e depois sai da tela.
- **Ataque**: Enquanto está na posição de alvo, atira um único projétil em direção ao herói em intervalos regulares.

## FadaComum2

Esta fada usa o sistema de `Estado` da classe `Inimigo` para criar um comportamento mais complexo.

- **Movimento**: Segue uma sequência de estados `IrPara` para se mover para diferentes pontos da tela.
- **Ataque**: Utiliza um estado customizado `MovimentoAtirando`, que combina a lógica de `IrPara` com a capacidade de disparar projéteis em um padrão circular em intervalos regulares enquanto se move.

## FadaComum3

Esta fada também usa o sistema de `Estado` para um padrão de ataque chamado "Pressão Dupla".

- **Movimento**: Move-se para uma posição e permanece lá.
- **Ataque**: Executa uma sequência de ataques:
    1.  `AtaqueEmLequeMirado`: Dispara um leque de projéteis lentos e grandes.
    2.  `Esperar`: Faz uma pequena pausa.
    3.  `AtaqueEmLequeMirado`: Dispara um leque de projéteis mais rápidos e numerosos.
    4.  `Esperar`: Faz uma pausa mais longa antes de sair da tela.
    
    Uma característica notável é que o ataque é sempre direcionado para baixo (ângulo de 90 graus), independentemente da posição do herói.

## FadaComum4

Esta classe parece ser um placeholder ou um trabalho em andamento, pois sua lógica de `atualizar()` está praticamente vazia.