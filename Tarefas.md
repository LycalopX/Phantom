### A. Requisitos Essenciais (Obrigatórios pelo PDF)

1.  **Implementar Vidas, Dano e "Game Over" (Requisito 3 do PDF)**
    *   **Seu Status:** ✅ CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** A `Engine` gerencia os estados `JOGANDO`, `RESPAWNANDO` e `GAME_OVER`. A classe `Hero` tem `HP`, `takeDamage()` e `processarMorte()`. A mecânica de *Deathbomb* também está presente.

2.  **Criar um Gerenciador de Fases (Requisito 1 e 4 do PDF)**
    *   **Seu Status:** 🟡 PARCIALMENTE CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** O `GerenciadorDeFases` existe e carrega 5 `ScriptDeFase` diferentes. A `Engine` tem um método `carregarProximaFase()`, mas ele nunca é chamado. Falta a condição de término da fase para acionar a transição.

3.  **Adicionar Inimigos por Drag-and-Drop (Requisito 6 do PDF)**
    *   **Seu Status:** ❌ NÃO IMPLEMENTADO
    *   **Minha Análise:** ✅ **Confirmo.** Não há nenhuma importação de `java.awt.dnd` ou código relacionado a arrastar arquivos `.zip` para a janela do jogo.

4.  **Salvar e Carregar Jogo (Requisito 5 do PDF)**
    *   **Status (Adicionado por mim):** 🟡 **PARCIALMENTE CONCLUÍDO**
    *   **Minha Análise:** Este é um requisito **obrigatório** do PDF que não estava na sua lista. O código para salvar/carregar com as teclas 'P' e 'R' existe na `Engine`, usando serialização para o arquivo `POO.dat`. Contudo, a tecla de carregar (`KEY_LOAD`) está mapeada para 'R', a mesma do `KEY_RESTART`, o que é um **bug**. A tecla de carregar deveria ser 'L' como o comentário no código sugere.

---

### B. Recursos de Polimento (Funcionalidades do Jogo)

5.  **Modo "Foco" (Tecla Shift)**
    *   **Seu Status:** ✅ CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** `ControladorDoHeroi` detecta `KeyEvent.VK_SHIFT` e reduz a velocidade pela metade.

6.  **IA de Inimigos e Padrões de Tiro (Danmaku)**
    *   **Seu Status:** 🟡 PLANEJAMENTO CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** A `FadaComum` tem uma máquina de estados simples e agora atira na direção do herói. É uma base sólida para criar padrões mais complexos.

7.  **Bomba em Duas Fases**
    *   **Seu Status:** 🟡 PARCIALMENTE CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** A `BombaProjetil` existe e o método `deactivate()` dela já chama `lancarMisseis()`, que por sua vez pega `ProjetilBombaHoming` da pool. A base está toda aí, precisando apenas de ajuste fino e da lógica do "Last Spell".

8.  **Itens e Coletáveis (Power-ups)**
    *   **Seu Status:** 🟡 PARCIALMENTE CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** O sistema de drop e coleta funciona. A auto-coleta quando o herói está no topo da tela não está implementada.

9.  **Efeitos Sonoros e Outras Utilidades**
    *   **Seu Status:** ❌ NÃO IMPLEMENTADO
    *   **Minha Análise:** 🟡 **CORREÇÃO.** O status aqui é parcialmente concluído. O `SoundManager` **está implementado** e funcionando, carregando e tocando tanto SFX quanto música. Faltam as funcionalidades de Pausa e Screenshot.

10. **Backgrounds Diferentes por Fase**
    *   **Seu Status:** ❌ NÃO IMPLEMENTADO
    *   **Minha Análise:** ✅ **Confirmo.** O `Fase.carregarRecursos()` carrega um caminho fixo.

---

### **Minha Recomendação (Nova Ordem de Prioridade)**

1.  **Corrigir o Bug de Save/Load (Tarefa 4):** **Prioridade máxima.** É um requisito obrigatório e um bug simples de resolver. A tecla para carregar o jogo (`KEY_LOAD`) deve ser diferente da de reiniciar. Proponho usar 'L', como o comentário no código sugere.

2.  **Implementar Transição de Fase (Tarefa 2):** **Prioridade alta.** Também obrigatório. Podemos definir uma condição simples para teste, como `distanciaTotalRolada > 5000`, para chamar `engine.carregarProximaFase()`.

3.  **IA de Inimigos e Padrões de Tiro (Tarefa 6):** Continua sendo a prioridade principal de gameplay. Após as correções obrigatórias, devemos focar 100% aqui.

4.  **Drag-and-Drop (Tarefa 3):** Concordo em deixar por último. É o mais complexo e isolado.

5.  **Finalizar a Bomba (Tarefa 7):** Implementar a Fase 2 (mísseis) da bomba e a lógica do "Last Spell". Isso pode ser feito a qualquer momento, pois é um polimento da mecânica existente.


### **Planejamento de Implementação e Correções (Adicionado em 20/10/2025)**

#### **1. Correções de Bugs Críticos (Bugs)**

*   **Bombas teleguiadas despawnam ao sair da tela:**
    *   **Problema:** `ProjetilBombaHoming` desaparece se sair dos limites da tela, impedindo que retorne para atingir alvos.
    *   **Análise:** O comportamento de despawn provavelmente é herdado da classe `Personagem.java`, que remove objetos fora da tela.
    *   **Ação Sugerida:** Sobrescrever o método de verificação de limites em `ProjetilBombaHoming.java` para que ele só seja removido após um tempo de vida ou após a explosão, em vez de simplesmente sair da tela.

*   **Projéteis não são limpos na morte do herói:**
    *   **Problema:** Quando o herói morre, a tela continua poluída com projéteis inimigos.
    *   **Análise:** O fluxo de morte do herói em `ControleDeJogo.java` precisa de uma etapa adicional.
    *   **Ação Sugerida:** No bloco de código que trata a morte do `Hero.java`, adicionar uma chamada para `ProjetilPool.getInstancia().removerTodosInimigos()` (ou um método similar) para limpar a tela.

---

#### **2. Ajustes de Gameplay e Balanceamento (Gameplay & Balancing)**

*   **Aumentar hitbox da bomba teleguiada:**
    *   **Problema:** A hitbox de `ProjetilBombaHoming` é muito pequena.
    *   **Ação Sugerida:** No construtor de `ProjetilBombaHoming.java`, localizar onde as dimensões da hitbox (largura/altura ou raio) são definidas e multiplicar esses valores por 3.

---

#### **3. Melhorias Visuais e de Áudio (Visuals & Audio)**

*   **Adicionar sprite na hitbox ao focar:**
    *   **Problema:** Falta um feedback visual claro para a hitbox do herói quando ele está no modo de foco.
    *   **Análise:** O estado de foco é gerenciado em `Hero.java`. A renderização ocorre em `Tela.java`.
    *   **Ação Sugerida:**
        1.  Carregar uma imagem (`sprite_hitbox.png`) em `Hero.java`.
        2.  No método de desenho do herói (provavelmente chamado por `Tela.java`), verificar se `hero.isFocusing()`.
        3.  Se estiver focando, desenhar a imagem `sprite_hitbox` na posição do herói.

*   **Aumentar amplitude da pulsação visual da bomba:**
    *   **Problema:** O efeito visual de pulsação da bomba esférica é pouco perceptível.
    *   **Análise:** A animação de pulsação em `BombaProjetil.java` (ou classe visual correspondente) provavelmente usa uma função senoidal (`Math.sin`) para alterar o tamanho do sprite.
    *   **Ação Sugerida:** Aumentar a amplitude da função senoidal em 200% no método de desenho. **Importante:** Garantir que essa alteração afete apenas o tamanho visual do sprite, e não o tamanho da hitbox de colisão.

---

#### **4. Novas Funcionalidades e Refinamentos (New Features / Refinements)**

*   **Adicionar "morte" animada para projéteis:**
    *   **Problema:** Projéteis desaparecem abruptamente da tela.
    *   **Análise:** Atualmente, os projéteis são simplesmente desativados e retornados ao `ProjetilPool`.
    *   **Ação Sugerida:**
        1.  Adicionar um estado `MORRENDO` e um contador de animação à classe `Projetil.java`.
        2.  Criar um método `morrer()` em `Projetil.java`. Em vez de remover o projétil instantaneamente, este método o colocaria no estado `MORRENDO`.
        3.  No método de desenho do projétil, se ele estiver no estado `MORRENDO`, desenhar uma animação (ex: fade out, encolher, ou um pequeno sprite de explosão de `effect_projectiles.png`).
        4.  Quando a animação terminar, o projétil é finalmente retornado ao `ProjetilPool`.