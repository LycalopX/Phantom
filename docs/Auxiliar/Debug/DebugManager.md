# DebugManager

`DebugManager` é uma classe de utilidade simples que gerencia um estado booleano global para o modo de depuração (debug) do jogo.

## Funcionalidade

A classe possui um único campo estático `active` e dois métodos estáticos para interagir com ele:

- **`toggle()`**: Inverte o estado do modo de depuração (de `true` para `false` e vice-versa) e imprime o novo estado no console.
- **`isActive()`**: Permite que outras partes do código verifiquem se o modo de depuração está ativo para, por exemplo, desenhar informações extras na tela (como hitboxes ou contadores de FPS).

Isso centraliza o controle do modo de debug, facilitando sua ativação e desativação em todo o projeto.

## Métodos Públicos

| Método | Retorno | Descrição |
|---|---|---|
| `toggle()` | `static void` | Alterna o estado do modo de depuração (ativo/inativo). |
| `isActive()` | `static boolean` | Verifica se o modo de depuração está atualmente ativo. |