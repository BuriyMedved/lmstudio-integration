# lmstudio-integration
Запуск lm studio
lms server start
  
Порт lms по умолчанию 1234

Для разработчика конфигурацию нужно хранить в папке dev-config. Для запуска с конфигами из этой папки указать параметр
-Dspring.config.additional-location=optional:file:./dev-config/
Папка находится в .gitignore чтобы случайно не залить в репозитарий.
Урл для чата http://localhost:8080/chat