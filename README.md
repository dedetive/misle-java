# Misle

### Uso

Para abrir o jogo, navegue até o diretório misle-java-master/ em um terminal e execute `javac src/com/ded/misle/*.java src/com/ded/misle/boxes/*.java` para compilar o projeto. Em seguida, utilize `java -cp src com.ded.misle.Launcher` para abrir o projeto compilado. Alternativamente, use sua IDE de preferência com compilador para executar o código automaticamente.

Em misle-java-master/:
```
javac src/com/ded/misle/*.java src/com/ded/misle/boxes/*.java
java -cp src com.ded.misle.Launcher
```

Até o momento os controles são as setinhas para movimentar o quadrado branco, que é o jogador.

### Configurações

Para alterar as configurações padrões do jogo, navegue até o arquivo misle-java-master/src/com/ded/misle/resources/settings.config e mude os valores. O valor default é usado quando o valor encontrado é diferente de qualquer opção possível. AS opções disponíveis são:

```
screenSize (small, default=medium, big, huge, tv-sized, comical)
isFullscreen (default=false, true)
fullscreenMode (default=windowed, exclusive)
frameRateCap (1..144, default=60)
displayFPS (default=false, true)
```

O que está entre parênteses e descartado e apenas o que está após o sinal de = é levado em consideração. A função `displayFPS` atualmente não funciona e esteve disponível apenas nos testes iniciais, mas será implementada novamente posteriormente.
