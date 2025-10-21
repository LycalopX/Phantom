# Classe `ProjetilPool`

**Pacote:** `Modelo.Projeteis`

## Descrição

Implementa o padrão de projeto **Object Pool** para gerenciar projéteis de forma eficiente. Em vez de criar e destruir objetos de projéteis constantemente (o que é custoso e pode causar pausas do Garbage Collector), esta classe pré-instancia um grande número de projéteis no início do jogo e os reutiliza.

## Funcionamento

1.  **Inicialização**: No construtor, a classe cria várias listas (`ArrayList`) e as preenche com instâncias inativas de cada tipo de projétil (`Projetil`, `ProjetilHoming`, etc.).
2.  **Requisição**: Quando o jogo precisa de um projétil, ele chama um dos métodos `get...()` (e.g., `getProjetilNormal()`).
3.  **Reutilização**: O método percorre a lista correspondente e retorna o primeiro projétil que encontrar com o estado `isActive = false`.
4.  **Reset**: O projétil retornado tem seu estado reiniciado pelo método `reset()` com novas coordenadas, velocidade e ângulo, e é então ativado.

## Métodos Principais

### `ProjetilPool(...)`
*   **@brief** Construtor que inicializa e pré-aloca as piscinas de objetos para cada tipo de projétil.

### `getTodosOsProjeteis()`
*   **@brief** Retorna uma única lista contendo todos os projéteis de todas as piscinas, para que possam ser adicionados à lista principal de renderização e processamento da fase.

### `getProjetilNormal()` / `getProjetilHoming()` / etc.
*   **@brief** Métodos para requisitar um projétil inativo de uma piscina específica.
