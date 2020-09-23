# Проект Датана.Смарт.UI

Это проект интерфейса пользователя для Датана-Смарт.

## Структура проекта

```plantuml
@startuml

actor User
(browser) as browser
(WebsocketInterfaces) as ws
(MQ) <<Kafka>> as mq

package "Frontend" as front {
  [UI Frontend] <<Home>> as umain
  (Front comp) <<Temperature>> as utemp
  (Front comp) <<Vibrations>> as uvib
  (Front comp) <<Camera>> as ucam
  (Front comp) <<Other>> as uoth

  umain -up- uvib
  umain -down- ucam
  uvib -up- utemp
  ucam -down- uoth
}


package "Backend" as back {
  [UI Backend] <<Gateway>> as bgate
  (Back module) <<Temperature>> as btemp
  (Back module) <<Vibrations>> as bvib
  (Back module cam) <<Camera>> as bcam
  (Back module other) <<Other>> as both

  bgate -up- bvib
  bgate -down- bcam
  bvib -up- btemp
  bcam -down- both
}



User -right- browser
umain <-left- browser
umain -right-> ws
ws -right-> bgate
bgate -right-> mq

utemp .left.> btemp
uvib  .left.> bvib
ucam  .left.> bcam
uoth  .left.> both

@enduml
```

## Модули

1. [`dmart-common`](dsmart-common/README.md) - главный компонент интерфейса пользователя, который управляет всеми 
компонентами UI.
1. [`dmart-ui-main`](dsmart-ui-main/README.md) - корневой компонент (Home) интерфейса пользователя, который управляет всеми 
компонентами UI.
1. [`dmart-module-temperature`](dsmart-module-temperature/README.md) - модуль для датчика температуры

## Сборка и деплой проекта

Компиляция проекта:
```bash
./gradlew build
```

Локальное построение образа:
```bash
# Опционально. При наличии, будет в 
export DOCKER_REGISTRY_HOST=registry.datana.ru

./gradlew build
```

Деплой проекта в реестр докеров:
```bash
#export DOCKER_REGISTRY_PORT=
export DOCKER_REGISTRY_HOST=registry.datana.ru
export DOCKER_REGISTRY_USER=admin
export DOCKER_REGISTRY_PASS=*****
./gradlew deploy
```

## Запуск проекта

Из докера:
```bash
 docker run -p 8080:8080 
```
