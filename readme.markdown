1. добавить lig4j2 или аналог для логирования
2. мало dto, лобавить минимум сущностей хотя бы до 5 
3. for (ServiceDTO s : services) переписать for на .Steam
4. диаграммы 
---
5. lombok, добавить на dto @Etity,что-то про get будет
6. ServiceDTO service = findServiceByName(serviceName);
   if (service == null) return "Ошибка: услуга не найдена.";
так не проверяется б почитать и исправить на options
7. В return null не возвращают
8. обернуть каждый метод в try/catch написать тесты в junit