# `dsmart-module-converter-models-metapy`

Модуль для генерации транспортных моделей для передачи данных из матмодели на
UI. Генерация из [openapi спеки](../spec-converter-MlUi.yaml).

Pypi-пакет генерируется и публикуется на корпоративном Nexus сервере.

Для подключения пакета требуется добавить его в `requirements.txt`, а также
указать репозитарий. На текущий момент это [https://nexus.datana.ru/repository/datana-pypi/](https://nexus.datana.ru/repository/datana-pypi/).
В дальнейшем этот адрес может измениться. Актуальная версия задается в gitlab-ci.

После подключения пакета, сериализацию и десериализацию объектов
можно выполнять как показано в примере: [example.py](example.py)
 
