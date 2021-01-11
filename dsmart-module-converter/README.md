# `dsmart-module-converter`

Это группа модулей для отображения конвертера и цифрового советчика на рабочем месте оператора

## Модули
1. [`dsmart-module-converter-app`](dsmart-module-converter-app/README.md) - основной модуль с приложением конвертера
2. [`dsmart-module-converter-backend`](dsmart-module-converter-backend/README.md) - модуль, содержащий бизнес-логику приложения 
3. [`dsmart-module-converter-common`](dsmart-module-converter-common/README.md) - модуль, содержащий различные модели и интерфейсы для бизнес-логики и репозитория
4. [`dsmart-module-converter-repo-inmemory`](dsmart-module-converter-repo-inmemory/README.md) - модуль с реализацией in-memory репозитория
5. [`dsmart-module-converter-widget`](dsmart-module-converter-widget/README.md) - модуль с фронтендом приложения 
6. [`dsmart-module-converter-angle-app`](dsmart-module-converter-angle-app/README.md) - модуль с приложением иммитатора углов
7. [`dsmart-module-converter-mock-app`](dsmart-module-converter-mock-app/README.md) - модуль с приложением иммитатора плавки
8. [`dsmart-module-converter-mock-widget`](dsmart-module-converter-mock-widget/README.md) - модуль с фронтендом приложения иммитатора плавки
9. [`dsmart-module-converter-ws-models`](dsmart-module-converter-ws-models/README.md) - модуль с транспортными моделями для web-socket'а
10. [`dsmart-module-converter-models-extevent`](dsmart-module-converter-models-extevent/README.md) - модуль с транспортными моделями для внешних событий
11. [`dsmart-module-converter-models-angle`](dsmart-module-converter-models-angle/README.md) - OpenAPI модуль с транспортные модели для углов (Kotlin)
12. [`dsmart-module-converter-models-meta`](dsmart-module-converter-models-meta/README.md) - OpenAPI модуль с транспортными моделями для меты (Kotlin)
13. [`dsmart-module-converter-models-metapy`](dsmart-module-converter-models-metapy/README.md) - OpenAPI модуль с транспортными моделями для меты (Python) 
14. [`dsmart-module-converter-models-mlui`](dsmart-module-converter-models-mlui/README.md) - OpenAPI модуль с транспортными моделями для данных из матмодели (Kotlin)
15. [`dsmart-module-converter-models-mluipy`](dsmart-module-converter-models-mluipy/README.md) - OpenAPI модуль с транспортными моделями для данных из матмодели (Python)
16. [`dsmart-module-converter-models-viml`](dsmart-module-converter-models-viml/README.md) - OpenAPI модуль с транспортными моделями для данных из видеоадаптера (Kotlin)
17. [`dsmart-module-converter-models-vimlpy`](dsmart-module-converter-models-vimlpy/README.md) - OpenAPI модуль с транспортными моделями для данных из видеоадаптера (Python)

## Переменные окружения

| Переменная | Описание | По умолчанию | Пример |
|:----------:|:--------:|:------------:|:------:|
|CONVERTER_ID|Идентификатор конвертера|converter1|converter9| 
|EVENT_MODE|Режим рекомендаций (по металлу и по шлаку)|STEEL|STEEL или SLAG| 
|STREAM_RATE_POINT_CRITICAL|Критическое значение процента содержания (металла или шлака) в потоке при сливе конвертера|0.15|0.17|
|STREAM_RATE_POINT_WARNING|Норма процента содержания (металла или шлака) в потоке при сливе конвертера|0.1|0.05|
|DATA_TIMEOUT|Время (в миллисекундах), через которое данные, пришедшие из кафки, считаются неактуальными (на UI появляются прочерки)|3000|5000|
|MELT_TIMEOUT|Время (в миллисекундах), через которое плавка завершается, если данные из видеопотока больше не приходили|10000|7000|
|REACTION_TIME|Время (в миллисекундах) реакции пользователя на рекомендации|3000|5000|
|SIREN_LIMIT_TIME|Время (в миллисекундах), через которое сирена перестаёт играть|3000|10000|
|ROUNDING_WEIGHT|Вес подсчёта усреднённого значения содержания в потоке|0.1|0.05|
|EVENT_REPOSITORY_STORAGE_DURATION|Длительность (в минутах) хранения данных событий в репозитории in-memory|10|15|
|STATE_REPOSITORY_STORAGE_DURATION|Длительность (в часах) хранения данных текущего состояния в репозитории in-memory|2|3|
|STATE_REPOSITORY_TIME_LIMIT|Промежуток времени (в секундах), за который выдаются последние значения шлака и стали|60|75|
|<b>KAFKA_BOOTSTRAP_SERVERS</b>|Список пар хостов и портов брокеров kafka, который разделяется запятыми. Хосты и пары указываются через ':'|-|172.16.0.1:9092,172.16.0.2:9092,172.16.0.3:9092|
|KAFKA_CLIENT_ID|Идентификатор потребителя kafka|ui-client-kafka|client-1|
|KAFKA_GROUP_ID|Идентификатор группы потребителей kafka|ui-converter|group-1|
|<b>KAFKA_TOPIC_META</b>|Топик kafka с мета-данными о плавке|converter-meta|converter-meta|
|<b>KAFKA_TOPIC_MATH</b>|Топик kafka с данными из матмодели|converter-math|converter-math|
|<b>KAFKA_TOPIC_VIDEO</b>|Топик kafka с данными из видеоадаптера|converter-video|converter-video|
|<b>KAFKA_TOPIC_ANGLES</b>|Топик kafka с данными из адаптера углов|converter-angles|converter-angles|
|<b>KAFKA_TOPIC_EVENTS</b>|Топик kafka с данными о внешних событиях|converter-events|converter-events|
|FRAMES_BASE_PATH|Путь до директории с кадрами видео в файловой системе сервера|frames/|path/to/frames|
Примечание: 
1. Переменные выделенные жирным шрифтом являются обязательными для корректной работы приложения
2. Дополнительные переменные см. в [datana-smart-logger](https://gitlab.dds.lanit.ru/datana_smart/datana-smart-common/datana-smart-logging#%D0%BF%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D0%BA%D1%80%D1%83%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-%D1%84%D0%B0%D0%B9%D0%BB%D0%B0-logbackxml)


