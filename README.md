# Misle

Para abrir o jogo, navegue até o diretório Misle/ em um terminal e execute `javac src/com/ded/misle/*.java src/com/ded/misle/boxes/*.java` para compilar o projeto. Em seguida, utilize `java -cp src com.ded.misle.Launcher` para abrir o projeto compilado. Alternativamente, navegue até o diretório /src/com/ded/misle/ e execute diretamente o .java com `java Launcher.java`.

Até o momento os controles são as setinhas para movimentar o quadrado branco, que é o jogador.

Para alterar as configurações padrões do jogo, navegue até o arquivo resources/settings.config e mude os valores. O valor default é usado quando o valor encontrado é diferente de qualquer opção possível. AS opções disponíveis são:

screenSize (small, default=medium, big, huge, tv-sized, comical)
isFullscreen (default=false, true)
fullscreenMode (default=windowed, exclusive)
frameRateCap (1..144, default=60)
displayFPS (default=false, true)

O que está entre parênteses e descartado e apenas o que está após o sinal de = é levado em consideração. A função `displayFPS` atualmente não funciona e esteve disponível apenas nos testes iniciais, mas será implementada novamente posteriormente.
