# GerenciadorDeArmasHeroi

Este é outro componente da classe `Hero`, focado exclusivamente na lógica de armamentos do jogador.

## Funcionalidades

- **Gerenciamento de Cooldown**: Mantém contadores de `cooldown` para o tiro principal e para os mísseis, garantindo uma cadência de tiro controlada. O método `atualizarTimers()` é chamado a cada frame para decrementar esses contadores.
- **Lógica de Disparo**: O método `disparar()` é o coração da classe. Ele verifica os cooldowns e o nível de poder (`power`) do herói para determinar:
    - Quantos projéteis principais disparar.
    - O ângulo e a velocidade desses projéteis.
    - Se mísseis teleguiados devem ser disparados.
- **Cálculo de Nível**: O método `getNivelTiro()` calcula o "nível" da arma com base na quantidade de `power` acumulada pelo jogador. Esse nível influencia diretamente o número de projéteis, a cadência e a ativação dos mísseis.
- **Criação de Projéteis**: Quando um tiro é disparado, o gerenciador solicita um projétil reciclável da `ProjetilPool` da fase e o configura (`reset`) com a posição, velocidade e tipo corretos.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `atualizarTimers()` | `void` | Atualiza os contadores internos de cooldown. Deve ser chamado a cada frame. |
| `disparar(double x, double y, int power, Fase fase)` | `void` | Executa a lógica de disparo, criando projéteis com base no nível de poder e nos cooldowns. |
| `getNivelTiro(int power)` | `int` | Calcula o nível de tiro do herói com base em seu poder. |