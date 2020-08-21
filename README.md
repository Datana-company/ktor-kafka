# Проект Датана.Смарт.UI

Это проект интерфейса пользователя для Датана-Смарт.

## Структура проекта

```plantuml
[Front Main|dsmart-ui-main]
[Backend Gateway|Websocket|dsmart-back-gateway]

[Front Main]->[<<Front Component>>;UI Temperature|dsmaprt-ui-temperature]<-->[Backend Gateway]

[Backend Gateway]<-->[<<Microservice>>; Back Temperature|dsmart-back-temperature]

[Front Main]->[<<Front Component>>;UI Events|dsmart-ui-events]<-->[Backend Gateway]
[Backend Gateway]<-->[<<Microservice>>;Back Events|dsmart-back-events]

[Front Main]->[<<Front Component>>;UI Messages|dsmart-ui-messages]<-->[Backend Gateway]
[Backend Gateway]<-->[<<Microservice>>;Back Messages|dsmart-back-messages]
```
