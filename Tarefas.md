### A. Requisitos Essenciais (Obrigatórios pelo PDF)

2.  **Criar um Gerenciador de Fases (Requisito 1 e 4 do PDF)**
    *   **Seu Status:** 🟡 PARCIALMENTE CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** O `GerenciadorDeFases` existe e carrega 5 `ScriptDeFase` diferentes. A `Engine` tem um método `carregarProximaFase()`, mas ele nunca é chamado. Falta a condição de término da fase para acionar a transição.

---

### B. Recursos de Polimento (Funcionalidades do Jogo)

6.  **IA de Inimigos e Padrões de Tiro (Danmaku)**
    *   **Seu Status:** 🟡 PLANEJAMENTO CONCLUÍDO
    *   **Minha Análise:** ✅ **Confirmo.** A `FadaComum` tem uma máquina de estados simples e agora atira na direção do herói. É uma base sólida para criar padrões mais complexos.

9.  **Efeitos Sonoros e Outras Utilidades**
    *   **Seu Status:** ❌ NÃO IMPLEMENTADO
    *   **Minha Análise:** 🟡 **CORREÇÃO.** O status aqui é parcialmente concluído. O `SoundManager` **está implementado** e funcionando, carregando e tocando tanto SFX quanto música. Faltam as funcionalidades de Pausa e Screenshot.

10. **Backgrounds Diferentes por Fase**
    *   **Seu Status:** ❌ NÃO IMPLEMENTADO
    *   **Minha Análise:** ✅ **Confirmo.** O `Fase.carregarRecursos()` carrega um caminho fixo.

---

### **Planejamento de Implementação e Correções (Adicionado em 20/10/2025)**

#### **1. Correções de Bugs Críticos (Bugs)**

*   **Projéteis não são limpos na morte do herói:**
    *   **Problema:** Quando o herói morre, a tela continua poluída com projéteis inimigos.
    *   **Análise:** O fluxo de morte do herói em `ControleDeJogo.java` precisa de uma etapa adicional.
    *   **Ação Sugerida:** No bloco de código que trata a morte do `Hero.java`, adicionar uma chamada para `ProjetilPool.getInstancia().removerTodosInimigos()` (ou um método similar) para limpar a tela.

---

#### **3. Melhorias Visuais e de Áudio (Visuals & Audio)**

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