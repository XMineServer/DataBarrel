# О плагине
Кор плагин, который предоставляет остальным доступ к базам данным/кэшам/хранилищам и т.п.
В нем хранится вся информация о конфигурации, преоставляя готовые подключения по названию.

# Примеры использования

Пример подключения базы данных на этапе запуска плагина лежит в SamplePlugin

Пример подключения базы данных на этапе инициализации плагина лежит в SampleBootstrapPlugin

# Статус

Плагин находится в разработке, сейчас реализованы только подключения к mysql базе данных.
## Поддерживаемые подключения:
### SQL-СУБД (mysql, postgres, h2)
Используется пул соединений HikariCP (по умолчанию – MySQL).<br>
Работа с подключениями происходит через HikariDataSource
### Redis
Используется библиотека Jedis. Возможно использование отдельного подключения и кластера.<br>
Работа с подключениями происходит через UnifiedJedis
### ZooKeeper
Используется библиотека Apache Curator.<br>
Работа с подключениями происходит через CuratorFramework
### S3
Используется библиотека AWS SDK.<br>
Работа с подключениями происходит через S3Client

# Подключение библиотеки
## gradle

укажите ваш username и токен, который должен обладать правом read:packages <br>
создать токен можно здесь https://github.com/settings/tokens/new <br>

`gradle.properties`
```
gpr.user=username
gpr.token=ghp_token
```

`build.gradle.kts`
```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/xmineserver/databarrel")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
dependencies {
    implementation("org.skyisland:databarrel:1.0.0")
}
```

# Для разработчиков
Для создания новой версии библиотеки нужно поменять версию
в `./Plugin/build.gradle.kts` и создать новый тег:
```
git tag v1.0.1
git push origin v1.0.1
```

 
