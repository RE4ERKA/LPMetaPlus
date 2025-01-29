
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.RE4ERKA:LPMetaPlus:1.0.0'
}
```
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
    <dependencies>
	    <dependency>
	        <groupId>com.github.RE4ERKA</groupId>
	        <artifactId>LPMetaPlus</artifactId>
	        <version>1.0.0</version>
	    </dependency>
    </dependencies>
```

Пример работы с API:
```java
public void test() {
  // Получаем экземпляр API плагина.
  final LPMetaPlusAPI api = LPMetaPlusAPI.getInstance();
  final String name = "RE4ERKA";

  // Асинхронная работа с сессией с поиском по нику.
  // Работает с любыми оффлайн игроками.
  api.openSession(name).thenAccept(session -> {
    try (Session ignored = session) {
      testSession(session);
    }
  });
 
  // Работа с сессией для онлайн игроков.
  try (MetaSession session : api.openSession(player)) {
    testSession(session);
  }

  final Key key = Key.of("RUBIES");
  // Более короткие методы.
  final int balance = api.getBalance(player, key);
 
  api.set(player, key, 500);
  api.give(player, key, 45);
  api.take(player, key, 1);
}

private void testSession(@NotNull MetaSession session) {
  // Получить количество рубинов.
  final Key key = Key.of("RUBIES");
  final boolean silent = false;

  final int balance = session.get(key);

  // Изменить количество рубинов.
  session.edit(editor -> {
    editor.set(key, 10);
    editor.give(key, 99);
    editor.take(key, 33);
  }, silent);
}
```

Обязательно используем try-catch блоки, они обезопасят наш код от утечки памяти если вдруг во время выполнения кода произойдет ошибка. Все изменения нужно, также, обязательно делать в блоке с передаваемой переменной editor, чтобы избежать избыточных обращений к базе данных.
