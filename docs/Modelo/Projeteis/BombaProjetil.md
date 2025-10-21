# Classe `BombaProjetil`

**Pacote:** `Modelo.Projeteis`

## Descrição

Representa o efeito da bomba do herói. Esta classe não é um projétil no sentido tradicional, mas sim um efeito de área que limpa projéteis inimigos e causa dano.

## Comportamento

1.  **Expansão**: Ao ser ativada, a bomba cria uma área de dano circular que se expande rapidamente a partir da posição do herói.
2.  **Dano e Limpeza**: Qualquer inimigo ou projétil inimigo dentro deste raio é destruído.
3.  **Lançamento de Mísseis**: Ao final de sua curta duração, a bomba é desativada e lança uma barragem de `ProjetilBombaHoming` em um padrão circular.

## Métodos Principais

### `BombaProjetil(...)`
*   **@brief** Construtor que inicializa o efeito da bomba na posição do herói.

### `atualizar()`
*   **@brief** Atualiza o raio de expansão da bomba a cada frame.

### `deactivate()`
*   **@brief** Sobrescreve o método padrão para garantir que a barragem de mísseis seja lançada no momento em que a bomba é desativada.
