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
    *   **Minha Análise:** 🟡 **CORREÇÃO.** O status aqui é parcialmente concluído. O `SoundManager` **está implementado** e funcionando, carregando e tocando tanto SFX quanto música. Faltam as funcionalidades de Pausa.

10. **Backgrounds Diferentes por Fase**
    *   **Seu Status:** ❌ NÃO IMPLEMENTADO
    *   **Minha Análise:** ✅ **Confirmo.** O `Fase.carregarRecursos()` carrega um caminho fixo.

---

#### **4. Novas Funcionalidades e Refinamentos (New Features / Refinements)**

// FINAL

*   **Adicionar "morte" animada para projéteis:**
    *   **Problema:** Projéteis desaparecem abruptamente da tela.
    *   **Análise:** Atualmente, os projéteis são simplesmente desativados e retornados ao `ProjetilPool`.
    *   **Ação Sugerida:**
        1.  Adicionar um estado `MORRENDO` e um contador de animação à classe `Projetil.java`.
        2.  Implementar no método `deactivate()` em `Projetil.java`. Em vez de remover o projétil instantaneamente, este método o colocaria no estado `MORRENDO`.
        3.  No método de desenho do projétil, se ele estiver no estado `MORRENDO`, desenhar uma animação (ex: fade out, encolher, ou um pequeno sprite de explosão de `effect_projectiles.png`).
        4.  Quando a animação terminar, o projétil é finalmente retornado ao `ProjetilPool`.



minhas anotações feias que precisam ser formatadas bonitinhas:
1. boss precisa ter diferentes hps - toda vez que ele estoura, passamos para uma nova fase:
comportamento do boss precisa ser definido por blocos de state diferentes, que modificam conforme sua fase.
