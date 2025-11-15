# ProjetilPool

A classe `ProjetilPool` é um exemplo do padrão de projeto "Object Pool". Sua função é gerenciar coleções de objetos `Projetil` para evitar a sobrecarga de criar (`new`) e destruir (coletor de lixo) objetos em alta frequência, o que é comum em jogos com muitos tiros.

## Funcionamento

- **Inicialização**: No construtor, a `ProjetilPool` pré-aloca e cria um número fixo de instâncias para cada tipo de projétil (`Projetil`, `ProjetilHoming`, etc.) e as armazena em `ArrayList`s. Todos os projéteis começam como "inativos".
- **Requisição**: Quando uma arma precisa disparar, em vez de criar um novo projétil, ela chama um método como `getProjetilNormal()`. Este método varre a lista correspondente e retorna o primeiro projétil que encontrar com o estado `isActive() == false`.
- **Reciclagem**: O projétil retornado é então reconfigurado com uma nova posição, velocidade e ângulo usando seu método `reset()`. Quando o projétil sai da tela ou atinge um alvo, ele não é destruído; seu método `deactivate()` é chamado, o que simplesmente o marca como inativo, pronto para ser requisitado e reutilizado novamente.

## Piscinas Internas

A `ProjetilPool` mantém piscinas separadas para diferentes categorias de projéteis para facilitar o gerenciamento:
- `poolNormais` (tiro principal do herói)
- `poolHoming` (mísseis teleguiados do herói)
- `poolBombaHoming` (mísseis da bomba)
- `poolInimigos` (todos os projéteis de inimigos)

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `getProjetilNormal()` | `Projetil` | Retorna um projétil normal inativo da piscina. |
| `getProjetilHoming()` | `ProjetilHoming` | Retorna um projétil teleguiado inativo da piscina. |
| `getProjetilBombaHoming()` | `ProjetilBombaHoming` | Retorna um projétil de bomba inativo da piscina. |
| `getProjetilInimigo()` | `Projetil` | Retorna um projétil de inimigo inativo da piscina. |
| `getTodosOsProjeteis()` | `ArrayList<Projetil>` | Retorna uma lista única contendo todos os projéteis de todas as piscinas, usada para adicioná-los à lista principal de personagens da fase. |